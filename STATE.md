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

- Installed `95e5f7fe0` passes measured rapid/long Home–Obsidian returns and actual Search input. Ours is default HOME; original release/data intact. Fresh reconciliation preserves 310 saved records and assigns all 14 legacy widgets to Widgets1. OctoRemote/Codex Usage are connected; eight external widgets and Chrome shortcut functionality remain unfinished. Phone actions are explicitly paused because the operator needs the phone. See latest task report/HANDOFF; do not resume device use without readiness. Existing baseline lint errors remain.

- At initial check, system free space was about 11 GiB and SSD free space about 4.3 GiB. Recheck live capacity before future build preparation; these figures are historical.
- No fork branch-protection ruleset, automated branch-policy check or new hosted CI is configured. These are documented rules, not a claim of server enforcement. GitHub listed zero active fork workflows during setup; inherited files remain intact and Actions settings were not changed.
- Native versioning is retained instead of adding conflicting `VERSION.json` metadata. Pinned toolchain compatibility is verified for default-debug app and UI test APK assembly on Desktop B:. Release/nightly/F-Droid-flavor builds and emulator testing are not claimed. Lint reports 22 errors in unchanged files; see the task report.

## Next safe step

Wait for explicit phone readiness; no device actions while paused. Then inspect pending Weather configuration/current widget IDs, finish eight external bindings and verify Chrome shortcuts using supported flows. Home/touch acceptance passed on the installed final APK; preserve original data, current mapped widget IDs and signing key. Keep PR #2 draft pending complete migration/user acceptance. See `HANDOFF.md` and the latest `docs/HOMESCREEN_BUG.md` section.
