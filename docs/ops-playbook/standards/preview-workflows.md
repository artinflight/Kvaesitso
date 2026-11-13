# Preview Workflow Standard

## Purpose

This document defines the default preview workflow for repos that do not already have a documented, reliable preview path.

The baseline assumes a solo operator may be away from the machine running the agent. The preview workflow must therefore produce a reachable URL, clear process state, and enough handoff context for another agent to recover or stop it.

## When To Use This Standard

Use this standard when:

- the repo has no established preview workflow
- the existing workflow is broken or undocumented
- the operator asks for a preview but has not specified a repo-specific path
- the agent needs a temporary preview before a durable deploy workflow exists

Do not replace a working repo-specific preview workflow without explicit reason. If the repo has a documented preview workflow, follow it and record only the actual preview URL and validation result.

## How Agents Should Find Preview Instructions

When asked to create or update a preview, agents should look for instructions in this order:

1. A repo-specific preview doc, normally `PREVIEW.md` or a clearly named setup/deployment doc linked from `README.md`, `AGENTS.md`, or `OPS_PLAYBOOK.md`.
2. Repo-specific agent rules in `AGENTS.md` or `OPS_PLAYBOOK.md`.
3. Existing service, deploy, or preview scripts already used by the repo.
4. This standard and `templates/PREVIEW.template.md` when the repo has no working documented preview path.

Do not override a working repo-specific preview workflow just because this standard includes a newer or more convenient pattern. Use this standard to fill gaps, repair broken or undocumented preview setup, or create a first repeatable preview workflow for an unmanaged repo.

Target repos that adopt this playbook should make preview discovery explicit by linking their preview doc from `README.md`, `AGENTS.md`, or `OPS_PLAYBOOK.md`.

## Required Preview Entrypoint

For any repo where agents are expected to spin up previews repeatedly, prose is not enough. The repo must provide one executable preview entrypoint, normally:

```bash
scripts/preview.sh start
```

The entrypoint must support:

- `start`: start or reuse the canonical preview, verify it, and print the exact `Preview URL::` line agents should use
- `verify`: verify the currently running canonical preview and fail nonzero if it is not usable
- `status`: show the process, service, port, or deployment state without changing it
- `stop`: stop only the repo-owned preview process, without removing shared DNS, proxy, or tunnel infrastructure

`PREVIEW.md` must put the `start` command in a top-level Quick Start section before any architecture explanation, route ladder, or fallback detail. If a repo has this command, agents should run it before manually inspecting ports, tmux sessions, services, tunnels, or DNS.

The command output should be copyable into the final report. A successful `start` should print:

- local URL
- operator-facing URL
- process or service owner
- exact `Preview URL:: Updated [Open preview](...)` metadata line

If the entrypoint cannot be created yet, that is a preview workflow gap. The repo may temporarily document manual commands, but `HANDOFF.md` must say the preview workflow is not fully operational until the single-command entrypoint exists.

## Canonical Preview Contract

Every repo-specific preview workflow must name one canonical operator-facing preview route for normal agent use. A repo may have multiple ways to serve the app, but `PREVIEW.md` must make the reporting choice unambiguous.

The preview doc should separate:

- canonical operator URL: the URL agents should verify and report in `Preview URL::`
- local evidence URL: localhost or loopback checks used only to prove the process is serving
- alternate reachable URLs: LAN, tailnet, or fixed-lane URLs that are acceptable fallbacks
- legacy or shared routes: services, `.local` names, or tunnels that may exist but must not be reported unless verified against the active worktree

If an existing service, tmux session, tunnel, or named route disagrees with `PREVIEW.md`, the agent must not guess. It must inspect both owners, verify which one serves the active worktree, update the repo handoff with the result, and report only the canonical or explicitly chosen fallback URL.

For long-lived previews, prefer one durable process owner per app surface. If both a user `systemd` service and a branch-local `tmux` session are valid, `PREVIEW.md` must say which one is canonical, which one is a fallback, and whether both should remain running. Do not leave two equally valid preview routes without a selection rule.

## Default Preview Ladder

Agents should use the first applicable option in this order:

1. Existing documented preview workflow.
2. Static file preview when the app can be opened directly in a browser.
3. Repo-approved fixed preview lane, such as a managed HTTPS hostname that routes to known local ports.
4. Local dev server exposed through a private network route the operator can reach.
5. Existing platform preview deploy, if already configured and safe.
6. Temporary tunnel only when local or private-network access is not enough and the operator approves or repo rules allow it.

The default for unmanaged repos is usually a local dev server. Prefer private network access, such as a VPN or mesh network address, over public tunnels when available.

Before using any platform preview, paid tunnel, managed preview lane, cloud deploy, hosted runner, domain, storage, database, or other potentially billable preview resource, agents must follow [usage-and-cost-controls.md](usage-and-cost-controls.md). First decide whether a local, existing, or other no-cost path adequately provides the requested review outcome. If spending is necessary, choose the lowest-cost adequate resource, confirm the cost is proportionate, bound its lifetime and usage, and tell the operator the expected cost before incurring it. Stop for direction when cost is unknown, disproportionate, outside scope or limits, or presents materially different cost/outcome choices.

## Reference Patterns Observed

Successful VK previews share a few durable traits:

- the preview has a named route or hostname, not only an anonymous port
- the local process is managed in a way that survives the agent's terminal when the preview needs to remain available
- the remote URL is verified directly, not inferred from a local `curl`
- API-backed apps verify both the page and an API or health endpoint
- the handoff records the command, service or session name, route, validation, access limits, and stop command

Reusable patterns observed in other repos:

| Pattern | Durable and reusable | Optional or environment-specific | Do not make baseline policy |
| --- | --- | --- | --- |
| Fixed HTTPS preview lane | Predeclare hostname, local ports, service names, validation checks, and allowed users. | Cloudflare Tunnel or equivalent managed routing. | A specific domain, lane count, or port range. |
| Tailscale Serve hostname | Use a tailnet hostname when the operator has Tailscale access and the route is verified. | Tailscale Serve/Funnel command shape and tailnet DNS name. | A specific machine name, tailnet suffix, or public Funnel exposure. |
| `.local` named preview | Document the DNS or mDNS owner, upstream proxy, app origin, and fallback. | Pi-hole, nginx, mDNS, host-router services, and LAN-specific IPs. | Assuming `.local` works for every Tailscale client. |
| Raw private IP and port | Useful fallback when bound to `0.0.0.0` and verified from the host. | Tailnet IPs such as `100.x`, LAN IPs such as `10.x`, or port-specific links. | Treating raw IP URLs as durable or always clickable in chat. |
| Public platform preview | Good when already configured with safe preview data and access controls. | Cloudflare Workers, Vercel, Fly, or another platform. | Creating production deploys or new public apps just to get a preview. |

These are reference patterns, not universal requirements. A target repo should adopt the smallest pattern that gives the operator a reliable, verified URL without exposing data unnecessarily.

## Tailscale And Named Private Preview Standard

For repos without an established preview path, prefer a Tailscale-accessible private preview before a public tunnel when the operator can reach the tailnet.

Use this order for private named access:

1. A repo-approved HTTPS preview hostname or fixed lane that already routes to known local ports.
2. A Tailscale Serve HTTPS or HTTP hostname, such as the machine's MagicDNS name, when it is already configured or can be safely configured for this repo.
3. A documented `.local` name only when the target environment already provides DNS, mDNS, or a host-router path for that name.
4. A raw tailnet or LAN URL, such as `http://100.x.y.z:<port>` or `http://10.x.y.z:<port>`, as a fallback when it is the only verified remote route.

Do not invent `.local`, `.preview`, or other named domains in a repo unless the DNS route, proxy owner, and validation path are documented. `.preview` may be a good private convention only when the operator has configured split DNS, MagicDNS, or a managed proxy for it; it is not a default internet or Tailscale convention.

When a Tailscale route is used, record:

- whether the route is raw tailnet IP, Tailscale Serve hostname, `.local`, or managed HTTPS lane
- the local origin host and port
- the command or service that owns the local origin
- the command or service that owns the Tailscale or DNS/proxy route, if known
- the exact URL verified from the agent machine
- what the operator's device must have, such as Tailscale access, MagicDNS, or homelab DNS
- how to stop the local process without accidentally removing shared network routing

## Cross-Repo Adoption Plan

For a repo that does not yet have an established preview workflow:

1. Create `PREVIEW.md` from `templates/PREVIEW.template.md`.
2. Create `scripts/preview.sh` from `templates/scripts/preview.sh` or an equivalent repo-native command, then make it executable.
3. Wire `scripts/preview.sh start` so it starts or reuses the canonical preview, verifies the operator-facing URL, and prints the final-report `Preview URL::` line.
4. Wire `scripts/preview.sh verify`, `status`, and `stop`.
5. Pick one default remote-access path:
   - fixed HTTPS preview lane when the environment already has managed hostnames and local port routing
   - Tailscale Serve hostname when the repo should stay private to the tailnet and a named URL is better than a raw IP
   - documented `.local` or private DNS name when homelab DNS or mDNS already makes that name work for the operator
   - raw tailnet IP and port only as the initial fallback
6. Mark that path as the canonical operator URL and label all other routes as local evidence, alternate fallback, or legacy/non-reportable.
7. Define one default local origin port per app surface, such as web and API, plus a safe alternate port rule.
8. Define the process manager for previews that should outlive the terminal, such as a user `systemd` service or named `tmux` session. If more than one process manager exists, choose one canonical owner and document the fallback.
9. Define validation commands for:
   - the local origin URL
   - the operator-facing remote URL
   - at least one API or health endpoint when the app is dynamic
10. Define security limits, including allowed test users, allowed data, and whether public tunnels are forbidden or require explicit approval.
11. Add the current preview state to `HANDOFF.md`, `STREAM.md`, or a repo-approved equivalent whenever a preview is started, changed, stopped, or blocked.

The first version does need an executable entrypoint. It does not need elaborate automation. It does need a repeatable route, a verified URL, and one command an agent can run without interpreting the preview architecture.

## Local Dev Server Baseline

When creating a local preview, the agent should:

1. Run the repo preview entrypoint, such as `scripts/preview.sh start`, when it exists.
2. If no entrypoint exists, identify the app entry point and package manager from repo files.
3. Confirm the current branch and worktree actually contain that app entry point.
4. Install dependencies only when required and permitted by repo rules.
5. Before using any paid or potentially billable preview path, complete the necessity and proportionality self-audit and disclose the expected cost as required by the usage and cost control standard.
6. Choose a port deliberately, checking whether it is already in use.
7. Bind the server to `0.0.0.0` or the repo-approved host when the operator needs remote access.
8. Prefer a non-production mode unless the repo explicitly requires otherwise.
9. Start the server in a way that keeps logs accessible.
10. Verify the preview from the agent machine with an HTTP check.
11. When browser tooling is available, open the preview and capture at least one screenshot or equivalent visual check for UI changes.
12. Record the command, working directory, port, local URL, remote-access URL, process/session identifier, and stop command.
13. Include the preview link in the final completion summary using the repo's `Preview URL::` metadata format when the repo has adopted that format.

If the server cannot be reached remotely, the agent should say so plainly and record the last verified local URL rather than implying the operator can open it.

## Worktree And Runtime Preflight

Before starting a preview server, prove that the current checkout can run the app being previewed.

Required preflight:

- run `pwd` and `git status --short --branch`
- read the repo's preview/setup docs, such as `PREVIEW.md`, `README.md`, `AGENTS.md`, or equivalent
- run the repo's preview entrypoint first when it exists, normally `scripts/preview.sh start`
- inspect existing preview owners named by the docs, such as `systemctl --user cat <service>`, `tmux ls`, tunnel config, or route scripts
- confirm the documented working directory exists in the current worktree
- confirm the documented entry files exist, such as `package.json`, `src/server.*`, `public/index.html`, `vite.config.*`, framework config, or equivalent
- confirm the package script or start command exists before running it
- confirm the current branch contains the app code expected by the docs

If the current worktree does not contain the app files, do not keep trying ports, tunnels, or alternate hostnames. Stop preview setup and recover the branch/worktree first.

Recovery options:

1. If the worktree is on an old bootstrap branch, rebase or merge the current integration branch only when repo rules allow it and the worktree is clean.
2. If the app already exists in another checkout, switch to that checkout or create a fresh worktree from the branch that contains the app.
3. If repo docs describe files that are not present anywhere, record the mismatch as the blocker.
4. If there are dirty files, preserve them and ask before replacing or rebasing the worktree.

Common failure sign:

- The preview docs say to run `npm start`, but `package.json` is missing in the active worktree. That is a branch/worktree problem, not a port or tunnel problem.
- The preview docs say to use one port, but an existing user service serves the same app on another port. That is a canonical-route problem: verify both, choose the documented canonical route or update the handoff with the chosen fallback, and do not report both as equally authoritative.

For apps with a backend API, a static file server is not a valid preview unless the static server also routes the API correctly. Verify both the document root and at least one app-specific API or health endpoint before reporting success.

## Remote Operator URL Rules

For operators working from another device, a preview URL is useful only if it can be reached from that device.

Agents should provide:

- local URL, such as `http://127.0.0.1:<port>`, only as machine-local evidence
- remote URL, such as `http://<private-host-or-ip>:<port>`, when the machine has a private-network address the operator can reach
- public tunnel URL only when explicitly allowed by repo rules or operator instruction

Agents must not print secrets, tokens, signed URLs, private cookies, or credentials in preview metadata.

Agents must also not create a paid public tunnel, managed hostname, platform preview, cloud service, or other billable remote route when an adequate no-cost route exists, or before confirming proportionality, disclosing expected cost, and selecting the lowest priced adequate option.

## Link Rendering Recovery

If the operator says the preview works only after copying a plain private-network URL, but the final report needs a clickable preview link, treat that as a preview workflow failure until a renderer-friendly URL is available.

Recovery order:

1. Confirm the private preview still works with `curl -I`.
2. Prefer a repo-approved HTTPS hostname that preserves the intended private route, such as `https://<app>.local`, before moving to a different public or managed hostname.
3. If a managed preview tunnel already exists, bind the local preview to the port that tunnel routes to.
4. Verify the public HTTPS URL directly with `curl -I`.
5. Put the public HTTPS URL in `Preview URL:: Updated [Open preview](https://...)`.
6. Record the local server command, public hostname, validation, logs, and stop command in handoff context.

Do not keep re-reporting a private `http://100.x`, `http://10.x`, or raw local URL when the operator has already said the chat client will not make it clickable.

### Example: Static docs through an existing preview hostname

This is an example, not universal baseline policy. Use it when a repo or MCP environment already has a managed Cloudflare hostname that routes to a known local port.

For this Ops Playbook branch, the working preview was:

1. Confirm the static file server command works locally:
   - `python3 -m http.server <port> --bind 127.0.0.1`
2. Start it durably in `tmux` on the local port used by an existing preview hostname:
   - `tmux new-session -d -s opspb-public-preview -c /path/to/repo 'python3 -m http.server 5226 --bind 127.0.0.1'`
3. Verify the local file:
   - `curl -I http://127.0.0.1:5226/docs/standards/preview-workflows.md`
4. Verify the public HTTPS preview:
   - `curl -I https://preview-5.opnvlp.com/docs/standards/preview-workflows.md`
5. Use the public HTTPS URL in the final report:
   - `Preview URL:: Updated [Open preview](https://preview-5.opnvlp.com/docs/standards/preview-workflows.md)`
6. Record the stop command:
   - `tmux kill-session -t opspb-public-preview`

If the public HTTPS check returns `502`, the local process is not listening on the tunnel's expected port. If it returns `404`, check whether the tunnel points at the expected local service and whether the file path exists under that service's working directory.

### Example: Static docs through `.local` HTTPS

This is an environment-specific example, not universal baseline policy. Use it when the operator's environment already has homelab DNS, nginx, and a local host router for `.local` previews.

For this Ops Playbook branch, the working preview was:

1. Start a static docs server in the repo worktree:
   - `tmux new-session -d -s opspb-standard-preview -c /path/to/ops-playbook 'python3 -m http.server 8126 --bind 0.0.0.0'`
2. Add a local host-router entry:
   - route `opspb.local -> 127.0.0.1:8126` in the existing local router
3. Add private DNS and proxy routing:
   - Pi-hole resolves `opspb.local -> 10.0.0.97`
   - homelab nginx proxies `opspb.local` to the MCP host router at `10.0.0.129:3010`
4. Add HTTPS for renderer-friendly links:
   - create `opspb.local` certificate files in the homelab nginx SSL directory
   - add an nginx `443 ssl` server block for `opspb.local`
5. Optionally add LAN mDNS fallback:
   - publish `opspb.local -> 10.0.0.129` with a user service when LAN clients need mDNS
6. Verify the local origin:
   - `curl -I http://127.0.0.1:8126/docs/standards/preview-workflows.md`
7. Verify private DNS:
   - `dig +short @10.0.0.97 opspb.local A`
8. Verify the operator-facing HTTPS route:
   - `curl -k -I --resolve opspb.local:443:10.0.0.97 https://opspb.local/docs/standards/preview-workflows.md`
9. Use the HTTPS `.local` URL in the final report:
   - `Preview URL:: Updated [Open preview](https://opspb.local/docs/standards/preview-workflows.md)`
10. Record cleanup commands:
   - `tmux kill-session -t opspb-standard-preview`
   - stop any repo-specific mDNS publisher if the route should not remain available

Important access limits:

- The operator's device must resolve `opspb.local` through the homelab DNS route or LAN mDNS.
- The browser may show a certificate warning unless it trusts the homelab local CA.
- If `http://opspb.local` works but the chat surface does not make it clickable, use `https://opspb.local` and verify the HTTPS route before reporting it.

## Verification Checklist

Before reporting a preview as updated, verify:

- the server process is running
- the process owner is the expected one, or the handoff explains why a fallback owner was used
- the chosen port responds from the agent machine
- the response is the expected app, not a different service already on that port
- the URL in the final report is the same URL that was verified or is a direct remote equivalent
- the final-report preview link uses a URL form the chat client can render as clickable, or the handoff records why no clickable URL is available
- UI changes are visually checked when the task affects user-facing screens
- the preview does not require production credentials unless the repo explicitly allows that mode
- logs do not show fatal startup errors

If any item is not possible, record the gap in `HANDOFF.md` or the repo's equivalent continuity doc and mark the final preview status accurately.

## Preview Handoff Requirements

For temporary previews, write a short durable note in `HANDOFF.md`, `STREAM.md`, or a dedicated preview doc. Include:

- preview status: not started, running, blocked, stopped, or superseded
- command used
- working directory
- host and port
- local URL
- remote URL, if available
- canonical reportable URL and any fallback URLs that were verified
- process ID, terminal session, service name, or platform deployment identifier
- whether any older service, tunnel, or named route was found and whether it is canonical, fallback, or non-reportable
- log location or how to view logs
- stop command
- validation performed
- known access limits

For long-lived or repeated preview needs, create a repo-specific setup doc from `templates/PREVIEW.template.md` and link it from `README.md` or `AGENTS.md`.

## Failure Recovery

When preview setup fails, agents should not keep retrying the same command blindly. They should:

1. Check whether another process owns the port.
2. Read the package scripts or repo setup docs again.
3. Confirm dependencies and environment variables.
4. Try the next safe port or repo-approved preview mode.
5. Record the blocker, attempted commands, and last useful error.
6. Ask for human input only when secrets, account access, firewall rules, or platform credentials are required.

## Security And Scope

Preview workflows must stay scoped to the target repo.

Do not:

- expose secrets through command lines, logs, URLs, screenshots, or final reports
- run production deploys to create a preview unless the repo explicitly defines that path
- create public tunnels for sensitive apps without approval
- leave undocumented background processes running
- claim a preview exists when only a build or static artifact was produced

## Template Adoption

Use `templates/PREVIEW.template.md` when a target repo needs a durable default preview procedure. The template should be specialized with the repo's actual commands, ports, remote-access method, validation steps, and stop procedure.
