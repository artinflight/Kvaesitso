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

Not completed: visual Home/Obsidian acceptance. The operator subsequently selected our fork; installation and startup succeeded as recorded below. Selection/configuration authorization was subsequently granted; the migration follow-up below records the current remaining work. The official update was not installed.

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

PREVIEW.md separately requires explicit authorization for launcher selection. At this stage the operator was asked whether to select/configure the fork. They subsequently answered Yes; the migration section below supersedes the earlier pending-approval state. The current HOME role remains `de.mm20.launcher2.release`. Once authorized and ready, complete the acceptance matrix above. The existing app offers **Backup and restore** for operator-controlled settings transfer, and **Gestures → Home button/gesture** for configuration. No settings backup or transfer has been performed.

Private receipts `fork-install-preflight.json`, `fork-install-result.json` and `fork-startup-result.json` are retained in the task's Desktop B: and MCP evidence directories. The required debug signing key is present at `B:\kv-homescreen-bug-20261113\android-user-home\debug.keystore`. Keep it private and preserve it for compatible future updates; do not delete that key as disposable build cleanup. No key was printed, exported or committed. The existing APK was reused without another build or paid service.

### Unresolved-problem follow-up

The operator subsequently reported "Problem isn't resolved". Fresh helper identity and live Tailscale proof passed. Read-only package/role inspection at 13:53 UTC confirmed `de.mm20.launcher2.release` still owns HOME and remains 1.39.3-fdroid / 2026012400. Our `de.mm20.launcher2.debug` 1.41.0 / 2026091500 is installed separately. The phone was locked (`showing=true`, `inputRestricted=true`), with no resumed activity reported. The retained receipt is `unresolved-inspection.json` in both private task evidence directories. An absent-process-tolerant read corrected an initial diagnostic command's empty-result exit handling; no mutation occurred.

Home therefore still routes to the original app. This explains why installing the separate fork alone has not changed Home behavior; it does not prove the patch will resolve the freeze. The operator was informed and asked to authorize making Kvaesitso Debug default and configuring Home to Obsidian, or to select it manually, with unlock readiness. No new code, role, permissions or settings were changed during this inspection. Live acceptance remains required; the PR stays draft.

## Original-configuration transfer and resumed validation

The operator explicitly authorized default selection/configuration/testing, then requested copying everything from the original installation. After seeing the fresh separate build, the original release was restored as default at 14:03 UTC. No original app data was cleared. Both versions use backup format 1.9. The original app's Backup UI completed a full backup at 14:06 UTC; phone and private Desktop B: copies matched SHA-256 `5d6271dc483f5384a736d294bbd9a0c08f05511077778632e0f1babafddce3f1`. The operator paused phone use, then explicitly resumed it. Fresh exact S25/cellular Tailscale proof passed through 14:45 UTC without changes to ADB transport settings, pairing, radios, listeners or the existing server.

Lock-screen interruptions prevented completing the UI import. A temporary instrumentation helper was compiled with the existing cached JDK/Android tools and signed with the fork's existing debug key. Its manifest targets only `de.mm20.launcher2.debug`. It verifies package/version and the input hash, invokes the installed app's actual BackupManager.restore and backup methods, and compares exported records in memory. It records an applied marker to avoid blindly repeating the full restore. The helper has no activity or requested permissions and was removed after each run. Source, APK and sanitized receipts are retained privately in the task directories; no helper or migration code was added to the production app.

Initial import completed at 14:30 UTC. Re-export comparison found optional search-action icon/color values being changed from absent/null to zero by the existing importer. A bounded correction at 14:45 UTC required the applied marker, skipped full restoration, and used the existing Room backup DAO to preserve the original values. The final comparison reports:

| Backed-up category | Original | Restored | Record contents |
| --- | ---: | ---: | --- |
| Saved items | 310 | 310 | All match |
| Widget layout/configuration records | 14 | 14 | All match |
| Customizations | 12 | 12 | All match |
| Search actions | 11 | 11 | All match |
| Custom colors/shapes/transparencies/typographies | 0 each | 0 each | All match |

The source contains zero asset files; asset comparison passes. Home's configured Obsidian action matches. Five ordinary runtime permissions already granted to the original (calendar, contacts, coarse/fine location and local-network access) were carried over under the configuration-transfer request. The final helper reports no missing ordinary runtime permissions. Accessibility remains disabled for both; notification access was not changed. Original app version, default HOME and all original widget bindings remain preserved.

This is **not a complete usable-setup transfer**. Ten external Android widget IDs still belong to the original host and cannot be displayed by the separate fork. Their provider metadata was identified without reading widget contents or modifying provider state. They need supported binding/configuration in the new host, including preserving their original selections. Android's [widget-host implementation](https://android.googlesource.com/platform/frameworks/base/+/master/services/appwidget/java/com/android/server/appwidget/AppWidgetServiceImpl.java) enforces ownership; [restore broadcasts](https://developer.android.com/reference/android/appwidget/AppWidgetManager#ACTION_APPWIDGET_RESTORED) are system-protected and were not forged. The app's existing Replace flow preserves copied layout/size/style while selecting a new bound widget. Original notification access is enabled while fork notification access is not; it still needs the normal Android flow. A subsequent read-only app-ops comparison found original all-files access at UID allow versus fork default, so file-search access also needs its normal Android permission screen. Installer access is default for both; no special app-op/security setting was changed.

Settings comparison also records the removed `uiFont` field: original Outfit is not reproduced by this newer baseline, which intentionally replaced that setting with Google Sans in upstream `8ba55f834`. This appearance difference remains unresolved, rather than silently claiming everything was copied. The explicit swipe-up `Widgets1` target is the default (`WidgetScreenTarget.Default`), and schema 5 to 6 migration is expected. All other original settings values compare equal after accounting for those documented differences; the conservative `exactConfigurationMatch` flag remains false.

A bounded live-test readiness window ended at 14:48 UTC with the phone still locked. Home/Obsidian UI testing was skipped before changing the default or sending UI input. That skipped attempt produced no visual or responsiveness evidence; the later live results below supersede its test status. Original release remains HOME while the remaining setup is incomplete. Continue under existing authorization once the phone is actually unlocked; do not repeat installation/default-selection permission questions or blindly rerun the stale import.

Private backup storage: `B:\vk-backups\kvaesitso\20261113-home-migration`. Original and initial verification archives are retained there; the fork's private cache also retains migration inputs/results. Temporary private MCP comparison staging was removed after analysis. Sanitized receipts on MCP SSD and Desktop B: include `migration-helper-result-after-defaults-correction.json`, `migration-runtime-permissions.json`, `migration-normalization-analysis.json`, `original-widget-binding-metadata.json`, and `home-obsidian-live-test.json`. Latest helper APK SHA-256: `84ab1c04b7183d2a3624936de1f779d2f411a73cac399e46f2b54171e3b0f46b`; source SHA-256: `f6e7806b12c5f13312d2d5884a4bc0d2d2e3172470f3d583cae300a4dba0bf84`. Receipts verify helper removal and preservation of the original app/default. Do not publish backup payloads or credentials.

Cost/resource audit: existing local tools and secondary-drive capacity sufficed. No SDK/dependency download or production rebuild was needed for the private helper, no hosted runner or paid service was enabled, and no persistent helper/service was installed. Required signing material is preserved. Zero fork workflows were independently checked before the documentation push. Production app code remains `fc95b0546`; previous seven state tests and build evidence still apply, with their stated lint and visual-acceptance limits.

### Live Home/Obsidian return checks — 14:58 UTC

The phone subsequently became unlocked. Obsidian's actual launcher component matches the copied Home target. An initial fixed 650 ms startup check did not observe Obsidian; a later test allowed initial stabilization and used bounded foreground polling and completed two real Home-to-Obsidian-to-fork cycles after a short visit and a six-second visit. Search was present after both returns. A capture made only after verifying the fork's LauncherActivity foreground shows the normal clock and Search control restored, without an additional Home press. No Obsidian screen or note content was captured. The image is a private diagnostic artifact, not a published attachment.

The original default was restored in finally after the tests because widget/access migration remains unfinished. A follow-up test intended to tap Search after return stopped before its first cycle when the fork was not the required foreground; no query was entered, and its result does not prove touch responsiveness or a new app defect. Keep that check open. Remaining acceptance includes quick returns, Back/recents, recreation, predictive Back and preserved ordinary gestures as listed above. `home-obsidian-live-test.json` confirms the two successful cycles and original-default restoration; `home-obsidian-input-test.json` records the inconclusive foreground-guard stop.

Final private backup export was retained on Desktop B: at `fork-after-defaults-correction.kvaesitso`; its phone-cache/Desktop-copy SHA-256 matches `41e4059a1031ccbb94d30751d27a79a349d4471404ce28fde68815289a9d9d7c`. `migration-final-resource-audit.json` confirms temporary diagnostic packages absent, original HOME active, and the existing ADB server preserved. After finishing special access, revalidate current saved records against the original backup before declaring full transfer complete; the recorded exact data comparison predates subsequent live app use. No default-selection authorization is missing; actual unlocked/idle-phone readiness is needed for the remaining UI work.


## Continuing failure report — 22:18 UTC

The operator reports “Not fixed.” Fresh identity/route proof again passed over cellular Tailscale with the existing native ADB server. Read-only inspection confirms the original 1.39.3-fdroid / 2026012400 still handles HOME; our separate 1.41.0 / 2026091500 remains installed. Waze was foreground and was left untouched. Configuration migration and live acceptance remain unfinished. Current default ownership does not establish which build the operator used when experiencing the failure.

The earlier test's nominal short visit was delayed further by Desktop/ADB foreground polling; its receipt did not measure actual app dwell time. It therefore does not establish a rapid-return regression test. A private device-side timing/input script, `home-return-device-timing.sh`, now removes those network round trips between detection of Obsidian and the return Home press, records uptime timestamps, and checks that tapping Search opens an editable control. It requires the fork already selected and foreground, guards the lock/foreground state, and requires caller-owned temporary-default restoration while migration is unfinished. Host `sh -n` passes. The script has not been transferred to or executed on S25; no new runtime success is claimed. Idle-phone readiness remains pending, with existing installation/configuration/testing authorization preserved.


## 2026-11-14 — setup preservation and data retention

Operator said Ready. Exact S25/Tailscale/native-server proof passed at 01:26 and 02:19 UTC; Wi-Fi was enabled, so no new cellular claim. Phone relocked during settings launch and remained locked at 02:23. No new measured Home/Obsidian return or input test; original release remains HOME. Existing authorization is granted; actual unlocked/awake UI availability is the remaining gate.

### Installed source and validation

`6d3f9cdb9` restores optional Outfit with nine original upstream OTF weights, Compose family declaration and OFL attribution. Every restored asset matches retained upstream. A separate built-in typography preserves the former brand/plain pairing without changing the newer default. Two tests cover resource loading/selectability and backup serialization.

Audit found three missing Chrome shortcut records. The pre-update database already had 307 and lacked the same three; all retained entries were unchanged. Android documents regular launchers cannot query shortcuts pinned only by another launcher ([ShortcutQuery reference](https://developer.android.com/reference/android/content/pm/LauncherApps.ShortcutQuery)). Repository cleanup deletes null resolutions; the old deserializer returned null for empty/denied/locked queries. This is a retention defect; the exact prior deletion event was not logged. The existing no-host-permission branch already used an unavailable model and is not by itself the diagnosis.

`c89edbffb` retains an unavailable shortcut when its source app remains installed but lookup returns null/empty or temporarily fails. Removed source apps can still be cleaned up. Six tests cover these cases plus stable keys and serialization. Test-only `6ffb291b4` corrects a type assertion: unavailable state is on the shortcut subtype, not the base saved-item interface. Production source is unchanged by that correction.

Final `6ffb291b406e5e8a71d798f85ecbc89cf7cf77e1` completed `:app:app:assembleDefaultDebug`, `:app:ui:assembleDebugAndroidTest`, `:data:appshortcuts:assembleDebugAndroidTest` and `:data:appshortcuts:lintDebug` with exit 0 in 1m 6s. Fifteen S25 tests pass: six shortcut cases (0.013 s) and nine scaffold/font cases (0.044 s). Temporary test packages removed. App APK hash `36eab8f919737a5756519c2caf92aa6d165cdc166abbc173b17030d8c98b3547`, signer `732cc761deb6a1c4a447094cd0f9766ff96a03d48b9b6046103a74990704bef1`. In-place installation and installed-file hash verification completed at 02:21; neither launcher was uninstalled or cleared.

Lint is not globally clean. Shortcut module: 0 errors/7 warnings; themes: 0 errors/2 warnings. Broader reports at `6d3f9cdb9`: UI 22 errors/78 warnings/13 hints, core-base 9 errors/11 warnings. Every error file is byte-identical to staging. Later production delta is only shortcut deserialization, with its own lint/tests. Initial combined validation exited 1 on core-base lint despite completed app/test assemblies. A follow-up startup had a local Gradle socket bind collision; reconciled lint-only validation completed with process-local IPv4 preference, without changing host networking. No lint suppression, new dependency download or paid resource.

### Settings protection and missing-only restoration

The fork's current files/databases were backed up on Desktop B: before update, hash `f757d236689be2fb37f15c9d6357c817e812124db673324e238731e8d0ee5a4e`. An initial exec-in settings transfer truncated the last 42 bytes (4,608 versus 4,650). Readback caught this while the app stayed stopped. Recovery used only that current snapshot's settings, shell-v2 raw input, and temporary-file verification before atomic replacement. Final settings equal the snapshot byte-for-byte except the intended typography UUID digit selecting Outfit. No database restore; original untouched.

Original notification/all-files grants were copied with supported Android commands after checking the exact originals; all prior listeners were preserved. Immediate notification metadata lagged, so no second grant was issued. Later metadata and the fork's public NotificationManager/Environment access APIs confirm both enabled, alongside runtime permission parity. No accessibility/installer or transport/security setting was enabled.

A temporary read-only helper exported current data through BackupManager.backup without restore/repair. After installing the retention fix, a separate guarded helper selected only the three hash-identified Chrome records from the verified original archive and used Room INSERT-IGNORE. Exactly three were inserted; the other 307 were not replaced and Android shortcut ownership was unchanged. Final re-export verifies all 310 saved items, 14 widget layouts, 12 customizations and 11 search actions match the source backup. Home targets Obsidian, Outfit is selected, runtime grants match and both special-access APIs pass. All helper/test packages removed.

Final private export `B:/vk-backups/kvaesitso/20261114-home-migration/fork-after-shortcut-repair.kvaesitso` matches the on-phone hash `968cb08c8475f5fbcd2f27078cf0499d5c69b18629853030391143da61a787fe` (18,715 bytes). Original and pre-update backups remain on Desktop B:. Nine private comparison files on MCP SSD (568,150 bytes) were removed after archive verification, no-open-handle checks and consumer inspection; sanitized receipts remain. No backup or screen content published.

### Remaining acceptance

Record parity is not full migration. Ten widgets need normal binding/configuration, preserving provider choices and layout/style while mapping new host IDs; never activate physical controls as tests. Three retained Chrome records need launchability verification/recreation through supported flows. Unavailable records do not prove working shortcuts. Compare a fresh original export for intervening changes before activation; never reapply an old snapshot wholesale or overwrite mapped IDs.

Original 1.39.3-fdroid / 2026012400 remains HOME with its data/widgets/pins intact. Fork is installed/prepared, but no new Home/touch success is claimed. Finish migration, select ours under existing authorization and run measured rapid/long returns and input checks with the phone unlocked/awake. Keep PR #2 draft; no merge/version change/main promotion. Secondary-drive SDK/cache/output and signing key reused; no paid, recurring or persistent resource introduced.
