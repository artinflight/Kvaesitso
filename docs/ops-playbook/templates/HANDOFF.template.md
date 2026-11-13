# HANDOFF.md

## What Changed This Session

-

## What Is True Right Now

-

## Delegated Agent State

- Active delegated agents:
- Latest durable checkpoints:
- Files touched by delegated agents:
- Blockers or uncertainty:
- Final delegated-agent disposition:

## Integration Lane State When Applicable

- Lane branch:
- Collection PR:
- Integrated slices:
- Pending slices:
- Superseded PRs or branches:
- Lane freshness:
- Final validation status:
- Cleanup still needed:

## Preview State When Applicable

- Status:
- Command:
- Host and port:
- Local URL:
- Remote-access URL:
- Process/session/deploy identifier:
- Validation performed:
- Logs:
- Stop command:
- Access limits or blockers:

## Usage / Cost State When Applicable

- Paid or potentially billable resources enabled:
- Necessity and proportionality conclusion:
- Lowest adequate tier or size selected:
- Expected cost and billing basis:
- Intended duration:
- Authorization basis or operator decision:
- Cleanup, scale-down, or stop command:
- Residual billing risk:

## What The Next Agent Should Do

-

## What The Next Agent Must Not Do

-

## Verification Required Before Further Changes

-

## Release Checklist When Applicable

- `VERSION.json` or the repo's equivalent version source is updated.
- Version source includes `version`, `tag`, and `releaseNotesId`.
- Version tag equals `v` plus version.
- Package manifest and lockfile versions match the version source.
- Top changelog or release-notes entry matches the version tag and release-notes ID.
- Version tag uses unpadded SemVer, for example `v0.96.0` or `v0.100.0`.
- Staging or preview metadata does not create production release records.
- Production release automation creates or updates the release matching the version tag.
- Deploy metadata records commit, ref, build time, and release URL without exposing secrets.
- Native platform versioning remains separate unless this repo explicitly says otherwise.

## Session Metadata

- Validation status:
- PR:
- Commit:
- Preview URL:
