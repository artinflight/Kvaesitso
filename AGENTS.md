# AGENTS.md

## Purpose

- Stable operating rules for agents in this repository.

## Required Read Order

1. `AGENTS.md`
2. `STATE.md`
3. `STREAM.md`
4. `HANDOFF.md`
5. Relevant code and validation paths
6. `DELTA.md` only when needed for compact history

## Authority Order

1. Code and validated behavior
2. `STATE.md`
3. `STREAM.md`
4. `HANDOFF.md`
5. `DELTA.md`

## Branch / PR Rules

- Never commit directly to `main` or `staging`.
- Never do normal feature, fix, docs, chore, or agent work in `main`.
- Start new task work from latest `origin/staging`.
- Normal task PRs must target `staging`, not `main`.
- Normal task agents must not merge both to `staging` and to `main`.
- Normal task agents must not promote `staging` to `main` after their task merge.
- Staging-to-main promotion happens only as separate release or deploy work in a separate workspace after the operator decides collected staging work is ready.
- Create or enter a separate task worktree or isolated checkout before editing files.
- Never use the root `staging` checkout as the workspace for normal task work.
- One task means one branch, one PR, and one task worktree or isolated checkout.
- Keep PRs scoped to one concern.

## Hotfix Rules

- A direct PR to `main` is allowed only for an explicitly approved hotfix.
- Hotfix branches must start from latest `origin/main` and be clearly marked as hotfix work.
- Every hotfix must include a backfill to `staging`.
- A hotfix is not complete until the fix has been merged or explicitly tracked back into `staging`.

## Usage / Cost Control Rules

- Before enabling any paid or potentially billable capability, self-audit whether spending is necessary, whether an adequate no-cost or lower-cost path exists, and whether expected and maximum plausible cost are proportionate to the requested outcome.
- Use an adequate no-cost path when one exists. Otherwise choose the smallest, shortest-lived, and lowest priced adequate resource and bound usage, retention, concurrency, and cleanup.
- Tell the operator the expected cost in plain English before incurring it. State whether the cost is one-time, hourly, monthly, usage-based, free-tier-covered, trial-covered, or unknown.
- Treat the current work request as authorization for necessary and proportionate in-scope spending; do not ask for repetitive approval solely because an action has a cost.
- Stop for operator direction when cost is unknown, disproportionate, outside scope or documented limits, creates a new recurring commitment, or presents materially different cost/outcome choices.
- This rule applies to providers and platforms such as Fly.io, GitHub, cloud hosts, preview providers, paid runners, storage, databases, tunnels, APIs, AI services, domains, observability, email, SMS, and any similar billable service.
- Local no-cost commands do not need a cost disclosure, but do not assume a local command is free if it starts, configures, or scales a paid external service.
- Record the resource, self-audit result, selected tier or size, expected cost, authorization basis, intended duration, cleanup path, and residual billing risk in durable handoff context.

## Integration Lane Rules When Adopted

- Use integration lanes only for long-running, multi-slice feature streams that should integrate before entering `staging`.
- Name lane branches `integration/<stream-slug>`.
- Create lane branches from fresh `origin/staging`.
- Open one collection PR from the lane branch to `staging`.
- Create slice branches from the latest lane branch and target slice PRs to `integration/<stream-slug>`.
- Slice PRs must pass normal validation, freshness, docs, and handoff requirements before merging into the lane.
- The lane collection PR must pass full validation before merging to `staging`.
- Keep lane plans and slice inventory in `STREAM.md`; update `STATE.md` only when repo-wide truth changes.
- Remove merged lane and slice worktrees/branches after the collection PR lands.

## Documentation Roles

- `AGENTS.md`: stable rules
- `STATE.md`: repo-wide truth
- `STREAM.md`: active stream scope and intent
- `HANDOFF.md`: short pickup note
- `DELTA.md`: append-only continuity ledger

## Delegated Agent Durability

- Treat sub-agents and delegated agents as ephemeral. Their chat history, IDs, registry entries, and final reports may not survive interruptions, compaction, aborts, resumes, or registry resets.
- Repo truth must live in files, not only in agent memory.
- Before substantial delegated work begins, create or return a durable checkpoint in `STREAM.md`, `HANDOFF.md`, `DELTA.md`, or the repo's equivalent management doc.
- The checkpoint must record the task, branch/worktree/repo path, dirty state, intended files or areas, plan, risks or blockers, and whether files will be modified.
- Long-running delegated work must checkpoint after initial inspection, before first edit, after meaningful milestones, before broad validation, and before final handoff.
- Implementation agents must use dedicated branches or workspaces unless an explicit exception is approved. Do not place implementation agents in a shared dirty worktree unless explicitly approved.
- If shared-worktree delegation is unavoidable, inventory dirty files first and state which changes will be avoided or preserved.
- The main management agent must mirror critical delegated-agent state into the main thread or repo docs.
- If a delegated agent disappears, inspect git status, inspect relevant diffs, read the latest durable checkpoint, preserve unexplained changes unless explicitly authorized to revert them, report uncertainty clearly, and decide whether to continue, revert, or move the work to a dedicated branch.

## Validation Rules

- Document and run the required checks before merge.
- Do not claim completion without evidence.

## Native Android Validation And Preview

- This is an existing Kotlin/Gradle Android launcher. Preserve its modules, wrapper, SDK declarations, application IDs, signing configuration, and GPL notices.
- Read `PREVIEW.md` and `docs/FORK_SETUP.md` before runtime work. Android builds, Gradle invocation (including `tasks` or `--version`), SDK/Gradle/dependency downloads, device access, and installation were deferred by the owner during setup.
- Check `df -h / /mnt/vk-storage` and confirm the SSD mount before build preparation. Store bulky downloads, caches and outputs on mounted secondary storage; do not fall back to the system disk.
- Documentation-only checks: `git diff --check`, `git fsck --full`, source/blob comparisons, and the setup checks in `docs/FORK_SETUP.md`. Do not report these as an Android build or runtime test.
- Future code changes require appropriate unit/lint/build checks after dependency preparation is authorized and capacity is adequate. The statically observed debug variant is `:app:app:assembleDefaultDebug`; task availability is not yet verified by Gradle.
- There is no web preview or server. Do not create a substitute browser app, tunnel, deployment, or paid runner for this setup.
- Before any S25 device work, read `/home/mcp/code/Operations/docs/s25-adb-access.md` and run `/home/mcp/.local/bin/s25-adb status`. Device testing requires task-specific authorization and verified current identity/route; preserve existing pairing and other owners.

## Upstream And Release Versioning

- `origin` is `git@github.com:artinflight/Kvaesitso.git`; `upstream` is `https://github.com/MM2-0/Kvaesitso.git`. Push only to the fork unless separately instructed.
- Keep full upstream history, tags, license text, copyright notices and attribution. Integrate upstream updates through an isolated branch and review; never reset fork branches to discard local work.
- `main` starts at the imported upstream baseline and is reserved for explicit release/upstream promotion. Normal task PRs target `staging`. Do not promote staging to main during ordinary tasks.
- The existing canonical application version is `versionName` and `versionCode` in `app/app/build.gradle.kts`; this is the repo's equivalent of `VERSION.json`. Do not introduce a conflicting version source or reset the app version to the playbook's template version.
- No release version, tag or changelog entry changes during documentation-only adoption. Future release work must align native version metadata, release notes and the production release record, while preserving Android's separate version-code rules.
- Inherited workflow files are preserved. Do not activate scheduled/nightly builds, releases, documentation deployment or external F-Droid dispatch as part of setup. Inspect fork Actions state before future pushes or CI enablement; audit cost and external effects first.

## User-Facing Language Standard

- Assume the operator may be a human non-developer.
- Write user-facing replies in plain English.
- Explain the practical meaning of technical terms when those terms are needed.
- Avoid unexplained jargon, acronyms, raw tool output, and implementation detail unless the operator needs them to make a decision.
- Make clear what changed, what was verified, what still needs human review, and what the next safe step is.

## Agent Summary Standard

This section is the default baseline for user-facing completion reports in repos using Ops Playbook. If this repo intentionally uses a different format, document that replacement here rather than leaving agents to improvise.

- Agents must produce exactly one user-facing final summary report per turn or task, and only when the turn is genuinely complete.
- The final summary report is the last action of the turn. If any work, validation, cleanup, sync, commit, push, PR operation, tool call, or additional reporting remains, do not send the final summary report yet.
- Every user-facing completion summary must follow the user-facing language standard above.
- Deliver pertinent, crucial, or important user-facing information once, in the final summary report.
- Treat mid-turn updates as transient: keep them brief, operational, and clearly non-final, and assume the operator may never read them.
- Important conclusions, decisions, blockers, validation results, and required user actions may appear in brief progress updates only if they are repeated in the one final summary report.
- A progress update must not use the final summary report headings, metadata block, or any other summary-shaped format. Those forms are reserved for the single final message only.
- Never send incomplete final summary reports, final-summary previews, draft final summaries, alternate summary formats, duplicate recaps, repeated summary blocks, post-summary addenda, or competing report formats.
- If the agent must stop because it is blocked, interrupted, or unable to continue, that stop-state still gets exactly one final summary report, and that report must clearly state what is incomplete, what was verified, and what the next safe step is. After sending it, do not continue the same turn.
- After sending the final summary report, do not continue the same turn with more status, caveats, work logs, tool actions, cleanup, reporting, or metadata.
- Use this default section order:
  - `Validation`
  - `What changed`
  - `Why it matters`
  - `What's next`
  - `PR`
  - `Docs`
  - `Churn`
  - `Human Needed`
  - `Commit/Push`
  - `Preview URL`
  - `Branch`
  - `Worktree`
  - `Version`
- Keep the first four sections as short complete-sentence narrative, not fragments.
- `What's next` is required every time. If no further execution is needed, say the branch is ready for review, merge, or user verification.
- Use compact metadata lines with `::` separators.
- Use `Docs:: Current` or `Docs:: Not Current`.
- Use `Churn:: Yes` or `Churn:: No`.
- Use `Human Needed:: Yes` or `Human Needed:: No`.
- Use `Commit/Push::` on one line and include both states, for example `Commit/Push:: Committed and Pushed`.
- Always include `Preview URL::` and use one of:
  - `Preview URL:: Not Generated`
  - `Preview URL:: Updated [Open preview](https://...)`
  - `Preview URL:: NotUpdated [Open preview](https://...)`
- When a PR exists, format it as an inline Markdown link on the metadata line, for example `PR:: [#123](https://github.com/owner/repo/pull/123)`.
- If a PR does not exist yet, use `PR:: Not opened yet`.
- When `PR::` or `Preview URL::` includes a URL, use a real inline Markdown link on that metadata line instead of a bare URL or backticked text.
- Keep preview status text outside the preview link, for example `Preview URL:: Updated [Open preview](https://...)`.
- Use `Churn:: Yes` when the agent has become stuck in a loop, is repeatedly revisiting the same ground, or has driven into minute detail that no longer moves the requested feature or task forward in a meaningful way. Use `Churn:: No` when the work remained directed at meaningful progress.
- Include `Version:: ` as the final metadata line, followed by the version number that the PR and branch were based on.
- Pull the version value from the repo's documented canonical version source, release baseline, or equivalent version record; do not guess it from memory.
- Keep the metadata block compact:
  - add exactly one blank line after `What's next` before the metadata block
  - use one line each for `PR`, `Docs`, `Churn`, `Human Needed`, `Commit/Push`, `Preview URL`, `Branch`, `Worktree`, and `Version`
  - do not add blank lines inside the metadata block
  - use `::` between each label and value
- Keep this format canonical in `AGENTS.md` so other docs do not fork it.

## Forbidden Behaviors

- Do not leave hidden local-only state.
- Do not treat stale docs as truth if code disagrees.
- Do not mix unrelated work into one branch.
