# Governance

`cloud-itonami-isco-9611` is an OSS open-occupation blueprint. Governance covers
both code and the operator model.

## Maintainers

Maintainers may merge changes that preserve these invariants:

- the Advisor cannot directly dispatch robot actions or disclose records.
- WasteCollectionGovernor remains independent of the advisor.
- hard policy violations cannot be overridden by human approval.
- a collection-execution decision, a route-safety-clearance decision
  (e.g. declaring a route cleared for safety), and any override of a
  route safety supervisor's judgment, stay permanently outside this
  actor's op-allowlist.
- every commit, hold and approval path is auditable.
- real worker/route/operator data stays outside Git.

## Decision Records

Architecture decisions live in `docs/adr/`. Changes to the trust model,
storage contract, public business model, operator certification, license, or
the closed op-allowlist should add or update an ADR.

## Operator Governance

Anyone may fork and operate independently. itonami.cloud certification is a
separate trust mark and should require security, audit, support and data-flow
review.

Certified operators can lose certification for:

- bypassing policy checks
- mishandling worker/route/operator data
- misrepresenting certification status
- failing to respond to security incidents
- hiding material changes to customer-facing operation
- attempting to route a collection-execution decision, a route-
  safety-clearance decision, or a route-safety-supervisor override
  decision, through this actor
