# Current repository state

## Current objective

Fix the Home-button app-launch return bug on an isolated branch targeting staging. Fork setup is complete; the current patch and validation limits are recorded in `STREAM.md` and `docs/HOMESCREEN_BUG.md`.

## Confirmed current state

- Fork: https://github.com/artinflight/Kvaesitso; upstream: https://github.com/MM2-0/Kvaesitso.
- Imported upstream main: `3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d` (4,303 reachable commits at setup).
- Canonical MCP clone: `/mnt/vk-storage/projects/Kvaesitso` on mounted `/mnt/vk-storage`; source clone measured 114 MiB before docs adoption.
- Vibe project: [Kvaesitso](https://vibe.local/projects/4bec76f9-8e07-4ddf-9d02-1e79313e48f6); repository ID: `9886ae3c-5eb5-4412-9711-94054eb81014`; default task target: `staging`.
- Playbook source: `artinflight/ops-playbook`, commit `047d8de11d5bf9c9328fac5d5c12e49ea9ea55dc`, baseline 0.1.0. Pinned standards/templates and hashes are recorded in `.ops-playbook.json`.
- Application version remains 1.41.0 / versionCode 2026091500 in `app/app/build.gradle.kts`. No app or release change is included.
- The existing GPL license, copyright, Android modules, Gradle files, assets and workflow files are preserved. Only a fork-operations section is appended to the original readme.

## Decisions in force

Normal work uses isolated task branches from `origin/staging` and PRs to `staging`. Main remains the upstream baseline until separate promotion work is requested. Do not rewrite history or create release tags during setup.

## Deferred and known gaps

- No Android build, Gradle invocation, SDK or dependency download, emulator, phone operation, app installation, signing setup or runtime validation.
- At initial check, system free space was about 11 GiB and SSD free space about 4.3 GiB. Recheck live capacity before future build preparation; these figures are historical.
- No fork branch-protection ruleset, automated branch-policy check or new hosted CI is configured. These are documented rules, not a claim of server enforcement. GitHub listed zero active fork workflows during setup; inherited files remain intact and Actions settings were not changed.
- Native versioning is retained instead of adding conflicting `VERSION.json` metadata. Android toolchain/task compatibility is unverified.

## Next safe step

Review the Home-button fix candidate, confirm the installed app version, and prepare adequate SSD capacity and the pinned Android toolchain for build/lint/instrumentation checks. Runtime validation is pending; do not merge on static checks alone. No phone installation or launcher-default change is included. See `docs/HOMESCREEN_BUG.md` for the acceptance matrix and `docs/FORK_SETUP.md` for the earlier setup evidence.
