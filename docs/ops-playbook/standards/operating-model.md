# Operating Model Standard

## Purpose

This document defines the default branch, PR, release, and cleanup model for repos using the Ops Playbook.

## Branching And Environment Model

### Standard branches

- `main`: production branch. Only production-ready code should land here.
- `staging`: integration and pre-production branch. This is the default base for normal feature, fix, docs, and chore work.
- `integration/<stream-slug>`: optional long-running feature integration lane. Use only for multi-slice streams that need to integrate before entering `staging`.
- task branches: short-lived branches for one stream of work.

### Default rules

- Direct pushes to `main` and `staging` must be blocked.
- Agents must not commit on, push to, or otherwise do task work inside `main`.
- New work must start from the latest `origin/staging` and open a PR back to `staging`.
- A PR from a normal task branch directly to `main` is invalid and must be retargeted or closed.
- Normal task agents must stop after their PR lands in `staging`; they must not also merge, promote, or deploy `staging` to `main`.
- Normal agent work must happen in a separate worktree or isolated checkout created for the task branch.
- Agents must not edit the root checkout that tracks `staging` for feature, fix, docs, chore, or exploratory work.
- One stream of work means one branch, one PR, and one task worktree or isolated checkout.
- Agents should not mix unrelated concerns in the same branch.
- Implementation sub-agents must receive a dedicated branch or workspace unless an explicit exception is approved, with clear ownership boundaries before they edit files.
- Do not assign implementation sub-agents to a shared dirty worktree unless explicitly approved. If unavoidable, inventory dirty files first and state which changes must be preserved.

### Branch naming

Use concise, readable prefixes such as:

- `feature/<topic>`
- `fix/<topic>`
- `docs/<topic>`
- `chore/<topic>`
- `agent/<topic>`
- `codex/<topic>`

Repos may add stricter naming conventions, but the baseline requirement is clarity and one-stream isolation.

Use `integration/<stream-slug>` only for documented integration lanes. Do not use that prefix for normal task branches.

### Integration lane exception

Normal work should target `staging` directly. A repo may add integration lanes when a long-running feature stream needs multiple slice PRs to integrate before the combined result enters `staging`.

The baseline lane model is:

- create `integration/<stream-slug>` from fresh `origin/staging`
- open one collection PR from the lane to `staging`
- create slice branches from the latest lane branch
- target slice PRs to `integration/<stream-slug>`
- merge only validated slices into the lane
- keep the lane fresh with `origin/staging`
- merge the collection PR to `staging` only after final validation

Integration lanes must not bypass PR governance, validation, continuity docs, changelog or version expectations, architecture rules, parity rules, or human QA. See [integration-lanes.md](integration-lanes.md) for the full standard.

### Hotfix exception

If a repo must patch production directly:

- branch from latest `origin/main`
- use a clearly marked hotfix branch
- target the hotfix PR to `main`
- require explicit human approval that this is a hotfix
- include the staging backfill plan in the hotfix PR before merge
- backfill the exact fix to `staging` immediately after the hotfix merge
- record the main merge and staging backfill in durable handoff or release notes

This hotfix path is the only acceptable direct-to-`main` work. A hotfix is incomplete until the fix is backfilled to `staging`; leaving `main` ahead of `staging` is a release-safety defect.

## Pull Request Discipline

### Scope rules

- One PR per concern.
- Keep PRs small enough to review safely.
- Do not mix refactors, fixes, infrastructure changes, and unrelated cleanup unless they are inseparable.
- Update docs that define changed behavior or changed operating truth.

### PR intent requirements

Every PR should make clear:

- what problem it solves
- what is in scope
- what is out of scope
- what was validated
- what still needs human verification
- whether it supersedes or depends on another open stream

### Supersession rules

- A newer PR does not automatically supersede an older one.
- Close an older PR only when its work is intentionally abandoned, replaced, or fully absorbed elsewhere.
- If one PR supersedes another, say so explicitly in the PR body, stream doc, and handoff.
- If two related PRs stay open, identify the primary stream and the sidecar stream.

### Freshness rules

- PR branches should contain the latest base branch tip before merge.
- If the repo uses long-running streams, rebase or merge base updates often enough to keep conflicts shallow.

## Merge Expectations

### Into staging

Before merging to `staging`, require:

- passing required PR checks
- current branch freshness against `staging`
- a PR from the task branch or approved integration lane, not direct edits in the root `staging` checkout
- current stream docs
- a clean PR scope
- version, package metadata, and changelog alignment when the PR carries user-facing release content

### Into an integration lane

Before merging a slice PR into an integration lane, require:

- passing required PR checks
- current branch freshness against the lane
- slice scope and lane target clearly documented
- current stream and handoff docs
- no bypass of release, architecture, parity, or governance requirements that would apply to staging

### From an integration lane into staging

Before merging a collection PR from an integration lane to `staging`, require:

- the lane refreshed from current `origin/staging`
- all intended slices integrated or explicitly removed from scope
- full required validation for the combined stream
- current lane-level stream and handoff docs
- cleanup plan for merged slice branches, obsolete PRs, and lane worktrees
- release notes, changelog, and version-source readiness if the stream is release-bearing

### Into main

Before merging to `main`, require:

- either an operator-started release or deploy promotion from `staging`, or an explicitly approved hotfix PR
- no normal feature, fix, docs, chore, agent, or integration-lane branch targeting `main`
- no same-agent continuation from task PR merge into production promotion
- a separate release or deploy workspace for staging-to-main promotion work
- explicit human QA for meaningful production impact
- required production release checks
- release notes, changelog, and version-source readiness if the repo uses them
- for hotfix PRs, a completed or tracked backfill to `staging`

## Release Flow

The default release flow is:

1. Agent creates or enters a separate task worktree from latest `origin/staging`.
2. Task branch opens PR into `staging`.
3. `staging` accumulates validated work.
4. Human QA happens against staging or release candidate artifacts.
5. When the operator decides the collected staging work is ready for release, a separate release or deploy workspace promotes `staging` into `main`.
6. Production deploy runs from `main` or from the release tag, depending on repo setup.

When integration lanes are adopted, they sit before step 1 as a feature-stream exception: slice branch -> integration lane -> collection PR -> `staging`.

No normal task work may skip `staging`, and no normal task agent may continue from a staging merge into a main promotion. Staging-to-main promotion is separate release work that happens only when the operator has collected staging work and is ready to deploy or release it. The only allowed direct path from a PR to `main` is a hotfix PR, and that path must include a backfill to `staging` so later staging-to-main promotion does not diverge.

Repos without a separate staging environment may adapt this, but they must still define:

- the integration branch
- the production branch
- the required promotion checks

Release, deploy, and promotion work must follow [usage-and-cost-controls.md](usage-and-cost-controls.md) before enabling or scaling any paid or potentially billable resource. Agents must self-audit necessity, cheaper alternatives, and proportionality; use the lowest-cost adequate option; and disclose expected cost before incurring it. The work request authorizes necessary and proportionate in-scope spending, while unknown, disproportionate, out-of-scope, or materially different cost choices require operator direction.

## Preview Flow

Repos with user-facing screens should define how an agent creates a preview for branch review. If no established workflow exists, follow [preview-workflows.md](preview-workflows.md).

The baseline expectation is:

1. Use an existing documented preview workflow when it works.
2. Otherwise start a local or static preview from the task branch.
3. Make the URL reachable to the operator, using private-network access when available.
4. Verify the URL from the agent machine and visually check UI changes when tooling is available.
5. Record the command, URL, process state, logs, stop command, and access limits in durable handoff context.

Preview creation does not replace PR validation or production release gates. It gives the operator a concrete thing to review before merge.

## App Versioning And Release Notes

Repos that publish user-facing app releases should define one canonical version source. For JavaScript apps that use package metadata, the recommended baseline is a root `VERSION.json` file that owns the next app release version and connects runtime metadata, changelog entries, and production release artifacts.

New repos seeded from Ops Playbook should copy `templates/VERSION.template.json` to root `VERSION.json` when they publish versioned releases. Existing repos may keep an equivalent version source, but `AGENTS.md`, release docs, and validation checks must name that source explicitly.

The baseline `VERSION.json` shape is:

- `version`: the unprefixed SemVer value, such as `0.1.0`
- `tag`: the release tag, such as `v0.1.0`
- `releaseNotesId`: the changelog or release-notes entry identifier
- `channel`: the release channel or baseline label
- `status`: the release state, such as `unreleased`, `rc`, or `released`

When a repo adopts this model, release-bearing PRs must update these files together:

- `VERSION.json`
- package manifest, such as `package.json`
- package lockfile, such as `package-lock.json`
- the top changelog or release-notes entry

The top changelog entry must align with:

- `VERSION.json.tag`
- `VERSION.json.releaseNotesId`

Use normal SemVer numeric segments only. Good examples are `v0.96.0` and `v0.100.0`. Bad examples are `v0.095.08` and `v0.096.00`.

Staging and preview builds may expose version and build metadata for QA and support, including the deployed commit, ref, build time, release channel, and release URL. That metadata must not expose secrets.

GitHub Releases or equivalent production release records should be created only from the production/live release path. The production release workflow should publish or update the release matching `VERSION.json.tag` and record the deployed commit, ref, build time, and release URL where the repo can surface them safely.

Native platform versioning, such as Android version codes or iOS build numbers, remains separate unless the repo explicitly brings native versioning into the same release policy.

## Cleanup And Hygiene

### After merge

- Delete merged task branches.
- Remove merged local worktrees or disposable checkouts.
- Stop editing merged branches.

### For superseded or abandoned work

- Close superseded PRs with a short explanation.
- Delete clearly abandoned branches unless they must be preserved for audit or recovery.
- Archive exploratory work only when it contains useful context not worth merging.

### Worktree hygiene

- Keep one worktree or isolated checkout per active stream.
- Reserve the root `staging` checkout for updating the integration baseline, reviewing merged state, and explicit maintenance. Do not use it as an agent task workspace.
- Name worktrees consistently.
- Periodically remove worktrees for merged, abandoned, or superseded streams.
- Treat delegated-agent workspaces as disposable but their checkpoints as durable. Before cleanup, confirm changed files, validation status, and handoff notes are captured in repo docs or the PR.

## Solo-Operator Interpretation

This baseline is intentionally disciplined but lightweight:

- no committee approval chains
- no mandatory multi-reviewer bureaucracy
- human QA where production risk justifies it
- strong rules around branch isolation, validation, and continuity because those are where solo multi-agent setups tend to fail
