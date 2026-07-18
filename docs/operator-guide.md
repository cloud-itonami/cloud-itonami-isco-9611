# Operator Guide

## First Deployment

1. Define the operator's route coverage and crew intake process.
2. Define consent and purpose categories for worker/route records.
3. Run synthetic operating cases (work-log entry, crew-operation
   scheduling, supply coordination, safety-concern flagging).
4. Enable human-reviewed sign-off for `:high`/`:safety-critical` actions
   (all flagged safety concerns, above-threshold supply orders).
5. Measure operating outcomes and audit coverage.

## Minimum Production Controls

- consent and disclosure log
- safety-critical escalation path (vehicle-traffic hazard,
  hazardous-material-handling hazard, equipment-condition hazard)
- provenance for all operating records (worker and route both
  independently registered)
- human review for high-risk cases
- audit export for all gated actions
- a hard, unconditional block on any attempt to route a collection-
  execution decision, a route-safety-clearance decision (e.g.
  declaring a route cleared for safety), or a route-safety-supervisor
  override decision, through this actor — those decisions stay a route
  safety supervisor's exclusive authority end to end

## Certification

Certified operators must prove that the governor gates every
safety-critical robot action, that safety-critical risks escalate to
humans, and that no deployment configuration can route a collection-
execution decision, a route-safety-clearance decision, or a
route-safety-supervisor judgment override through this actor.
