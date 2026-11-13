# OPS_PLAYBOOK.md

## Purpose

Use this file only when a repo or bootstrap flow expects an `OPS_PLAYBOOK.md` entrypoint. `AGENTS.md` remains the preferred canonical place for stable agent rules. If both files exist, this file must either duplicate the active summary-report contract below or point agents to the matching `AGENTS.md` section without weakening it.

## Required Source Of Truth

Before acting, agents must read:

1. `AGENTS.md`
2. `STATE.md`
3. `STREAM.md`
4. `HANDOFF.md`
5. Relevant standards, code, and validation docs
6. `DELTA.md` only when compact history is needed

## Working Loop

1. Confirm the branch, worktree, and requested scope.
2. If normal task work is needed, create or enter a separate task worktree from latest `origin/staging`; do not edit the root `staging` checkout.
3. Inspect the relevant files before editing.
4. Keep changes focused on the requested task.
5. Run the documented validation that matches the risk of the change.
6. Update continuity docs when repo truth, branch status, validation, or next steps change.
7. Preserve unrelated user or agent changes unless explicitly authorized to change them.
8. Handoff notes must include changed files or areas, validation results, residual risk, and the next safe step.

## Usage / Cost Control Rules

Before enabling any paid or potentially billable capability, agents must self-audit whether spending is necessary, whether an adequate no-cost or lower-cost path exists, and whether expected and maximum plausible cost are proportionate to the requested outcome. Use an adequate no-cost path when one exists; otherwise choose the smallest, shortest-lived, and lowest priced adequate resource and tell the operator the expected cost before incurring it.

The cost disclosure must state whether the cost is one-time, hourly, monthly, usage-based, free-tier-covered, trial-covered, or unknown. The current work request authorizes necessary and proportionate in-scope spending; agents must not ask for repetitive approval solely because an action has a cost. Stop for direction when cost is unknown, disproportionate, outside scope or limits, creates a new recurring commitment, or presents materially different cost/outcome choices.

This rule applies to providers and platforms such as Fly.io, GitHub, cloud hosts, preview providers, paid runners, storage, databases, tunnels, APIs, AI services, domains, observability, email, SMS, and any similar billable service. Local no-cost commands do not need a cost disclosure, but agents must not assume a local command is free if it starts, configures, or scales a paid external service.

When a paid or potentially billable resource is enabled, handoff notes must record the resource, self-audit result, selected tier or size, expected cost, authorization basis, intended duration, cleanup path, and residual billing risk.

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

## Preview Workflow Rules

Use this section when the repo has user-facing screens or needs operator review through a browser.

- Follow the repo's documented preview workflow when one exists and works.
- Look for preview instructions in this order: `PREVIEW.md`, setup or deployment docs linked from `README.md`, repo-specific rules in `AGENTS.md` or this file, and existing preview scripts or services.
- When `scripts/preview.sh` or an equivalent repo-local preview command exists, run that command first. Do not manually choose ports, services, tunnels, or domains before trying the repo command.
- If no workflow exists, create a local or static preview using the repo's normal development command and the Ops Playbook preview standard.
- Before using any paid or potentially billable preview path, complete the necessity and proportionality self-audit and disclose the expected cost as required by the usage and cost control rules.
- If the repo has multiple preview owners, such as a user service plus a branch-local terminal session, identify the canonical operator URL in `PREVIEW.md` or record which verified fallback was used. Do not report competing routes as equally authoritative.
- Verify the preview URL before reporting it; for apps with backend APIs, verify at least one app-specific API or health endpoint too.
- Record the command, working directory, host, port, URL, process or session identifier, logs, stop command, and access limits in handoff context.
- Do not replace a working repo-specific preview workflow just because another Ops Playbook pattern exists.
- Do not expose secrets, credentials, production-only data, or public tunnels unless repo rules explicitly allow it.

## Summary Report Rules

Agents must produce exactly one user-facing final summary report per turn or task, and only when the turn is genuinely complete. The final summary report is the last action of the turn. If any work, validation, cleanup, sync, commit, push, PR operation, tool call, or additional reporting remains, do not send the final summary report yet.

Use brief progress updates during the turn, but treat them as transient. Important conclusions, decisions, blockers, validation results, and required user actions must appear in the one final summary report, not only in progress updates. A progress update must not use the final summary report headings, metadata block, or any other summary-shaped format.

Never send incomplete final summary reports, final-summary previews, draft final summaries, alternate summary formats, duplicate recaps, repeated summary blocks, post-summary addenda, or competing report formats. If the agent must stop because it is blocked, interrupted, or unable to continue, that stop-state still gets exactly one final summary report, and that report must clearly state what is incomplete, what was verified, and what the next safe step is. After sending the final summary report, do not continue the same turn with more status, caveats, work logs, tool actions, cleanup, reporting, or metadata.

Use this default section order:

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

Write `Validation`, `What changed`, `Why it matters`, and `What's next` as short complete-sentence narrative for the operator. `What's next` is required every time.

Use compact metadata lines with `::` separators:

- `PR:: [#123](https://github.com/owner/repo/pull/123)` when a PR exists, or `PR:: Not opened yet` when no PR exists.
- `Docs:: Current` or `Docs:: Not Current`.
- `Churn:: Yes` when the agent has become stuck in a loop, is repeatedly revisiting the same ground, or has driven into minute detail that no longer moves the requested task forward. Otherwise use `Churn:: No`.
- `Human Needed:: Yes` or `Human Needed:: No`.
- `Commit/Push::` with both states, for example `Commit/Push:: Committed and Pushed`.
- `Preview URL:: Not Generated`, `Preview URL:: Updated [Open preview](https://...)`, or `Preview URL:: NotUpdated [Open preview](https://...)`.
- `Branch::` with the active branch.
- `Worktree::` with the active worktree path.
- `Version::` as the final metadata line, using the repo's documented version source, release baseline, or equivalent version record. Do not guess the value from memory.

When `PR::` or `Preview URL::` include a URL, use a real inline Markdown link on the same metadata line. Do not use bare URLs or backticked links.

## Definition Of Done

Work is not done until:

- requested changes are complete
- relevant validation has been run or the reason it could not run is documented
- continuity docs reflect current truth when they changed
- known limitations, residual risk, and follow-up work are captured
- the final user-facing report can be sent as the last action of the turn
