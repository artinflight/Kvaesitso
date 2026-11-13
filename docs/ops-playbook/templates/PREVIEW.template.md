# Preview Workflow

## Purpose

- Define how agents create and verify a temporary preview when no stronger repo-specific workflow is available.
- Link this file from `README.md`, `AGENTS.md`, or `OPS_PLAYBOOK.md` so agents can find it before falling back to generic preview rules.

## Discovery Rule

- Use this file before the generic Ops Playbook preview standard.
- Preserve any stronger established repo-specific preview workflow.
- Fall back to the Ops Playbook preview standard only when this file is missing, incomplete, or no longer works.

## Quick Start

Agents should use this first:

```bash
scripts/preview.sh start
```

Useful follow-up commands:

```bash
scripts/preview.sh verify
scripts/preview.sh status
scripts/preview.sh stop
```

The `start` command must start or reuse the canonical preview, verify the operator-facing URL, and print the exact `Preview URL::` line to use in the final report. Do not manually choose ports, services, tunnels, or domains unless this command fails.

## Preview Type

- Existing platform preview:
- Static file preview:
- Local dev server:
- Fixed HTTPS preview lane:
- Tailscale Serve hostname:
- `.local` or private DNS name:
- Temporary tunnel:
- Other:

## Default Command

- Working directory:
- First command: `scripts/preview.sh start`
- Install command:
- Start command:
- Required environment:
- Default host:
- Default port:

## Cost Control

- Paid or potentially billable preview resources allowed:
- Default lowest-cost adequate option:
- Cost source or pricing note:
- Necessity and proportionality self-audit:
- Expected cost before enablement:
- Authorization basis or operator decision required:
- Free-tier or trial assumptions:
- Cleanup or scale-down command:

Agents must first determine whether an adequate local, existing, or no-cost preview path can meet the review need. If spending is necessary, they must select the lowest-cost adequate option, confirm that expected and maximum plausible cost are proportionate, bound usage and lifetime, and disclose expected cost before incurring it. The current preview request authorizes necessary and proportionate in-scope spending; stop for direction when cost is unknown, disproportionate, outside scope or limits, or presents materially different cost/outcome choices.

## Canonical Route

- Canonical operator URL:
- Final-report URL:
- Why this route is canonical:
- Local evidence URL:
- Alternate verified URLs:
- Legacy or non-reportable routes:
- What to do if routes disagree:

## Worktree Preflight

- Expected branch or base:
- Required files:
- Package or start script:
- App entry point:
- API or health endpoint:
- Existing service/tmux/tunnel/route checks:
- What to do if files are missing:

## URLs

- Local URL:
- Tailnet IP URL:
- Tailscale Serve URL:
- Private DNS or `.local` URL:
- HTTPS private URL, if needed for clickable links:
- Remote-access URL:
- Required client network or DNS:
- Public URL, if explicitly allowed:
- Final-report URL:
- Known chat-renderer limits:

## Verification

- First check: `scripts/preview.sh verify`
- HTTP check:
- API or health check:
- Public URL check:
- Clickable-link check:
- Browser or screenshot check:
- Expected first screen:
- Known acceptable warnings:
- Known failure signs:

## Process Management

- Status command: `scripts/preview.sh status`
- How to see logs:
- How to find the running process:
- How to stop the preview: `scripts/preview.sh stop`
- When to stop the preview:
- Canonical process owner:
- Fallback process owner:
- Whether multiple owners may run at once:
- Managed tunnel or preview slot:
- Tailscale/DNS/proxy owner:
- Route repair command:

## Security Rules

- Secrets allowed in preview:
- Data or accounts allowed:
- Public tunnel allowed:
- Production services allowed:
- Paid services covered by standing task or repo authorization:
- Screenshot restrictions:

## Handoff Notes

When an agent starts or updates a preview, record:

- preview status
- command used
- host and port
- local URL
- tailnet, Tailscale Serve, or private DNS URL when used
- remote-access URL
- canonical reportable URL
- process/session/deploy identifier
- route owner or service identifier when different from the app process
- competing service, route, or tunnel status when one exists
- paid or potentially billable resource, self-audit result, expected cost, authorization basis, intended duration, cleanup path, and residual billing risk when applicable
- validation performed
- final-report URL
- known access limits
- stop command

## Final Report Metadata

Use the repo's completion-summary format when adopted:

- `Preview URL:: Not Generated`
- `Preview URL:: Updated [Open preview](https://...)`
- `Preview URL:: NotUpdated [Open preview](https://...)`
