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

## Preview Workflow Rules

Use this section when the repo has user-facing screens.

- Follow the repo's documented preview workflow when one exists and works.
- Look for preview instructions in this order: `PREVIEW.md`, setup or deployment docs linked from `README.md`, repo-specific rules in `AGENTS.md` or `OPS_PLAYBOOK.md`, and existing preview scripts or services.
- When `scripts/preview.sh` or an equivalent repo-local preview command exists, run that command first. Do not manually choose ports, services, tunnels, or domains before trying the repo command.
- If no workflow exists, create a local or static preview using the repo's normal development command and the Ops Playbook preview standard.
- Before using any paid or potentially billable preview path, complete the necessity and proportionality self-audit and disclose the expected cost as required by the usage and cost control rules.
- Before starting a preview, confirm the current worktree contains the documented app entry files and start command.
- If the repo has multiple preview owners, such as a user service plus a branch-local terminal session, identify the canonical operator URL in `PREVIEW.md` or record which verified fallback was used. Do not report competing routes as equally authoritative.
- Bind local dev servers to a host the operator can reach when remote review is needed, and prefer private-network access over public tunnels.
- Verify the preview URL before reporting it; for apps with backend APIs, verify at least one app-specific API or health endpoint too.
- Record the command, working directory, host, port, URL, process or session identifier, logs, stop command, and access limits in handoff context.
- Do not replace a working repo-specific preview workflow just because another Ops Playbook pattern exists.
- Do not expose secrets, credentials, production-only data, or public tunnels unless repo rules explicitly allow it.

## Release Versioning Rules

Use this section when the repo publishes user-facing app releases.

- Treat `VERSION.json`, or the repo's named equivalent, as the canonical source for the next app release version.
- If this repo is newly adopting release versioning, create root `VERSION.json` from the Ops Playbook `templates/VERSION.template.json` and adjust it to the first intended release.
- The version source must include at least `version`, `tag`, and `releaseNotesId`; `tag` must equal `v` plus `version`.
- Release-bearing PRs must update the version source, package manifest, package lockfile, and top changelog or release-notes entry together when those files exist.
- The top changelog entry must match the version source tag and release-notes ID.
- Use standard unpadded SemVer tags such as `v0.96.0` and `v0.100.0`; do not use padded tags such as `v0.095.08` or `v0.096.00`.
- Staging and preview builds may show version and build metadata for QA, but only the production/live release path should create or update GitHub Releases or equivalent release records.
- Release automation may record deployed commit, ref, build time, and release URL, but must not expose secrets.
- Keep Android, iOS, or other native platform versioning separate unless this repo explicitly adopts one combined release policy.

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
