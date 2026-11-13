# Fork setup stream

- Stream: `ops/bootstrap-20261113` targeting `staging`.
- Objective: fork registration and documentation-only Ops Playbook adoption.
- Isolated checkout: `/mnt/vk-storage/projects/Kvaesitso`; initially clean, based on unchanged `origin/staging`.
- In scope: operating docs, pinned baseline snapshot, provenance manifest, readme navigation, fork remotes, Vibe registration.
- Out of scope: Android code/build changes, toolchain/dependency downloads, device work, releases, upstream pushes and service restarts.
- Initial audit: upstream history/license/project files present; fork operating docs and staging/Vibe configuration absent. This stream supplies that baseline; hosted enforcement and native build verification are deferred.
- Status: documentation baseline complete; static source/history/manifest/Vibe checks passed and are recorded in `docs/FORK_SETUP.md`.
- Risk: copying generic web/version templates unmodified would misdescribe this existing Android app; root rules explicitly retain native preview/version behavior.
- Integration: publish and merge the setup PR into staging after static validation; keep main at the imported upstream commit. Future normal work starts in a new task worktree from staging.
