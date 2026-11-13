# Fork setup and verification

Setup date: 2026-11-13 UTC.

## Sources and registration

- Fork: https://github.com/artinflight/Kvaesitso
- Original: https://github.com/MM2-0/Kvaesitso
- Imported upstream commit: `3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d`; full history retained, without shallow or partial clone settings.
- Pinned Ops Playbook: https://github.com/artinflight/ops-playbook/tree/047d8de11d5bf9c9328fac5d5c12e49ea9ea55dc
- MCP clone: `/mnt/vk-storage/projects/Kvaesitso`; this is a source checkout, not a build/dependency cache.
- Vibe project: [Kvaesitso](https://vibe.local/projects/4bec76f9-8e07-4ddf-9d02-1e79313e48f6) (`4bec76f9-8e07-4ddf-9d02-1e79313e48f6`).
- Vibe repository: `9886ae3c-5eb5-4412-9711-94054eb81014`, default target `staging`; setup/dev/cleanup scripts remain unset.

Repository registration and target configuration use the live local API at the stable gateway. This Vibe version exposes projects read-only. Project/link creation therefore follows the established Build.local local registration method: a short SQLite transaction adding only the new project and link, after confirming current API project IDs and the registered repository path against the live database. No database replacement, migration, service restart or agent launch is involved. The API must read back the exact project and linked repository.

## Operating model and adaptations

`main` remains the imported upstream baseline; staging begins at the same commit. Documentation adoption takes an isolated setup branch and PR to staging. Future tasks use separate worktrees and PRs to staging. Main promotion is separate work. No history rewriting or license replacement.

The pinned standards/adoption/templates snapshot is installed under `docs/ops-playbook`, with its source commit and file hashes in `.ops-playbook.json`. Root operating docs adapt the generic templates to the existing Android app. The native canonical version remains `versionName = "1.41.0"` and versionCode 2026091500 in `app/app/build.gradle.kts`; a second `VERSION.json` is intentionally omitted. The original readme content is retained as a byte-identical prefix with an appended fork-operations section.

No web preview is appropriate for the native launcher. `PREVIEW.md` records the deferred native path and S25 access prerequisites. No Android command, SDK/Gradle/dependency fetch, device connection, app install, signing secret or paid service is needed for this setup. GitHub listed zero active fork workflows; inherited files and repository Actions settings remain unchanged. Do not enable schedules, docs deploys or external F-Droid triggers incidentally.

Branch policy is documented but no hosted ruleset/check has been created. Future automation is a separate scoped task after necessity/cost review. Cost audit: existing Git/GitHub and local Vibe resources suffice; no new paid or recurring resource is required or enabled.

## Reproducible setup checks

Run only static commands during this setup:

```sh
git remote -v
git rev-parse --is-shallow-repository
git fsck --full
git merge-base --is-ancestor 3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d HEAD
git diff --check 3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d HEAD
git diff --name-status 3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d HEAD
```

Require all upstream tracked blobs and modes to match except the appended readme section; compare against the pinned commit rather than a moving remote ref. Require all upstream tags and the crowdin branch tip to remain reachable locally. Check the manifest source/file hashes, root relative links and native version source. Use Vibe GETs to read back the project, linked clone path, remotes and staging target. Inspect GitHub fork parent, branch heads, PR and Actions runs. These checks establish repository setup, not Android toolchain compatibility or runtime behavior.

## Capacity and deferred validation

Initial free space: system about 11 GiB; mounted SSD about 4.3 GiB. Clone size before adoption: 114 MiB. Recheck live capacity with `df -h / /mnt/vk-storage` and `findmnt /mnt/vk-storage` before any future build preparation. Store bulky generated/dependency files on mounted secondary storage; do not create a second full clone for task worktrees.

Android builds, dependency downloads, CI execution, signing, emulator/device tests and app installation remain deferred by the owner's request. Static task-name inspection is not Gradle task discovery. No launcher defaults or device settings have changed.

## Verification result

Passed on 2026-11-13 before publication:

- `git fsck --full` and `git diff --check` pass; the clone is neither shallow nor partial.
- The imported main has 4,303 reachable commits. Both upstream branch tips and all 95 upstream tag refs match the fork. Main remains at `3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d`.
- Every original tracked path except `readme.md` matches the upstream baseline; original readme bytes remain an exact prefix. Android source/build files, modes, assets, workflow files and `LICENSE.txt` have no changes.
- All installed manifest hashes and root-doc relative links pass. The pinned standards/templates are copied from the committed playbook source, with trailing whitespace normalized in four inherited templates. Original source hashes, normalized paths and installed hashes are recorded in the manifest.
- The live Vibe API returns the exact project, repository link, SSD path, origin/upstream URLs, staging target and unset setup/cleanup/dev-server scripts. The operator project URL responds with HTTP 200.
- No `.gradle`, app build output, root build output or `local.properties` was created. GitHub reports zero Actions runs. No Android command, dependency fetch or device operation was performed.

The documentation setup is published through a PR to staging. The final PR/merge receipt and post-publication verification are recorded in the Operations task log; main is not promoted.
