# Validation And Automation Standard

## Purpose

This document defines the minimum safe validation and workflow automation baseline for repos using the Ops Playbook.

## Validation Tiers

### Required baseline checks

Every repo should have a documented minimum PR validation path. Depending on stack, that usually means:

- lint or static analysis, if the repo uses it
- tests appropriate to the repo
- build or packaging verification, if the repo builds artifacts
- preview smoke verification when a user-facing app has no stronger established preview workflow
- basic repo-governance checks, such as branch policy or required docs presence

If a repo does not support one of these categories, say so explicitly rather than leaving it ambiguous.

### Recommended checks

Most repos should also have:

- branch freshness or rebase checks
- preview/build artifact verification where applicable
- release note or changelog checks for promotion PRs
- version-source alignment checks for release-bearing PRs
- continuity-doc freshness checks if the repo adopts the continuity model

### Optional or repo-specific checks

Add heavier checks when risk justifies them, such as:

- integration tests
- migration tests
- infra or policy checks
- permissions/rules tests
- data consistency audits
- smoke deploys

These should block only when the affected surface actually demands them.

## What Blocks Which Merge

### PR into staging or integration branch

Required blockers should usually include:

- branch/base policy
- required baseline validation
- branch freshness

For a slice PR into an integration lane, the base branch should be `integration/<stream-slug>` and the head branch should be a normal task branch. For an integration-lane collection PR into `staging`, the head branch should be `integration/<stream-slug>` and the collection PR should satisfy the same or stronger checks as a normal staging PR.

Normal task PRs must target `staging`, not `main`. If automation sees a feature, fix, docs, chore, agent, or other normal task branch targeting `main`, it should fail the branch/base policy check and tell the agent to retarget the PR to `staging` or close and recreate it from `origin/staging`.

### Promotion into main or production

Required blockers should usually include:

- everything required for normal PRs
- base branch policy that allows only operator-started `staging` promotions or explicit hotfix PRs into `main`
- proof that staging-to-main promotion is running in a separate release or deploy workspace, not as a continuation of normal task work
- staging or release-candidate validation
- human QA signoff for important user-facing or production changes
- release note/changelog readiness when the repo uses them
- canonical version-source readiness when the repo has a version source such as `VERSION.json`
- for hotfixes, a required staging backfill PR, commit, or tracked follow-up before the hotfix is considered complete

## Workflow Automation Standards

### Recommended baseline workflows

Most well-managed repos should have:

- PR validation workflow
- branch policy enforcement
- branch freshness check
- staging or integration validation workflow
- production release workflow
- release version and changelog readiness workflow when the repo publishes app releases
- integration-lane base policy when the repo adopts `integration/<stream-slug>` lanes

### Optional but useful workflows

Depending on repo type, also consider:

- preview deploy workflow
- default local preview workflow for repos without platform previews
- scheduled hygiene reports
- stale branch or stale PR reminders
- release notes generation
- compliance or docs freshness checks

## Automation Design Rules

1. Automate behaviors, not just commands.
2. Keep required workflows understandable and predictable.
3. Prefer a smaller number of trustworthy workflows over a large number of brittle ones.
4. Separate advisory jobs from blocking jobs.
5. Document which checks are authoritative for merge decisions.

## Integration Lane Checks

Repos that adopt integration lanes should make lane support explicit in CI rather than relying on human memory.

Recommended branch policy:

- PRs into `staging` may come from normal task branches or from `integration/<stream-slug>` collection branches.
- PRs into `integration/<stream-slug>` may come only from normal task branches.
- Integration lane branches should not target other integration lane branches.
- PRs into `main` may come only from operator-started `staging` release or deploy promotions, or explicitly marked hotfix branches.
- Normal task branches targeting `main` must fail branch policy.
- Normal task agents attempting to merge both to `staging` and to `main`, or to promote `staging` to `main` after their task merge, must fail branch policy.
- Staging-to-main promotion must be handled as separate release work in a separate workspace.
- Hotfix PRs targeting `main` must require a staging backfill plan before merge and a completed or tracked backfill after merge.

Recommended validation:

- slice PRs run normal PR validation against the lane base
- slice PRs run freshness checks against the lane
- collection PRs run full validation against `staging`
- continuity-doc freshness checks apply to both slice PRs and collection PRs when the repo uses continuity docs
- release-bearing slice or collection PRs run changelog, release-note, or version-source checks according to the repo's release policy

Recommended automation behavior:

- create or update PR bodies with an explicit lane/slice/collection note
- refuse ambiguous branch reuse after a PR has already merged
- support safe merge into both `staging` and `integration/<stream-slug>`
- help close or identify superseded slice PRs
- surface stale lane branches before final lane-to-staging validation

## Release Version Checks

Repos that adopt a canonical version file such as `VERSION.json` should validate release-bearing PRs before they merge to staging or production.

Required checks should confirm:

- `VERSION.json` is the source of truth for the next app release version
- `VERSION.json.version`, `VERSION.json.tag`, and `VERSION.json.releaseNotesId` are present
- `VERSION.json.tag` equals `v` plus `VERSION.json.version`
- package metadata and lockfile versions match `VERSION.json.version` when the repo has package files
- the top changelog or release-notes entry matches `VERSION.json.tag`
- the top changelog or release-notes entry ID matches `VERSION.json.releaseNotesId`
- version tags use standard unpadded SemVer, such as `v0.96.0` or `v0.100.0`
- padded versions such as `v0.095.08` or `v0.096.00` fail validation

Production release automation should validate the same contract before it publishes. It should create or update the GitHub Release, or equivalent release record, for `VERSION.json.tag` only from the production/live release path.

Staging and preview deploy workflows may show version and build metadata for QA, but they should not create production release records. Deploy summaries may record the commit, ref, build time, release channel, and release URL, but must not print secrets or browser-unsafe credentials.

## Preview Workflow Checks

Repos with user-facing screens should document how an agent can create a preview the operator can actually reach. If no stronger workflow exists, use [preview-workflows.md](preview-workflows.md) as the default.

Minimum preview validation should confirm:

- the active worktree contains the documented app entry files and start command
- the server, static artifact, or deploy is running
- the preview URL responds from the agent machine
- the response is the expected app, not another service on the same port
- one app-specific API, health, or dynamic endpoint responds when the app depends on a backend
- remote-access details are recorded when the operator is not on the agent machine
- UI-facing changes receive a browser or screenshot check when tooling is available
- the process identifier, logs, and stop command are captured for handoff

Preview validation is not a replacement for tests, builds, or human QA. It proves that the operator has something concrete to inspect.

## Compliance Expectations

A repo should be considered minimally compliant when it has:

- documented required checks
- enforced branch/PR policy
- a clear production promotion path
- at least one reliable validation workflow for normal PRs

A repo should be considered strongly compliant when it also has:

- distinct staging vs production gates
- freshness checks
- docs/continuity freshness checks where applicable
- cleanup and stale-stream visibility

## Lessons Generalized From HYROXready-app

Keep:

- branch policy enforcement
- rebase freshness
- separate PR vs staging validation
- stronger gates for production promotion
- canonical app version, changelog, and production release alignment when a repo publishes user-facing app releases
- optional integration-lane support for long-running multi-slice streams
- default local preview workflow when a repo lacks a working preview path

Do not treat as universal:

- Firebase-specific deploy mechanics
- app-specific summary audit scripts
- app-specific data consistency jobs
- native platform versioning rules unless the target repo explicitly adopts them

Those belong in target repos as specializations layered on top of this baseline.
