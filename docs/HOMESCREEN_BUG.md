# Home-button app-launch return bug

## Scope and evidence

Task: KV::Homescreen Bug, 2026-11-13. The operator reports Home opening the configured app (Obsidian), then returning to a blank, unresponsive launcher until Home is pressed again.

The supplied 10.698-second recording was decoded locally. At the start the clock and search control are visible. Around 1–2 seconds those controls disappear; wallpaper remains through approximately 9 seconds despite touch indicators. Around 10 seconds the controls return. Obsidian itself is not visible in the sampled recording. This confirms the visible symptom, not the installed code version or its cause.

The clean task branch `vk/0b85-kv-homescreen-bu` started at freshly fetched `origin/staging`, `6d6dc32e96348bceb1be45ff6597607b74b8c4dc`, in `/mnt/vk-storage/worktrees/0b85-kv-homescreen-bu/Kvaesitso` (host alias `/home/mcp/code/worktrees/0b85-kv-homescreen-bu/Kvaesitso`). Baseline app version: 1.41.0 / 2026091500.

Source already includes upstream commit `b0765ceed`, which registers the Home-button component for rendering. An older installed app could exhibit that separate, already-fixed empty-page bug. Wireless ADB subsequently confirmed the installed version as 1.39.3-fdroid / 2026012400. Do not claim this patch is a device-confirmed fix for the recording; the installed app predates multiple upstream fixes.

## Changes

- Include `Gesture.HomeButton` in depth-based tap animation progress. Previously it fell into the swipe path, setting depth to zero during predictive Back rather than following partial progress.
- Mark app-launch splash components for immediate reset on resume. Clear such a page before Home-intent handling, including when restored from saved state with no usable pause timestamp. Previously the ordinary reset depended on a pause longer than five seconds; shorter returns/restoration could retain the temporary page. Retain the existing delayed launch cleanup.
- Preserve the existing resume policy for ordinary pages, including feed/search behavior and locked-page handling.
- Add seven Android instrumentation tests against the actual scaffold state: tap progress including Home, partial predictive Back, transient Home-page restoration with immediately usable swipe input, transient swipe-page restoration, ordinary-page preservation, Home-page dispatch eligibility, and swipe progress.

These address source-level defects and a restoration gap. Their connection to this installed app's symptom still needs runtime confirmation.

## Validation

Completed:

- `git diff --check` passes; `git fsck --full` exits successfully (dangling blobs, not object corruption).
- Wireless ADB verifies the installed version, expected S25 identity, existing default HOME role and actual Tailscale route; official-update and installed-executable signatures match.
- The pinned Android toolchain was prepared on Desktop B:, keeping caches, SDK, source and outputs off the system drive.
- Initial code commit `9d1736d76`: `:app:app:assembleDefaultDebug`, `:app:ui:lintDebug`, and `:app:ui:assembleDebugAndroidTest` completed with exit 0 in 39m 1s (1,962 actionable tasks). The MCP SSH caller timed out earlier, but reconciliation proved that its task-owned Windows job continued and completed. No duplicate build was started.
- Lint is **not clean**: 22 errors, 78 warnings and 13 hints. The repository sets `abortOnError = false`. Exact source-blob comparison shows every error is in a file unchanged from staging; modified launcher files have no errors. Error IDs: NewApi (1), NonObservableLocale (5), ContextCastToActivity (1), LocalContextGetResourceValueCall (14), SuspiciousModifierThen (1). No unrelated source cleanup is included.

Initial device state-test execution crashed before running cases because the test APK omitted `androidx.test.runner.AndroidJUnitRunner`. Its temporary package was removed, and the original launcher version, default HOME role and native ADB server were verified unchanged. Commit `fc95b0546` adds the explicit `androidx.test:runner:1.6.2` dependency and catalog alias; it changes no production code, production dependency or toolchain version. The corrected app and test APK builds completed at commit `fc95b0546` with exit 0 in 8m 4s. All seven state tests then passed on the identity-verified S25 through the existing Tailscale ADB route (`OK (7 tests)`, 0.033 s, receipt 2026-11-13 13:03:57 UTC). The self-targeting package `de.mm20.launcher2.ui.test` was removed afterward. The installed launcher remained 1.39.3-fdroid / 2026012400; its default HOME role and existing native ADB server were verified unchanged. These state tests do not constitute a visual Home/Obsidian acceptance test.

Not completed: visual Home/Obsidian acceptance. The operator subsequently selected our fork; installation and startup succeeded as recorded below. Explicit default-launcher selection and gesture-configuration approval remain pending. The official update was not installed.

The build task names above are now verified by execution. Device instrumentation uses the existing Desktop native ADB server and fresh helper proof; do not start a new Linux ADB server or switch launcher defaults. Main app version remains 1.41.0 / 2026091500.

## Runtime acceptance checks

1. Record installed version, exact candidate commit, build variant and device identity.
2. With Obsidian assigned to Home, press Home from the normal launcher. Confirm Obsidian opens once.
3. Return using Home after short and long app visits. Confirm clock/search/widgets appear immediately and swipes/taps work without an extra Home press.
4. Repeat with Back and recents, including a quick return and several launch/return cycles.
5. Recreate the launcher while the launch page is saved. Confirm return clears the splash without launching Obsidian again.
6. From a Home-opened internal page, partially perform and cancel predictive Back; confirm continuous progress and usable controls. Complete Back and confirm the normal home page.
7. Verify unassigned Home, Home-to-search/widgets, swipe/double-tap/long-press app launch, and locked widget editing retain their normal behavior.

## Resource/cost audit

Local source editing and existing GitHub fork resources suffice; no paid service, hosted runner, new recurring resource, workflow activation, or deployment is used. Fork Actions inspection returned zero workflows. No merge or main promotion is included.

Recording inspection used one no-cost, 35 MB PyAV wheel download, installed without dependencies or a pip cache under `/mnt/vk-storage/kv-homescreen-bug-20261113/tools`; decoded contact sheet is in that same task directory. These are local diagnostic intermediates, not published deliverables. They may be removed after review; no credentials are stored there. This temporary media tool is unrelated to the deferred Android dependency preparation.

## Wireless ADB follow-up — 2026-11-13

The operator explicitly authorized Wireless ADB through Tailscale to check the version and proceed with validation. The shared runbook and maintained S25 guide were read first. `s25-adb status` passed with exact SM-S936W and the expected hardware serial identity, a live phone listener, the Desktop Tailscale route, and an established socket owned by existing native server PID 32268. A second independent status passed through 12:01:35 UTC. Wi-Fi was enabled; this is Tailscale-over-Wi-Fi proof, not cellular proof. No server/radio/listener/pairing/security changes were made.

Read-only primary-user package metadata confirmed:

- Package/default HOME role: `de.mm20.launcher2.release`.
- Installed version: `1.39.3-fdroid`, code `2026012400`, target SDK 36.
- Installer: `org.fdroid.fdroid`; last update 2026-02-21.

The preserved `v1.39.3` source locks the scaffold in `performTapGesture`, starts the external app in `LaunchComponent.onActivate`, then delays 500 ms before clearing the secondary page and unlocking. This runs inside the RESUMED lifecycle block, so pausing to open the app can cancel the remaining cleanup. The subsequent return skips the ordinary reset while locked; another Home can dispatch Back and restore it. This is a source-supported explanation matching the report, not a completed device reproduction. Upstream `b07166874` replaces the transition with a non-cancellable lifecycle and app-local cleanup, and is included in `v1.41.0` and the fork baseline.

The phone had been unlocked during metadata inspection but was locked by the reproduction attempt (`showing=true`). Four Home key events while locked produced no resumed-app transitions and do not constitute a reproduction. No unlock gesture, credential input, screenshot or app-private-data read was performed. The installed launcher was not replaced, and its default role was preserved. A separate diagnostic test package was subsequently installed, tested and removed as recorded above. Further interactive testing is waiting for operator readiness.

### Prepared official update

The official [F-Droid listing](https://f-droid.org/en/packages/de.mm20.launcher2.release/) offers 1.41.0-fdroid / 2026091500. Downloaded only for inspection from `https://f-droid.org/repo/de.mm20.launcher2.release_2026091500.apk`. Android `apksigner` verified both v2 and v3 signatures. A copy of the installed executable was inspected on Desktop B: (no app-private data). Both old and new APKs have the same signer certificate SHA-256:

`85192966b7cfc30a2708845066267dbd312e42555631fb719ba50bfd923179c3`

Official update APK SHA-256: `a83264c6fb437aad083c61206a4e79dba555300dbd4578fa17c0c43f55798739`. Installed executable SHA-256: `7a7ee3516f854c700e4e9add50369db5427c9ffb92571d7ce03a5bc90ea0de65`. Package name and signer are compatible with an in-place update; settings retention and post-update behavior still need verification. The operator subsequently chose our fork instead, to support future modifications. The official update was not installed.

### Isolated native builds and evidence

MCP SSD remains low on space. Desktop B: is a separate internal 4 TB drive with roughly 572 GiB available. A task-only build directory is `B:\kv-homescreen-bug-20261113`; no source/dependency/output bulk is stored on the Desktop system drive. Source archive is the exact committed `9d1736d7600ae690b9937f4442c59c6f269e8d65`, SHA-256 `34c311951d8f5f3db59c8c92ee874844ccb3f914b678febd51697a71c15352c9`. The archive excludes local attachments and private data.

Build driver sets process-local Java 17, Gradle cache, Android SDK/user-home, and temporary directories; uses two workers, no parallel builds, no persistent Gradle daemon, and the repository's pinned wrapper/SDK/plugin versions. [Android's AGP 9.4 compatibility table](https://developer.android.com/build/releases/agp-9-4-0-release-notes) confirms Java 17, Gradle 9.6.0 and Build Tools 36.0.0. The existing host JAVA_HOME was stale, so the driver selects the actual Java installation only in its process. Host PowerShell script execution is restricted; the reviewed build script runs with process-only execution permission, without changing persistent policy. The initial combined command completed in 39m 1s; its caller timeout did not end the task-owned Windows process, so that process was reconciled before further work. It completed successfully while a scoped stop was being reconciled. The corrected test-only rebuild then used four workers and a 4 GiB heap, completing in 8m 4s. No overlapping builds were started. Build workers have exited; no new background service was installed.

The initial source archive was updated only with the two committed test-dependency files for `fc95b0546`. Production code is identical between the linted `9d1736d76` and device-tested `fc95b0546`; the latter adds only test-runner packaging. Full lint was not repeated for that test-only runtime dependency correction. Logs, SDK, caches, source extraction, copied executable and update APK are all under the task B: directory. This is a no-cost local validation route, with no hosted runner, paid service or recurring resource. Retain for task review, then remove only these task-owned intermediates when no longer needed.

The final test APK SHA-256 is `36a4b85dd6fcfa3883d67bec33b02104e5312e1a027f632938b13365b81824cd`. Its manifest was checked to target only its own `de.mm20.launcher2.ui.test` package, and the previously missing runner class was verified as defined in its DEX before the successful retry. The initial failed-run receipt is preserved separately.

Private local evidence is retained under `/mnt/vk-storage/kv-homescreen-bug-20261113` and `B:\kv-homescreen-bug-20261113`: `device-test-result.json`, `runner-build-status.json`, state-test log, lint XML and `lint-baseline-comparison.json` (all 22 error files compare byte-identical to staging). These are diagnostic intermediates, not published review attachments. Never reuse an old script's selector without fresh `s25-adb status` identity/route proof. No Android application version, signing configuration, SDK declaration, module layout or inherited workflow was changed.

## Authorized fork installation — 2026-11-13

The operator explicitly selected our build: "beyond that, I'm going to want to make other improvements/modifications, so let's install ours". Fresh `s25-adb status` proved the exact S25 identity, live listener and actual Tailscale route/socket through Desktop's existing native server. Wi-Fi was on; this is not cellular proof. No connection, pairing, radio, security or listener changes were needed.

The existing `defaultDebug` APK built from `fc95b05466741193eafc5c8861eb1dc1070e6091` was verified before installation. The five changed source/build files in the Desktop build snapshot match the current task-branch SHA-256 values. Manifest: `de.mm20.launcher2.debug`, 1.41.0 / 2026091500, min SDK 26, target SDK 37. Visible name: **Kvaesitso Debug**. APK SHA-256: `2605c4126fc885dfc4781380f254ddf5669a1758ffdd113d90986b968b9e8b5e`; verified signer certificate SHA-256: `732cc761deb6a1c4a447094cd0f9766ff96a03d48b9b6046103a74990704bef1`. This separate package has its own settings; the original app's configuration is not automatically transferred.

Native `adb install --user 0` returned Success. Receipt at 13:42:22 UTC confirms the fork's installed version and preservation of the original 1.39.3-fdroid version, original default HOME role and existing ADB server. There was no uninstall, data clear, replacement of the release app, permission grant or default-role change. A first command exceeded Windows command-line limits before executing; the reviewed script-file invocation then performed the single installation.

At 13:43 UTC the phone was unlocked. Explicit `am start -W` of the fork's LauncherActivity returned `Status: ok` and `LaunchState: COLD` (562 ms); the process was present and Android reported the fork as its resumed activity. No screenshot or app-private-data read was made. This checks startup only; it does not establish visual correctness or Home/Obsidian acceptance.

PREVIEW.md separately requires explicit authorization for launcher selection. The operator was asked whether to make Kvaesitso Debug default and configure its Home action to Obsidian. That answer is pending. The current HOME role remains `de.mm20.launcher2.release`. Once authorized and ready, complete the acceptance matrix above. The existing app offers **Backup and restore** for operator-controlled settings transfer, and **Gestures → Home button/gesture** for configuration. No settings backup or transfer has been performed.

Private receipts `fork-install-preflight.json`, `fork-install-result.json` and `fork-startup-result.json` are retained in the task's Desktop B: and MCP evidence directories. The required debug signing key is present at `B:\kv-homescreen-bug-20261113\android-user-home\debug.keystore`. Keep it private and preserve it for compatible future updates; do not delete that key as disposable build cleanup. No key was printed, exported or committed. The existing APK was reused without another build or paid service.
