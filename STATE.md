# Current repository state

## Current objective

Fix the Home-button app-launch return bug on an isolated branch targeting staging. Fork setup is complete; the current patch and validation limits are recorded in `STREAM.md` and `docs/HOMESCREEN_BUG.md`.

## Confirmed current state

- Fork: https://github.com/artinflight/Kvaesitso; upstream: https://github.com/MM2-0/Kvaesitso.
- Imported upstream main: `3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d` (4,303 reachable commits at setup).
- Canonical MCP clone: `/mnt/vk-storage/projects/Kvaesitso` on mounted `/mnt/vk-storage`; source clone measured 114 MiB before docs adoption.
- Vibe project: [Kvaesitso](https://vibe.local/projects/4bec76f9-8e07-4ddf-9d02-1e79313e48f6); repository ID: `9886ae3c-5eb5-4412-9711-94054eb81014`; default task target: `staging`.
- Playbook source: `artinflight/ops-playbook`, commit `047d8de11d5bf9c9328fac5d5c12e49ea9ea55dc`, baseline 0.1.0. Pinned standards/templates and hashes are recorded in `.ops-playbook.json`.
- Application version remains 1.41.0 / versionCode 2026091500 in `app/app/build.gradle.kts`. The Home-button patch leaves application and release version metadata unchanged.
- GPL notices, copyright, module layout, wrapper/SDK declarations, application IDs, signing configuration, assets and inherited workflows are preserved. The Home-button task adds only UI test dependencies to build configuration; production dependencies are unchanged.

## Decisions in force

Normal work uses isolated task branches from `origin/staging` and PRs to `staging`. Main remains the upstream baseline until separate promotion work is requested. Do not rewrite history or create release tags during setup.

## Deferred and known gaps

- The operator authorized our fork installation, default selection, Home-to-Obsidian setup/testing, copying the original configuration, and resuming S25 work after a pause. Our default-debug 1.41.0 / 2026091500 is installed alongside the original 1.39.3-fdroid. Background import and re-export verification confirm all 310 saved-item records, 14 widget records, 12 customizations and 11 search actions match the original backup; Home still targets Obsidian. Ordinary runtime permission parity is verified. Ten Android widget bindings, notification access and all-files access remain tied to the original package. The old Outfit font setting is unsupported by the newer baseline. The original release remains default HOME while migration is unfinished. After the earlier locked readiness check, two live Home/Obsidian short/long return cycles passed at 14:58 UTC. A guarded launcher-only capture shows clock and Search restored without another Home press. The later touch check lost the required foreground before its first cycle and is inconclusive; remaining UI setup and full acceptance are unfinished. Temporary diagnostic packages were removed.
- At initial check, system free space was about 11 GiB and SSD free space about 4.3 GiB. Recheck live capacity before future build preparation; these figures are historical.
- No fork branch-protection ruleset, automated branch-policy check or new hosted CI is configured. These are documented rules, not a claim of server enforcement. GitHub listed zero active fork workflows during setup; inherited files remain intact and Actions settings were not changed.
- Native versioning is retained instead of adding conflicting `VERSION.json` metadata. Pinned toolchain compatibility is verified for default-debug app and UI test APK assembly on Desktop B:. Release/nightly/F-Droid-flavor builds and emulator testing are not claimed. Lint reports 22 errors in unchanged files; see the task report.

## Next safe step

Resume with an unlocked, available phone under existing task authorization; do not ask for installation/default-selection approval again. Reconnect the copied third-party widgets through Android's supported binding/configuration flow, preserve their original provider selections, restore notification and all-files access through the normal Android flow, resolve the old-font appearance difference, revalidate restored records after access setup, and finish the Home/Obsidian acceptance matrix, especially touch responsiveness. Keep the original app/data/widgets intact as fallback and keep the PR draft. The completed data import must not be blindly repeated from the old snapshot. Preserve the debug signing key for compatible future updates. See `HANDOFF.md` and `docs/HOMESCREEN_BUG.md` for receipts and remaining limits.
