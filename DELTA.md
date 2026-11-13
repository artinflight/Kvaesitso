# Continuity ledger

## 2026-11-13 — fork setup

- Intent: establish a history-preserving artinflight fork and operational baseline.
- Completed: GitHub fork, 114 MiB full clone on mounted SSD, origin/upstream remotes, staging seed, Vibe repository/project link and default target, pinned playbook documentation.
- Files: root operations docs, `docs/FORK_SETUP.md`, `docs/ops-playbook/`, `.ops-playbook.json`, appended section in `readme.md`.
- Baselines: upstream `3faf4d01c3b4a7ea82b5e56b8d1b628f4145e10d`; playbook `047d8de11d5bf9c9328fac5d5c12e49ea9ea55dc`.
- Verification: source/history, manifest, docs and Vibe checks recorded in `docs/FORK_SETUP.md` before publication.
- Deferred: all Android builds, dependencies, device operations, native runtime verification, hosted policy enforcement and releases.
- Next: review setup, then choose the first development task from staging.

## 2026-11-13 — Home-button app-launch return fix candidate

Task branch `vk/0b85-kv-homescreen-bu` starts at fresh staging `6d6dc32e9` in the dedicated SSD worktree. Added Home-button depth progress and immediate resume cleanup for temporary launch pages, plus seven Android instrumentation tests. Recording confirms disappearing controls; installed version is unconfirmed and baseline already includes upstream `b0765ceed`. Static checks pass; Android build/lint/tests/device validation remain pending due absent pinned toolchain and low SSD capacity. No device action, version change, paid service, workflow activation or merge. See `docs/HOMESCREEN_BUG.md`, `STREAM.md`, and `HANDOFF.md`.

## 2026-11-13 — Wireless ADB version diagnosis and native validation

Operator authorized S25 Wireless ADB over Tailscale and next-step validation. Exact expected device identity/route verified; installed launcher is 1.39.3-fdroid / 2026012400. Its preserved source has cancellation-prone launch cleanup; upstream non-cancellable transition cleanup is already in official 1.41.0 and the fork baseline. Official F-Droid 1.41.0 was downloaded, signature-verified and matched to the installed executable; installation choice and unlock readiness are pending. No launcher update or default-role change.

Native app/test builds passed on isolated Desktop B: storage. Full lint on `9d1736d76` completed in the initial combined build (39m 1s) with 22 errors, 78 warnings and 13 hints; every error file is byte-identical to staging. Commit `fc95b0546` corrects a missing test-runtime runner dependency; it changes only two test-dependency files. Corrected app/test builds passed in 8m 4s. All seven scaffold state tests passed on the S25 at 13:03 UTC; temporary test package removed and original version, HOME role and native server preserved. No paid service or hosted workflow was enabled. Full evidence/remaining visual acceptance: `docs/HOMESCREEN_BUG.md`. Draft PR #2 targets staging; no merge/main promotion.
