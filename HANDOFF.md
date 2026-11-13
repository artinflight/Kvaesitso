# Handoff

The fork is `artinflight/Kvaesitso`; upstream is `MM2-0/Kvaesitso`. The canonical clone is `/mnt/vk-storage/projects/Kvaesitso`. Vibe project ID is `4bec76f9-8e07-4ddf-9d02-1e79313e48f6` and repository ID is `9886ae3c-5eb5-4412-9711-94054eb81014`; task PR target is staging.

Read `AGENTS.md`, `STATE.md`, `STREAM.md`, `PREVIEW.md`, and `docs/FORK_SETUP.md`. Ops Playbook adoption adds docs and a pinned source manifest while preserving the imported Android source and GPL terms. The original readme has one appended operations section. Native app versioning remains in `app/app/build.gradle.kts`.

The next agent should inspect current Git/remote/Vibe state, create a separate task worktree from fresh origin/staging, and follow the chosen task. Do not run Gradle, fetch Android dependencies, install on S25, promote main, activate inherited workflows, or create releases merely to finish setup. Build and runtime checks are deliberately deferred; check live disk capacity first. No paid service was enabled, no dependency cache was created, and no device was accessed.

Session: fork setup, 2026-11-13 UTC. Validation and publication evidence: `docs/FORK_SETUP.md` and the staging PR.
