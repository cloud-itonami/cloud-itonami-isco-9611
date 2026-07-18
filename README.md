# cloud-itonami-isco-9611

Open Occupation Blueprint for **ISCO-08 9611**: Garbage and Recycling
Collectors.

This repository designs a forkable OSS business for a garbage-and-
recycling collection-route scheduling and logistics coordination
practice: a route scheduling and supply-coordination robot manages
crew/route records under a governor-gated actor, so a garbage-and-
recycling collection crew keeps its own operating records instead of
renting a closed workforce-management SaaS.

**Maturity: `:implemented`.** `src/wastecollect/` implements the
`WasteCollectionActor` as a `langgraph.graph/state-graph`
(`wastecollect.actor`) wired to a `Garbage and Recycling Collector
Advisor` (`wastecollect.advisor`) and an independent
`WasteCollectionGovernor` (`wastecollect.governor`), following the
itonami actor pattern (ADR-2607121000): `:intake -> :advise -> :govern
-> :decide -+-> :commit (:ok?) +-> :request-approval (:escalate?,
human-in-the-loop interrupt) +-> :hold (:hard?)`. 24 tests / 52
assertions green (`clojure -M:test`). HARD invariants (always hold,
never overridable): worker provenance, route provenance, no-actuation
(`:effect` must be `:propose`), a closed op-allowlist
(`:log-work-record`, `:schedule-crew-operation`,
`:flag-safety-concern`, `:coordinate-supply-order` — nothing else may
ever be proposed), and a permanent, unconditional block on any
proposal that would directly finalize a collection-execution decision
(e.g. authorizing a specific vehicle route/loading operation) *or* a
route-safety-clearance decision (e.g. declaring a route cleared for
safety), or that would override a route safety supervisor's judgment.
Always-escalate paths (human sign-off regardless of confidence,
mapping this repo's Trust Controls in
[`docs/business-model.md`](docs/business-model.md)):
`:flag-safety-concern` (always) and `:coordinate-supply-order` above
the registered cost threshold.

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot
performs the physical domain work**. Here a route scheduling/logistics
coordination robot performs crew scheduling, collection-log/progress-
record logging and collection-equipment/consumables procurement
coordination for a garbage-and-recycling collection crew, under an
actor that proposes actions and an independent
**WasteCollectionGovernor** that gates them. The governor never
dispatches hardware itself, never performs collection work on the
route itself, and never finalizes a collection-execution decision or a
route-safety-clearance decision, and never overrides a route safety
supervisor's judgment; `:high`/`:safety-critical` actions (such as a
flagged vehicle-traffic-hazard/hazardous-material-handling-hazard/
equipment-condition concern, or an above-threshold supply order)
require human sign-off. **This actor coordinates ROUTE
SCHEDULING/LOGISTICS ONLY — it never performs collection work itself
and never makes a route-safety-clearance decision itself.**

## Core Contract

```text
worker roster + route registration + safety-reporting policy
        |
        v
Garbage and Recycling Collector Advisor -> WasteCollectionGovernor -> log/schedule/coordinate, or human sign-off
        |
        v
robot actions (gated) + operating records + audit ledger
```

No automated advice can dispatch a robot action the governor refuses,
finalize a collection-execution decision, finalize a route-safety-
clearance decision (e.g. declaring a route cleared for safety),
override a route safety supervisor's judgment, suppress an operating
record, or disclose sensitive data without governor approval and audit
evidence.

## Capability layer

Resolves via [`kotoba-lang/occupation`](https://github.com/kotoba-lang/occupation)
(ISCO-08 `9611`). Required capabilities:

- :robotics
- :identity
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.
