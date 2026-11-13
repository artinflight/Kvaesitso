# Native Android review

This repository is a native Android launcher. There is no browser preview, preview URL, server port, background service, tunnel, or preview stop command. Setup does not launch an emulator or access a phone.

The owner deferred Android builds and large dependency downloads. Do not invoke `gradlew`, even for task discovery, during this setup. A future development task should first check disk space and the SSD mount, inspect the pinned wrapper/SDK/toolchain declarations, and arrange cache/output storage on mounted secondary storage. No toolchain compatibility claim has been validated.

Static inspection finds the `default` and `fdroid` flavors and `debug`, `release`, and `nightly` build types in `app/app/build.gradle.kts`. The expected debug assembly task is `:app:app:assembleDefaultDebug`; it has not been executed or discovered through Gradle. Release/nightly signing and external API secrets are not configured for this fork.

Device install, launcher selection, permission changes and runtime testing require separate task authorization. For S25 work, first read `/home/mcp/code/Operations/docs/s25-adb-access.md` and run `/home/mcp/.local/bin/s25-adb status`. Reuse verified existing authorization without resetting pairing, radios, servers, or listeners. Never change the default launcher implicitly.

Future device review should record the exact commit, variant, device identity, install authorization, observed behavior and any limitations. Publish only intended review artifacts through the shared artifact helper. Report `Preview URL:: Not Generated` while no review artifact exists.
