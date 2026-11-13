# Home-button app-launch return bug

## Scope and evidence

Task: KV::Homescreen Bug, 2026-11-13. The operator reports Home opening the configured app (Obsidian), then returning to a blank, unresponsive launcher until Home is pressed again.

The supplied 10.698-second recording was decoded locally. At the start the clock and search control are visible. Around 1–2 seconds those controls disappear; wallpaper remains through approximately 9 seconds despite touch indicators. Around 10 seconds the controls return. Obsidian itself is not visible in the sampled recording. This confirms the visible symptom, not the installed code version or its cause.

The clean task branch `vk/0b85-kv-homescreen-bu` started at freshly fetched `origin/staging`, `6d6dc32e96348bceb1be45ff6597607b74b8c4dc`, in `/mnt/vk-storage/worktrees/0b85-kv-homescreen-bu/Kvaesitso` (host alias `/home/mcp/code/worktrees/0b85-kv-homescreen-bu/Kvaesitso`). Baseline app version: 1.41.0 / 2026091500.

Source already includes upstream commit `b0765ceed`, which registers the Home-button component for rendering. An older installed app could exhibit that separate, already-fixed empty-page bug. The installed version was requested and remains unconfirmed. Do not claim this patch is a device-confirmed fix for the recording.

## Changes

- Include `Gesture.HomeButton` in depth-based tap animation progress. Previously it fell into the swipe path, setting depth to zero during predictive Back rather than following partial progress.
- Mark app-launch splash components for immediate reset on resume. Clear such a page before Home-intent handling, including when restored from saved state with no usable pause timestamp. Previously the ordinary reset depended on a pause longer than five seconds; shorter returns/restoration could retain the temporary page. Retain the existing delayed launch cleanup.
- Preserve the existing resume policy for ordinary pages, including feed/search behavior and locked-page handling.
- Add seven Android instrumentation tests against the actual scaffold state: tap progress including Home, partial predictive Back, transient Home-page restoration with immediately usable swipe input, transient swipe-page restoration, ordinary-page preservation, Home-page dispatch eligibility, and swipe progress.

These address source-level defects and a restoration gap. Their connection to this installed app's symptom still needs runtime confirmation.

## Validation

Completed:

- `git diff --check` passes.
- `git fsck --full` exits successfully; it reports dangling blobs, not object corruption.
- Source review checked reset ordering, Home-before-gesture dispatch, default component behavior, swipe calculations, test configuration, and unchanged application/version/build declarations except the existing Android JUnit test dependency in the UI module.
- SSD mount verified. About 3.3 GiB remains free after recording inspection. No Android SDK, Gradle distribution, or Android dependency downloads were performed. Cached compiler directories inspected contain no compiler jars; the pinned Gradle 9.6.0 distribution is absent.

Not completed: Android compilation, lint, instrumentation execution, installation, or device reproduction. No Android runtime command or phone operation was performed. Static checks are not a build/test pass.

Before merge, prepare the pinned toolchain and adequate SSD capacity, then discover/verify and run the appropriate tasks. Expected commands (availability unverified):

```sh
./gradlew :app:app:assembleDefaultDebug :app:ui:lintDebug :app:ui:assembleDebugAndroidTest
./gradlew :app:ui:connectedDebugAndroidTest
```

Place downloads, dependency caches, temporary files, and build outputs on mounted secondary storage. Do not change pinned versions to fit an unrelated cached toolchain. Follow `PREVIEW.md` and the S25 access runbook; device install/testing and default-launcher changes need their own authorization. Do not install over the operator's launcher implicitly.

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
