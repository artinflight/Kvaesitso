# Bring A Repo Up To Spec

## Purpose

This document is the reusable adoption workflow for auditing a target repo against the Ops Playbook and bringing it into compliance without breaking active development.

## Adoption Principles

- Harden incrementally.
- Do not stop active delivery unless the repo is actively unsafe.
- Fix the highest-risk operational gaps first.
- Preserve working repo-specific practices where they are sound.
- Standardize the operating model before layering on advanced automation.

## Phase 1: Initial Audit

Assess the target repo in these areas:

### Repo structure and governance

- Is there a clear default branch model?
- Is there a staging/integration branch or an equivalent release path?
- Are protected branches treated as protected in practice?
- Are agents blocked from doing normal work in `main`?
- Are normal task PRs required to start from and target `staging`?
- Are normal task agents blocked from promoting `staging` to `main` after their task merge?
- Is staging-to-main promotion handled only as separate release or deploy work in a separate workspace?
- Is the only direct-to-`main` exception an explicit hotfix with a required backfill to `staging`?
- Does the repo need integration lanes for long-running multi-slice streams, or is direct-to-staging task work enough?

### PR workflow

- Are task branches isolated?
- Do agents create or enter separate task worktrees instead of working in the root `staging` checkout?
- Are PRs reviewable and single-purpose?
- Are normal task PRs prevented from targeting `main`?
- Are agents prevented from merging the same task both to `staging` and to `main`?
- Are older PRs being left open without clear status?
- If integration lanes exist, do slice PRs target the lane and does one collection PR target `staging`?

### Validation

- What checks currently exist?
- Which checks are blocking versus advisory?
- What is missing from the minimum safe baseline?

### Preview workflow

- Does the repo already have a documented, reliable preview workflow?
- Is the preview workflow easy for agents to find from `README.md`, `AGENTS.md`, `OPS_PLAYBOOK.md`, or a root `PREVIEW.md`?
- Does the repo have a single executable preview entrypoint, normally `scripts/preview.sh start`, that starts or reuses the preview, verifies it, and prints the final `Preview URL::` line?
- Does that entrypoint also support `verify`, `status`, and `stop`?
- Can an agent produce a URL the operator can open from another device?
- Is the default preview local, private-network, platform-hosted, or tunneled?
- If remote operator access is needed, is the preferred path a fixed HTTPS preview lane, Tailscale Serve hostname, documented `.local` or private DNS name, raw tailnet IP, or platform preview?
- If `.local`, `.preview`, or another named private domain is used, does the repo document the DNS/proxy owner, route target, validation command, and access limits?
- If Tailscale is used, does the repo distinguish raw tailnet IP access from Tailscale Serve or MagicDNS hostname access?
- Does `PREVIEW.md` name one canonical operator-facing URL for final reporting, with local evidence, alternate, fallback, and legacy routes labeled separately?
- If multiple preview owners exist, such as a user service, tmux session, tunnel, or route script, does the repo document which owner is canonical and what agents should do when they disagree?
- Does preview setup say which branch/worktree and app entry files must exist before starting a server?
- Are host, port, command, logs, stop command, and validation steps documented?
- For apps with APIs, does preview validation check both the page and the API or health endpoint?
- Are secrets, production data, and public tunnel rules clear?

### Usage and cost controls

- Do agent rules require the lowest priced adequate resource before any paid or potentially billable service is enabled?
- Do agents self-audit whether spending is necessary, whether an adequate no-cost or lower-cost path exists, and whether expected and maximum plausible cost are proportionate to the requested outcome?
- Do agent rules require the operator to be told the expected cost before enabling paid services, including whether cost is one-time, hourly, monthly, usage-based, free-tier-covered, trial-covered, or unknown?
- Does the repo avoid repetitive approval gates when the current work request already authorizes necessary, proportionate in-scope spending?
- Do agents stop for direction when cost is unknown, disproportionate, outside scope or limits, creates a new recurring commitment, or presents materially different cost/outcome choices?
- Do preview, deploy, automation, storage, runner, API, AI service, domain, tunnel, and integration workflows include the same cost gate?
- Do handoff docs record enabled paid resources, the necessity and proportionality conclusion, expected cost, authorization basis, intended duration, cleanup path, and residual billing risk?

### Automation

- What GitHub Actions or equivalent automation exists?
- Are branch policy, freshness, and release safety enforced?
- Are there stale or brittle workflows that no one trusts?
- If integration lanes exist, does automation recognize `integration/<stream-slug>` bases and block invalid lane targets?

### Release versioning

- Does the repo have a canonical source for the next app release version, such as `VERSION.json`?
- If this is a new versioned repo, was `templates/VERSION.template.json` copied to root `VERSION.json` and adjusted to the repo's first intended release?
- Do release-bearing PRs update the version source, package manifest, lockfile, and top changelog or release-notes entry together?
- Does the version source include at least `version`, `tag`, and `releaseNotesId`, and does `tag` equal `v` plus `version`?
- Does the top changelog entry match the version tag and release-notes ID from the version source?
- Are version tags standard unpadded SemVer, such as `v0.96.0` or `v0.100.0`, rather than padded values such as `v0.095.08`?
- Are staging and preview builds allowed to show build metadata without creating production GitHub Releases?
- Does the production release workflow publish or update the GitHub Release matching the canonical version tag?
- Does release automation record deployed commit, ref, build time, and release URL without exposing secrets?
- Is native platform versioning kept separate unless the repo has explicitly adopted one combined policy?

### Documentation and continuity

- Do `README.md` and `AGENTS.md` exist?
- Does the repo or bootstrap flow create `OPS_PLAYBOOK.md`, and if so, does it include the same summary-report contract as `AGENTS.md`?
- Is there any durable current-state doc?
- Is branch-local continuity documented anywhere?
- Is critical context trapped in chat only?
- Do agent rules say sub-agents are ephemeral and require durable checkpoints before substantial delegated work?
- Do delegated-agent rules define where checkpoints and final handoffs are written?
- Do agent-facing rules tell agents to write for a human non-developer operator when replying to the user?
- Does the repo already have a canonical completion-summary/reporting format, or are agents improvising inconsistent status reports?

### Cleanup and hygiene

- Are merged or superseded branches being removed?
- Are abandoned worktrees or stale branches accumulating?
- Does repo state match open PR reality?

## Phase 2: Gap Classification

Classify findings into:

- critical: production or branch safety risk
- important: continuity, reviewability, or operational clarity gap
- nice-to-have: useful improvement that should not block initial adoption

Typical critical items:

- direct pushes to production branch
- normal work committed in `main`
- normal task PRs targeting `main`
- normal task agents promoting `staging` to `main` after their task merge
- hotfixes merged to `main` without a staging backfill
- no PR validation
- unclear release path
- no documented validation commands
- no usable preview path for a user-facing app that requires remote operator review
- a working preview path exists but is hidden in chat, shell history, or an unlinked local script agents are unlikely to find
- preview setup is documented only as prose with no repo-local command an agent can run
- preview instructions can be followed from a stale branch that does not contain the app runtime
- preview docs tell agents to use a named private URL without documenting how that name resolves or how to verify it
- preview docs expose multiple working ports, services, or URLs without selecting one canonical reportable operator URL
- agent rules allow paid services, deploys, runners, previews, tunnels, storage, APIs, or other billable resources to be enabled before cost is disclosed
- agent rules do not require the lowest priced adequate resource for billable work
- agent rules treat operator approval as a substitute for auditing whether spending is necessary and proportionate
- production release automation can publish the wrong version or create releases from non-production paths

Typical important items:

- missing continuity docs
- poor branch naming or branch isolation
- agents using the root `staging` checkout as a task workspace instead of a separate worktree
- no freshness checks
- preview setup depends on agent guesswork, repeated failed commands, or local-only URLs the operator cannot open
- preview docs require agents to interpret ports, services, tunnels, or route ownership instead of running a repo-local preview command
- preview docs exist but are not linked from normal agent entrypoints
- remote preview setup relies on raw IPs, `.local`, `.preview`, or tailnet hostnames without a durable route owner, service name, and stop or repair instructions
- preview validation checks only that a port responds, not that the active worktree contains and serves the intended app
- preview service ownership is ambiguous, for example both a user service and a branch-local session can serve the app but the repo does not say which one agents should report
- paid or potentially billable resources can remain running without handoff notes that name the resource, necessity and proportionality conclusion, expected cost, authorization basis, intended duration, cleanup path, and residual billing risk
- stale PR or branch clutter
- release-bearing PRs can drift between version source, package metadata, and changelog
- ad hoc long-running feature branches without lane rules, slice ownership, or final collection validation

## Phase 3: Baseline Upgrade Plan

Implement in this order unless repo conditions require otherwise:

1. Define branch roles and merge path.
2. Define the worktree rule: normal agent work starts from latest `origin/staging` in a separate task worktree or isolated checkout, never in the root `staging` checkout.
3. Add or normalize `README.md` and `AGENTS.md`.
4. If the repo or bootstrap flow expects `OPS_PLAYBOOK.md`, create it from `templates/OPS_PLAYBOOK.template.md` or make it explicitly defer to the matching `AGENTS.md` rules. Do not leave `OPS_PLAYBOOK.md` with only working-loop or definition-of-done handoff notes.
5. Add `STATE.md`, `HANDOFF.md`, and `DELTA.md`.
6. Start using `STREAM.md` for active branches.
7. Add delegated-agent durability rules to `AGENTS.md` and continuity docs. Require durable checkpoints, branch or workspace isolation for implementation delegation, and lost-agent recovery steps.
8. Add usage and cost control rules from `docs/standards/usage-and-cost-controls.md`. Require agents to self-audit necessity, adequate no-cost and lower-cost alternatives, and proportionality before any billable action; choose the lowest-cost adequate resource; and disclose expected cost before incurring it. Treat the work request as authorization for necessary and proportionate in-scope spending, while requiring operator direction for unknown, disproportionate, out-of-scope, recurring, or materially different cost choices.
9. Add minimum PR validation and branch policy automation.
10. Define production promotion rules, including the hard rule that only separate operator-started staging promotions and explicit hotfixes may target `main`.
11. Add integration-lane rules only if the repo has real multi-slice streams that should integrate before `staging`.
12. Add a default preview workflow for user-facing apps that do not already have one. Use `templates/PREVIEW.template.md` and `templates/scripts/preview.sh` or equivalent setup docs and commands to record the canonical reportable operator URL, local evidence URL, alternate or fallback URLs, commands, reachable URLs, Tailscale or private-DNS route details when used, validation, logs, stop command, and security limits. The repo must expose a single preview command, normally `scripts/preview.sh start`, that agents can run before reading fallback details. Link that workflow from `README.md`, `AGENTS.md`, or `OPS_PLAYBOOK.md` so agents find it before falling back to generic rules.
13. Define release versioning rules when the repo publishes app releases. For new versioned repos, copy `templates/VERSION.template.json` to root `VERSION.json` and adjust it to the repo's first intended release. If the repo uses `VERSION.json`, require release-bearing PRs to update `VERSION.json`, package metadata, lockfiles, and the top changelog entry together, and require unpadded SemVer tags.
14. Add operator-facing language rules in `AGENTS.md` so agents use plain English for a human non-developer operator.
15. Standardize agent completion-summary formatting in `AGENTS.md`.
   For new repos, copy the full `Agent Summary Standard` from `templates/AGENTS.template.md` unless the operator explicitly chooses a repo-specific replacement. For existing repos, preserve a stronger local format only if it already covers the same baseline contract. Require exactly one final summary report per turn, allow it only when the turn is genuinely complete, make it the last action of the turn, prohibit summary-shaped progress updates, prohibit incomplete final summary reports, final-report previews, draft final summaries, alternate summary formats, duplicate recaps, repeated summary blocks, post-summary addenda, and competing report formats, require important user-facing information to appear there instead of only mid-turn, define `Churn::` as the agent's self-check for unproductive looping or excessive detail that no longer moves the task forward, make the `PR::` and `Preview URL::` values use inline Markdown links that render in the actual summary surface, require `Docs:: Current` or `Docs:: Not Current`, require `Human Needed:: Yes` or `Human Needed:: No`, require `Preview URL::` every time, and add a final `Version:: ` metadata line with the version number that the PR and branch were based on.
16. Add cleanup and archival rules.
17. Add repo-specific optional governance only after the baseline is stable.

## Phase 4: Incremental Rollout

### For active repos

- Do not rewrite every doc at once.
- Introduce the continuity docs and use them on current active streams first.
- Add CI checks in stages so the repo is not flooded with failing unrelated work.
- Start by blocking only the controls that are clearly required.

### For new repos

- Start with the full baseline from day one.
- Use the templates in this repo directly.
- Add stack-specific validation commands before the first feature PR.

## Phase 5: Compliance Review

A repo is reasonably up to spec when:

- branch roles are documented and followed
- agents cannot do normal work in `main`
- normal task agents cannot promote `staging` to `main`
- staging-to-main promotion is isolated as separate release or deploy work
- direct-to-`main` work is limited to explicit hotfixes with staging backfills
- agents use separate task worktrees and do not edit the root `staging` checkout for normal work
- integration lanes are documented and enforced when adopted
- PRs are isolated by concern
- required validation is documented and enforced
- user-facing apps have a documented preview workflow that can produce an operator-reachable URL
- preview instructions are linked from the repo's normal agent entrypoints
- any Tailscale, `.local`, `.preview`, or fixed preview hostname is documented with its route owner, local origin, validation checks, access limits, and stop or repair path
- paid or potentially billable services require a necessity and proportionality self-audit, the lowest-cost adequate resource, and cost disclosure before enablement
- paid resource handoffs record the necessity and proportionality conclusion, expected cost, authorization basis, intended duration, cleanup path, and residual billing risk
- release flow is clear
- release version, package metadata, changelog, and production release records align when the repo uses them
- continuity docs exist and are being used
- delegated-agent work is checkpointed durably and recoverable if a sub-agent disappears
- agent completion summaries follow the baseline final-report contract or a documented repo-specific replacement
- `OPS_PLAYBOOK.md`, when present, has a dedicated summary-report section or defers to the equivalent `AGENTS.md` section
- user-facing replies are written in plain English for the operator, not only for developers
- active streams have readable handoff context
- merged and superseded work is being cleaned up

## Deliverable For A Future Agent

When asked to bring a repo into compliance, the agent should produce:

1. An audit summary
2. A gap list grouped by severity
3. A proposed upgrade sequence
4. The initial baseline docs and automation changes
5. A statement of what remains repo-specific or deferred

## Recommended Output Structure For Adoption Passes

- `docs/audits/<repo>-ops-audit.md`
- updated root docs in the target repo
- `VERSION.json` for repos that publish versioned releases, or a documented equivalent version source
- workflow changes under `.github/workflows/`
- preview setup docs for user-facing apps
- any repo-specific setup or release notes needed to explain deviations

## Solo-Operator Bias

The purpose of adoption is not to recreate enterprise process. It is to give one operator a repo that agents can work in safely:

- with clean branches
- with visible continuity
- with reliable validation
- with understandable release flow
- with less cleanup debt

That is the standard to optimize for.
