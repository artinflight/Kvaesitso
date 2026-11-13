# Integration Lanes Standard

## Purpose

Integration lanes are optional branches for long-running, multi-slice feature streams. They let several related slice PRs integrate with each other before the combined stream enters `staging`.

Use this model when landing each completed slice directly in `staging` would expose an incomplete product stream, create excessive superseded PRs, or make cross-slice validation difficult.

Do not use an integration lane for normal small tasks, independent fixes, docs-only work that can safely land in `staging`, production promotions, or direct hotfixes.

## Branch Model

The default lane shape is:

1. Create a lane branch from the latest `origin/staging`.
2. Name it `integration/<stream-slug>`.
3. Open one collection PR from `integration/<stream-slug>` into `staging`.
4. Create slice branches from the latest lane branch.
5. Target slice PRs back into `integration/<stream-slug>`.
6. Merge validated slice PRs into the lane.
7. Refresh the lane from `origin/staging` regularly.
8. Merge the collection PR into `staging` only after the whole stream is finalized and validated.

Slice branches should use the repo's normal task-branch prefixes, such as `feature/`, `fix/`, `agent/`, or `codex/`. Lane branches should be reserved for `integration/<stream-slug>`.

## Required Rules

- A lane starts from `origin/staging`, not from `main`.
- A lane has exactly one collection PR into `staging`.
- Slice PRs target the lane, not `staging`.
- The lane collection PR is the only PR that moves the combined stream into `staging`.
- Slice PRs must pass normal PR governance, validation, freshness, documentation, and handoff requirements before merging into the lane.
- The lane collection PR must pass the full required validation set before merging into `staging`.
- A lane does not bypass architecture rules, parity rules, changelog expectations, continuity docs, CI, or human QA.
- The lane must be refreshed from `origin/staging` before final lane-to-staging validation.
- Branch-only lane plans belong in `STREAM.md` or the repo's equivalent stream doc, not in repo-wide state.
- Repo-wide docs should change only when the lane changes durable repo truth.

## Continuity Requirements

Use the lane branch's stream doc for:

- overall stream objective
- boundaries and non-goals
- slice inventory
- integrated slice status
- lane freshness status
- finalization criteria

Use slice branch stream docs for slice-local scope and explicitly name the lane they target.

Use handoff docs for pickup context, latest integrated slices, blockers, validation still needed, and cleanup state.

Use append-only continuity logs for compact checkpoints when a slice merges, the lane refreshes from `staging`, a blocker changes direction, or the collection PR becomes ready for staging.

## Validation And Automation

Repos that adopt integration lanes should teach their PR automation to recognize:

- normal PRs into `staging`
- slice PRs into `integration/<stream-slug>`
- collection PRs from `integration/<stream-slug>` into `staging`
- separate operator-started release or deploy promotions from `staging` into `main`
- explicitly approved hotfix PRs into `main`, with required backfill to `staging`

Integration lanes never target `main`. They exist only before `staging`; production promotion remains separate release work from `staging` to `main`, except for the hotfix path that must be backfilled to `staging`.

Recommended blocking checks for slice PRs:

- base branch is `integration/<stream-slug>`
- head branch is a normal task branch, not another lane
- branch freshness against the lane
- normal PR validation
- continuity-doc freshness when the repo uses continuity docs
- changelog or release-note readiness when the slice is release-bearing

Recommended blocking checks for the collection PR:

- base branch is `staging`
- head branch is `integration/<stream-slug>`
- lane freshness against current `staging`
- full required validation
- continuity docs current for the whole stream
- changelog, release-note, or version-source readiness when the combined stream is release-bearing
- human QA signoff when production risk justifies it

Automation may provide helper commands for PR sync, safe merge, stale branch cleanup, and superseded PR closure. Those helpers are repo-specific implementations of the lane policy, not universal playbook requirements.

## Cleanup

After the collection PR merges into `staging`:

- remove the lane worktree or isolated checkout
- delete the merged lane branch when retention policy allows
- remove merged slice worktrees
- delete obsolete slice branches
- close superseded slice PRs with a clear explanation
- preserve useful final context in the PR, handoff, or continuity log

## Adoption Checklist

- Does the repo have enough long-running multi-slice work to justify lanes?
- Is `staging` already defined as the normal integration branch?
- Is `integration/<stream-slug>` branch naming documented?
- Are slice PR and collection PR base rules enforced?
- Do lane and slice docs clearly separate branch-local truth from repo-wide truth?
- Does automation prevent lane branches from targeting other lanes?
- Does final lane-to-staging validation equal or exceed normal staging validation?
- Is cleanup after lane completion documented?

## Lessons Generalized From HYROXready-app

Keep:

- `integration/<stream-slug>` branches for long-running streams
- one collection PR from the lane to `staging`
- slice PRs from normal task branches into the lane
- lane-aware base branch policy
- freshness and continuity checks for both slice PRs and collection PRs
- final lane-to-staging validation before staging merge
- explicit cleanup of merged lanes, slices, worktrees, and superseded PRs

Do not treat as universal:

- HYROXready-specific script names
- HYROXready-specific worktree paths
- Firebase or deploy mechanics
- app-specific architecture or parity declarations
- app-specific changelog file names

Those belong in target repos as local specializations layered on top of this standard.
