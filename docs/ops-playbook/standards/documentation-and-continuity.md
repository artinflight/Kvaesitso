# Documentation And Continuity Standard

## Purpose

This document defines the documentation model required for safe agent handoff, concurrent streams, and recoverable repo operations.

## Core Principle

Critical repo operating context must live in the repo, not only in chat.

This applies with extra force to delegated-agent work. Sub-agent IDs, active-agent registry entries, chat history, and final reports may not survive interruptions, compaction, client aborts, resumes, or registry resets.

## Required Documents

### `README.md`

Purpose:

- Explain what the repo is, how it is used, and how to get oriented.

Required contents:

- repo purpose
- high-level setup or usage guidance
- where the important operating docs live

Update rules:

- update when repo purpose, setup, or entry points materially change

### `AGENTS.md`

Purpose:

- Stable operating rules for agents in that specific repo.

Required contents:

- required read order
- authority order
- branch and PR rules
- documentation roles
- operator-facing language expectations
- completion-summary format rules when the repo wants standardized agent reporting
- validation expectations
- forbidden behaviors

Update rules:

- change only when repo rules change
- keep stable and concise

### `STATE.md`

Purpose:

- Current repo-wide truth.

Required contents:

- current objective
- confirmed current state
- in progress
- proposed/not adopted
- known gaps/blockers/deferred
- relevant files/modules
- decisions currently in force
- risks/regression traps
- next safe steps

Update rules:

- update only for repo-wide truth changes
- do not put branch-only intent here

### `STREAM.md`

Purpose:

- Active branch or PR stream scope and boundaries.

Required contents:

- stream identifier
- objective
- in scope
- out of scope
- stream-specific decisions
- relevant files/modules
- current status
- risks/regression traps
- next safe steps

Update rules:

- create and maintain on active task streams
- update when branch scope or direction changes

### `HANDOFF.md`

Purpose:

- Short pickup note for the next agent.

Required contents:

- what changed this session
- what is true right now
- what the next agent should do
- what the next agent must not do
- verification required before further changes
- session metadata

Update rules:

- keep short
- replace targeted sections instead of appending history

### `DELTA.md`

Purpose:

- Append-only checkpoint ledger for meaningful continuity events.

Required contents per entry:

- timestamp
- branch/session
- intent
- completed
- files changed
- verified
- not complete/known gaps
- risks/warnings
- next safest step

Update rules:

- append only for meaningful checkpoints, blockers, reversals, or handoff changes

## Delegated-Agent Continuity

Before a delegated agent does substantial work, it must create or return a durable checkpoint in `STREAM.md`, `HANDOFF.md`, `DELTA.md`, or the repo's equivalent management doc.

The checkpoint must include:

- task assigned
- branch, worktree, and repo path
- files or areas it expects to touch
- current dirty state observed
- plan of attack
- risks or blockers
- whether it will modify files

For long-running delegated work, checkpoint progress to disk:

- after initial inspection
- before first file edit
- after a meaningful implementation milestone
- before broad validation
- before final handoff

Implementation agents must use dedicated branches or workspaces unless an explicit exception is approved. If a delegated agent must work in a shared dirty worktree, it must inventory the dirty files first and state which changes it will avoid or preserve.

The main management agent must mirror critical delegated-agent state into durable repo context when the work is important. At minimum, record the task assigned, checkpoint received, files touched, blockers, validation status, and final disposition.

A delegated agent final report should include:

- what changed
- files changed
- validation run and results
- what was deliberately avoided
- known risks
- next recommended step

For important delegated work, the final report must be written to `HANDOFF.md`, `STREAM.md`, `DELTA.md`, or another agreed durable doc before the management agent treats the task as complete.

If a delegated agent disappears or cannot be resumed, do not infer invisible work from memory. Inspect git status, inspect relevant diffs, read the latest durable checkpoints, preserve unexplained changes unless explicitly authorized to revert them, report uncertainty clearly, and decide whether to continue, revert, or move the work to a dedicated branch.

## Recommended Optional Documents

### `REPO_IDENTITY.md`

Use when the repo benefits from a stable statement of role, audience, and constraints.

### Architecture notes

Use when the repo has meaningful technical boundaries or invariants that agents must preserve.

### Environment or setup docs

Use when local setup, secrets handling, deploy targets, or runtime modes need explicit instructions.

### Preview workflow docs

Use when the repo has user-facing screens and agents need a repeatable way to produce a preview URL for an operator who may be on another device.

For repos without a working preview path, create a doc from `templates/PREVIEW.template.md` or add equivalent guidance to the setup docs. It should record the command, host, port, local URL, remote-access URL, validation steps, log location, stop command, security limits, and handoff expectations.

Link the preview doc from `README.md`, `AGENTS.md`, or `OPS_PLAYBOOK.md` so future agents can find the repo-specific workflow before falling back to the generic Ops Playbook preview standard. Do not replace an established repo-specific workflow unless it is broken, undocumented, or explicitly superseded by the operator.

### Migration, refactor, or release notes

Use when a large effort needs durable context beyond one branch.

### QA or release checklist docs

Use when human verification needs a repeatable checklist.

For repos that publish app releases, release checklists should include the canonical version source, package metadata or lockfile alignment, top changelog or release-notes alignment, unpadded SemVer, production-only release publishing, safe deploy metadata, and any explicitly adopted native platform versioning rules.

### `OPS_PLAYBOOK.md`

Use only when a target repo or bootstrap flow expects an `OPS_PLAYBOOK.md` entrypoint. `AGENTS.md` remains the preferred canonical place for stable agent rules.

If `OPS_PLAYBOOK.md` exists, it must not become a weaker parallel rulebook. It should either be generated from `templates/OPS_PLAYBOOK.template.md` or explicitly point agents to the matching `AGENTS.md` summary-report contract.

## Standardized Completion Summaries

Repos using Ops Playbook should define their user-facing completion-summary contract in `AGENTS.md`. For new repos seeded from the playbook, the default contract is the full `Agent Summary Standard` from `templates/AGENTS.template.md`; if a target repo intentionally uses a different format, that replacement must be documented in `AGENTS.md` instead of leaving agents to improvise.

Recommended structure:

- `Validation`
- `What changed`
- `Why it matters`
- `What's next`
- metadata lines for `PR`, `Docs`, `Churn`, `Human Needed`, `Commit/Push`, `Preview URL`, `Branch`, `Worktree`, and `Version`

Rules:

1. Agents must produce exactly one user-facing final summary report per turn or task, and only when the turn is genuinely complete.
2. The final summary report is the last action of the turn. If any work, validation, cleanup, sync, commit, push, PR operation, tool call, or additional reporting remains, do not send the final summary report yet.
3. Deliver pertinent, crucial, or important user-facing information once, in that final summary report. Treat mid-turn updates as transient text the operator may never read.
4. Keep intermediate progress updates short, operational, and clearly non-final. They may orient the operator to current activity, but important conclusions, blockers, validation results, decisions, and required user actions must be repeated in the final summary report.
5. A progress update must not use the final summary report headings, metadata block, or any other summary-shaped format. Those forms are reserved for the single final message only.
6. Never send incomplete final summary reports, final-summary previews, draft final summaries, alternate summary formats, repeated summary blocks, duplicate recaps, post-summary addenda, or competing report formats.
7. If the agent must stop because it is blocked, interrupted, or unable to continue, that stop-state still gets exactly one final summary report, and that report must clearly state what is incomplete, what was verified, and what the next safe step is. After sending it, the agent must not continue the same turn.
8. After sending the final summary report, the agent must not continue the same turn with more status, caveats, work logs, tool actions, cleanup, reporting, or metadata. Any follow-up work belongs before the final summary report or in a later user-requested turn.
9. Treat the first four sections as short narrative explanation for the actual operator.
10. Follow the user-facing language rules below.
11. Keep them in complete sentences, not fragments.
12. Keep the metadata block compact and consistently formatted.
13. Use `Docs:: Current` or `Docs:: Not Current`, `Churn:: Yes` or `Churn:: No`, and `Human Needed:: Yes` or `Human Needed:: No`.
14. Use `Commit/Push::` on one line and include both states.
15. Always include `Preview URL::` with one of the repo-approved states: `Not Generated`, `Updated [Open preview](https://...)`, or `NotUpdated [Open preview](https://...)`.
16. When `PR::` or `Preview URL::` include a URL, use a real inline Markdown link on the same metadata line instead of a bare URL or backticked text.
17. When no PR exists, use `PR:: Not opened yet`.
18. Keep preview status words such as `Updated` or `NotUpdated` outside the Markdown link and link only the action text.
19. Define `Churn:: Yes` as a signal that the agent has become stuck in a loop, is repeatedly revisiting the same ground, or has driven into minute detail that no longer moves the requested feature or task forward in a meaningful way. Use `Churn:: No` when the agent judges that the work remained directed at meaningful progress.
20. Include `Version:: ` as the final metadata line, followed by the version number that the PR and branch were based on. The value should come from the repo's documented canonical version source, release baseline, or equivalent version record.
21. Keep this format canonical in one place so the repo does not develop competing summary templates.
22. Do not reuse the same headings in `HANDOFF.md` or other continuity docs unless they are the actual user-facing completion summary.

## User-Facing Language Rules

Repos using this playbook should make operator-facing language expectations explicit in `AGENTS.md`.

Baseline rule:

- assume the operator may be a human non-developer
- use plain English in user-facing replies
- explain the practical meaning of technical terms when they are needed
- avoid unexplained jargon, acronyms, raw logs, and unnecessary implementation detail
- preserve technical facts, but translate them into useful operating information

This rule applies to normal replies and final completion summaries.

## Stable Rules vs Current State

The baseline distinction is:

- `AGENTS.md`: stable rules
- `STATE.md`: current repo-wide truth
- `DELTA.md`: append-only history
- `HANDOFF.md`: immediate next-agent continuity
- `STREAM.md`: branch-local scope and intent

If these roles blur, concurrent agent work becomes harder to trust.

## Source-Of-Truth Rules

1. Repo truth must not live only in chat.
2. Repo-wide truth belongs in `STATE.md`.
3. Branch truth belongs in `STREAM.md`.
4. Immediate pickup context belongs in `HANDOFF.md`.
5. Historical continuity belongs in `DELTA.md`.
6. Stable rules belong in `AGENTS.md`.

## Continuity For New Agents

A new agent should be able to:

- read the required docs in order
- determine what is true
- determine what branch they are in
- see what remains to do
- understand what not to touch
- continue safely without hidden prior chat

That is the standard.
