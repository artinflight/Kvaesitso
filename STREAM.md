# Home-button app-launch return fix

- Task: KV::Homescreen Bug, 2026-11-13; target `staging`.
- Branch/worktree: `vk/0b85-kv-homescreen-bu`, `/mnt/vk-storage/worktrees/0b85-kv-homescreen-bu/Kvaesitso` (host alias under `/home/mcp/code/worktrees`).
- Baseline: freshly fetched `origin/staging` at `6d6dc32e96348bceb1be45ff6597607b74b8c4dc`; initially clean. Native version remains 1.41.0 / 2026091500.
- Scope: launcher scaffold Home-button progress and temporary app-launch restoration; UI instrumentation tests and task documentation. No module/version/signing/application-ID changes, device operations, release, main promotion, or hosted workflow activation.
- Implemented: Home uses depth progress like other tap gestures; app-launch splash pages reset immediately on resume before Home dispatch, including saved-state restoration; seven instrumentation tests added.
- Validation: static patch/source/integrity checks pass. Android build, lint, instrumentation execution and device reproduction remain pending: pinned toolchain absent, only about 3.3 GiB SSD space available. Do not present static checks as Android test results.
- Evidence/risks: `docs/HOMESCREEN_BUG.md`. Recording confirms disappearing controls but does not establish installed version; source already has a separate upstream Home-only rendering fix. Operator version answer pending.
- Integration: draft PR to staging; validate the pinned Android build and runtime acceptance matrix before merge. Preserve setup history in `DELTA.md` and `docs/FORK_SETUP.md`.
