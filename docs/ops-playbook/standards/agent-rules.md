# Agent Operating Rules

## Purpose

This document defines how agents should behave inside a repo that uses the Ops Playbook.

## Before Acting

Agents should:

1. Read source-of-truth docs before making changes.
2. Confirm the current branch or workstream.
3. Confirm they are in a task worktree or isolated checkout, not the root `staging` checkout.
4. Confirm normal task work is based on `staging`, not `main`.
5. Verify the requested task against actual repo state.
6. Identify the intended scope before editing files.

## Scope Discipline

Agents must:

- stay within the assigned stream
- avoid mixing unrelated concerns into one branch
- avoid unannounced structural changes
- update stream-local docs if scope changes

## Safety Rules

Agents must not:

- edit protected branches directly
- do normal feature, fix, docs, chore, or agent work in `main`
- open a normal task PR to `main`
- merge both to `staging` and to `main` as part of the same task
- promote, merge, or deploy `staging` to `main` after finishing a normal task unless they are explicitly assigned separate release or deploy work in a separate workspace
- use the root `staging` checkout as the workspace for normal task work
- make destructive cleanup changes without understanding ownership and impact
- assume a newer branch supersedes an older open stream
- rely on stale docs when code or validation contradicts them

The only direct-to-`main` exception is an explicitly approved hotfix PR. A hotfix must be clearly marked, must target `main`, and must include a backfill to `staging`; without the backfill, the hotfix workflow is not complete.

## Usage And Cost Control Rules

Before enabling any paid or potentially billable service, resource, feature, workflow, preview, deployment, runner, storage, tunnel, API, model, domain, integration, or external platform capability, agents must follow [usage-and-cost-controls.md](usage-and-cost-controls.md).

The baseline requirement is:

1. Self-audit whether spending is necessary, whether an adequate no-cost or lower-cost path exists, and whether expected and maximum plausible cost are proportionate to the requested outcome.
2. Use the no-cost path when adequate; otherwise choose the smallest, shortest-lived, and lowest priced adequate resource.
3. Tell the operator the expected cost in plain English before incurring it and state the billing basis.
4. Treat the current work request as authorization for necessary, proportionate in-scope spending instead of asking for routine approval on every cost-bearing turn.
5. Stop for direction when cost is unknown, disproportionate, outside scope or limits, creates a new recurring commitment, or presents materially different cost/outcome choices.

This applies to providers and platforms such as Fly.io, GitHub, cloud hosts, preview providers, paid runners, storage, databases, tunnels, APIs, AI services, domains, observability, email, SMS, and any similar billable service. Local no-cost commands do not need a cost disclosure, but agents must not assume a local command is free if it starts, configures, or scales a paid external service.

## Reporting Rules

Agents should leave visible, durable state by:

- updating continuity docs when truth changes
- recording blockers and next-safe steps
- stating what was verified versus still unverified
- making handoffs readable by a fresh agent

Repos using Ops Playbook should standardize user-facing completion reports in `AGENTS.md`. New repos should use the baseline `Agent Summary Standard` from `templates/AGENTS.template.md` unless the operator explicitly documents a repo-specific replacement. If a repo or bootstrap flow uses `OPS_PLAYBOOK.md`, that file must include a dedicated summary-report section or explicitly defer to the matching `AGENTS.md` rule.

Agents must produce exactly one final summary report per turn or task, and only when the turn is genuinely complete. The final summary report is the last action of the turn; if work, validation, cleanup, sync, commit, push, PR operations, tool calls, or additional reporting remains, the agent has not reached the final summary report yet.

Mid-turn updates should stay brief, operational, and clearly non-final. Important conclusions, decisions, blockers, validation results, and required user actions may appear there only if they are repeated in the one final summary report. Progress updates must not use final summary report headings, metadata blocks, or any other summary-shaped format. Agents must not send incomplete final summary reports, final-report previews, draft final summaries, alternate summary formats, duplicate recaps, repeated summary blocks, post-summary addenda, or competing report formats. If an agent must stop because it is blocked, interrupted, or unable to continue, that stop-state still gets exactly one final summary report that states what is incomplete, what was verified, and the next safe step.

## Preview Discovery Rules

When asked to create, update, validate, or report a preview, agents should first look for repo-specific preview instructions before falling back to generic Ops Playbook guidance.

Discovery order:

1. `PREVIEW.md`
2. setup, development, deployment, or QA docs linked from `README.md`
3. preview rules in `AGENTS.md` or `OPS_PLAYBOOK.md`
4. existing preview scripts, services, or platform configuration already used by the repo
5. `docs/standards/preview-workflows.md` and `templates/PREVIEW.template.md` from the Ops Playbook when no working repo-specific preview path exists

Agents must not replace a working repo-specific preview workflow just because the Ops Playbook contains another pattern. The generic standard is for missing, broken, or undocumented preview workflows.

## Delegated Agent Rules

Sub-agents and delegated agents must be treated as temporary helpers, not durable systems of record. Their IDs, chat history, registry entries, and final reports may disappear after interruptions, compaction, client aborts, resumes, or registry resets.

Rules:

- do not rely on delegated-agent memory as the only source of truth
- require a durable checkpoint before substantial delegated work begins
- checkpoint long-running delegated work to disk, not only to chat
- use dedicated branches or workspaces for implementation delegation unless an explicit exception is approved
- avoid shared dirty worktrees unless explicitly approved
- require changed-file lists and validation results in delegated handoffs
- preserve unexplained user or agent changes unless explicitly authorized to revert them

The initial delegated-agent checkpoint should record:

- task assigned
- branch, worktree, and repo path
- files or areas the agent expects to touch
- current dirty state observed
- plan of attack
- risks or blockers
- whether the agent will modify files

Minimum checkpoint cadence for long-running delegated work:

- after initial inspection
- before first file edit
- after a meaningful implementation milestone
- before broad validation
- before final handoff

The main management agent must mirror critical delegated-agent state into the main thread or repo docs. That includes agent spawned, task assigned, checkpoint received, files touched, blockers found, validation status, and final disposition.

If a delegated agent disappears, the latest durable checkpoint and the git worktree become the source of truth. The management agent should inspect status, inspect relevant diffs, read the latest checkpoint and handoff docs, report uncertainty clearly, and decide whether to continue, revert with approval, or move changes to a dedicated branch.

## Operator-Facing Language

Agents should assume the operator is a human who may not be a developer. User-facing replies should use plain English and explain the practical outcome, not just the technical mechanism.

Agents should:

- say what changed in terms the operator can act on
- explain technical terms briefly when they are needed
- distinguish what is done from what still needs human review
- avoid raw logs, unexplained acronyms, and file inventories unless the operator asked for them

The goal is not to hide technical facts. The goal is to translate them into useful operating information.

## Handoff Rules

At the end of meaningful work, an agent should leave:

- current branch scope
- current validated state
- what remains to do
- what not to do next
- whether human QA or deploy action is still needed

## Concurrent Development Rules

When many agents are working in parallel:

- one branch should represent one stream
- one PR should represent one concern
- one task worktree or isolated checkout should represent one active stream
- stream-local docs should explain branch boundaries
- repo-wide docs should not be polluted with branch-only intent
- superseded streams should be closed or explicitly marked

## Confusion Handling

If an agent encounters missing context, conflicting docs, or unclear ownership, the default response should be:

- verify code and branch reality first
- narrow the uncertainty
- update stale docs if truth is clear
- escalate only the unresolved risk, not the whole task

## Repo Preparation For New Agents

A repo is ready for new agents when:

- the core docs exist
- branch roles are clear
- validation commands are documented
- the active stream has a readable `STREAM.md`
- `HANDOFF.md` and `DELTA.md` carry the latest continuity state

That is the minimum entry condition for safe cold-start agent work.
