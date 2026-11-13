# Usage And Cost Control Standard

## Purpose

This document defines the baseline rule for any agent action that can create, start, enable, scale, or keep running a paid or potentially billable service.

The operator must know the expected cost before an agent enables a billable resource. The primary safety gate is the agent's judgment that spending is necessary and proportionate, not repetitive approval of spending the task already implies.

## Mandatory Rule

Before enabling any service, resource, feature, workflow, preview, deployment, storage, runner, seat, domain, tunnel, API, model, database, queue, cache, VM, container, job, or integration that may create cost, the agent must:

1. Determine whether the requested outcome can be achieved adequately through a no-cost path, existing resource, local execution, manual one-off action, mock, fixture, dry run, or deferred execution.
2. Confirm that spending is necessary to complete the requested outcome now rather than merely faster, easier, more durable, or more convenient.
3. Identify the lowest priced adequate resource, tier, size, region, quota, duration, retention setting, and usage bound.
4. Judge whether the expected and plausible maximum cost are commensurate with the task's scope, duration, and value.
5. Tell the operator the expected cost in plain English before incurring it and state whether it is one-time, hourly, monthly, usage-based, trial-covered, free-tier-covered, or unknown.

If an adequate no-cost or lower-cost path exists, the agent must use it. If the agent cannot determine the price or billing impact with reasonable confidence, or concludes that the spend may be disproportionate, it must stop before enabling the resource and ask the operator how to proceed.

## Necessity And Proportionality Self-Audit

The agent must answer these questions before each action that can start or increase spend:

1. Is cost unavoidable for the requested outcome, or can an adequate result be produced for less or at no cost?
2. Is the paid action required now, or can existing infrastructure, local work, a manual action, or a shorter-lived resource accomplish the task?
3. What is the minimum capacity, duration, retention, call volume, concurrency, and exposure needed?
4. What is the expected cost, what is the credible maximum exposure, and are both proportionate to the result being requested?
5. What control stops or limits spend, and what cleanup happens immediately after the resource is no longer needed?

The audit is a decision requirement, not a reporting ritual. An agent must change course when its answers reveal an adequate cheaper path or spending that is not commensurate with the work.

## Scope

This rule applies to any paid or potentially paid system, including but not limited to:

- application hosting and deploy platforms, such as Fly.io, Vercel, Netlify, Cloudflare, Render, Railway, Heroku, AWS, GCP, Azure, DigitalOcean, or similar providers
- source-control and automation platforms, such as GitHub paid runners, GitHub Actions minutes, GitHub storage, GitHub Codespaces, package registries, and seat-based features
- preview environments, public tunnels, fixed preview lanes, managed DNS, domains, certificates, and hosted logs
- databases, object storage, caches, queues, analytics, observability, email, SMS, push notification, search, and AI or API usage
- any service where scaling, retention, concurrency, seats, regions, bandwidth, uptime, or background jobs can change cost

Local no-cost processes on the agent machine do not need a cost disclosure, but the agent must not assume that a local command is free if it starts or configures a paid external service.

## Lowest Adequate Resource Rule

Agents must choose the smallest and lowest priced option that can accomplish the task safely.

The default choice should minimize:

- instance size, CPU, memory, storage, and replica count
- always-on duration
- region or zone cost when the repo does not require a specific one
- log, metric, artifact, backup, cache, and data retention
- paid runner size, concurrency, and job duration
- paid seats, add-ons, managed features, and public exposure

Agents must not select a larger, faster, more durable, or more public paid option just because it is convenient. A higher-cost option is allowed only when the lower-cost option is demonstrably inadequate and the higher cost remains proportionate to the requested outcome.

## Required Cost Disclosure

The pre-enablement message should include:

- provider or service name
- resource or feature being enabled
- selected lowest adequate tier or size
- expected price and billing unit, such as per hour, per month, per request, per GB, per build minute, or free-tier limit
- expected duration or cleanup plan
- cheaper alternatives considered when relevant
- why spending is necessary and commensurate with the requested outcome
- what happens if usage exceeds the estimate

Use plain English. If exact pricing depends on usage, state the pricing basis and the assumed usage for the task.

## Authorization And Escalation

An operator's request to complete work authorizes cost-bearing actions when all of these are true:

- the spend is necessary to achieve the requested outcome
- no adequate lower-cost or no-cost path exists
- the cost and credible maximum exposure are proportionate to the task
- the action stays within the current request and documented repo limits
- the agent uses the lowest-cost adequate configuration and states the expected cost before incurring it

Agents must not ask the operator to reapprove every cost-bearing action solely because it has a cost. Repeated approval transfers the agent's cost judgment to the operator and does not satisfy the self-audit requirement.

The agent must stop for an operator decision when the price is unknown, the spend appears disproportionate, the action creates a new recurring commitment outside the requested scope, a cost ceiling would be exceeded, or materially different cost/outcome choices require operator judgment. Do not enable first and explain later.

## Handoff And Cleanup

When a paid or potentially billable resource is enabled, the agent must record durable handoff context:

- resource name and provider
- selected tier, size, region, and billing basis
- expected cost and intended duration
- necessity and proportionality conclusion
- authorization basis and any operator decision, if applicable
- stop, scale-down, deletion, or cleanup command
- validation performed
- residual cost risk, such as metered traffic, storage growth, or retained logs

The final user-facing report must mention the paid resource and the cleanup or next billing-control step.

## Forbidden Shortcuts

Agents must not:

- enable a paid resource before stating cost
- hide cost details only in logs, PR bodies, or handoff files
- assume a provider's free tier applies without checking the selected resource and current task
- choose default platform settings when a cheaper adequate option exists
- leave paid preview, deployment, runner, storage, tunnel, or test data running without a documented stop or cleanup path
- treat task authorization as permission for unnecessary or disproportionate spending
- ask for routine approval instead of deciding whether spending is necessary and commensurate
- convert a local preview or validation task into a paid deploy when the local path is adequate
