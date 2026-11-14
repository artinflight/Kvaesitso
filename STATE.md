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
- GPL notices, copyright, module layout, wrapper/SDK declarations, application IDs, signing configuration and inherited workflows are preserved. Original upstream Outfit assets/attribution are restored as an optional typography. Only test dependencies were added; production dependencies are unchanged.

## Decisions in force

Normal work uses isolated task branches from `origin/staging` and PRs to `staging`. Main remains the upstream baseline until separate promotion work is requested. Do not rewrite history or create release tags during setup.

## Deferred and known gaps

- Installation/default selection, copying the original setup and S25 testing are authorized, including resuming after a pause. Installed fork code is `6ffb291b4`, version 1.41.0 / 2026091500, alongside preserved original 1.39.3-fdroid / 2026012400. Fifteen state/compatibility tests pass. Outfit and original runtime/notification/all-files grants are restored. Audit found three Chrome shortcut records already missing before this update; every retained record was unchanged. A retention fix now preserves inaccessible shortcuts while the source app exists, and missing-only insertion restored those three. Final re-export verifies all 310 saved items, 14 widget records, 12 customizations and 11 search actions match the original backup. This is record parity, not functional completion: ten widget bindings remain with the original host, and three Chrome shortcuts need launchability verification/recreation. Original remains HOME fallback. Phone relocked during the Ready session and stayed locked; no new live Home/input test completed. Prior cycles do not prove rapid-return/touch acceptance. All temporary packages and private MCP comparison staging were removed.
- At initial check, system free space was about 11 GiB and SSD free space about 4.3 GiB. Recheck live capacity before future build preparation; these figures are historical.
- No fork branch-protection ruleset, automated branch-policy check or new hosted CI is configured. These are documented rules, not a claim of server enforcement. GitHub listed zero active fork workflows during setup; inherited files remain intact and Actions settings were not changed.
- Native versioning is retained instead of adding conflicting `VERSION.json` metadata. Pinned toolchain compatibility is verified for default-debug app and UI test APK assembly on Desktop B:. Release/nightly/F-Droid-flavor builds and emulator testing are not claimed. Lint reports 22 errors in unchanged files; see the task report.

## Next safe step

Resume with the phone unlocked and kept awake under existing authorization; do not ask for installation/default-selection permission again. Compare a fresh original export for intervening changes before final activation; do not reapply the old snapshot wholesale. Reconnect widgets through normal Android bind/configuration UI, preserving provider selections and layout/style while mapping new host IDs. Verify/recreate the three Chrome shortcuts through supported flows, preserving retained history/customizations. Finish measured rapid/long Home/Obsidian returns and actual input checks, with guarded launcher-only captures and no Obsidian content or physical widget actions. Preserve original fallback and the signing key; keep the PR draft until migration and live acceptance are complete. See `HANDOFF.md` and `docs/HOMESCREEN_BUG.md`.
