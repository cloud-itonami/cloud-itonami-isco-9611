(ns wastecollect.store
  "SSoT for the ISCO-08 9611 garbage-and-recycling-collection-route
  scheduling/logistics coordination actor (itonami actor pattern,
  ADR-2607121000 / CLAUDE.md Actors section; README's 'Robotics
  premise' — a route scheduling/logistics coordination robot performs
  crew scheduling, collection-log/progress-record logging and
  collection-equipment/consumables procurement coordination for a
  garbage-and-recycling collection crew under this advisor/governor
  pair, which never dispatches hardware itself, never performs
  collection work itself, and never finalizes a collection-execution
  decision or a route-safety-clearance decision, and never overrides a
  route safety supervisor's judgment — those remain the route safety
  supervisor's exclusive judgment). Modeled closely on
  cloud-itonami-isco-9212's livestockfarm.store for the outdoor-labour
  hazard-domain shape, extended with a second, independent
  vehicle-traffic/hazardous-material-handling hazard-scope dimension
  (garbage and recycling collectors work alongside moving collection
  vehicles in traffic and handle waste materials that may include
  sharp/hazardous items).

  Domain:

    worker — a registered garbage-and-recycling collection crew member
             (:worker-id, :name)
    route  — a registered collection route {:route-id :name
             :max-supply-cost number}. `:max-supply-cost` is an
             informational registered ceiling used only to decide
             whether a `:coordinate-supply-order` proposal escalates to
             human sign-off (the governor never blocks a
             within-threshold order outright; it only decides commit
             vs. escalate).
    record — a committed operating record (a logged collection-log/
             progress entry, a scheduled crew operation, a flagged
             safety concern, or a coordinated supply order) — written
             ONLY via commit-record!.
    ledger — append-only audit trail, commit or hold.")

(defprotocol Store
  (worker [s worker-id])
  (route [s route-id])
  (records-of [s worker-id])
  (ledger [s])
  (register-worker! [s worker])
  (register-route! [s route])
  (commit-record! [s record])
  (append-ledger! [s fact]))

(defrecord MemStore [a]
  Store
  (worker [_ worker-id] (get-in @a [:workers worker-id]))
  (route [_ route-id] (get-in @a [:routes route-id]))
  (records-of [_ worker-id] (filter #(= worker-id (:worker-id %)) (:records @a)))
  (ledger [_] (:ledger @a))
  (register-worker! [s w]
    (swap! a assoc-in [:workers (:worker-id w)] w) s)
  (register-route! [s r]
    (swap! a assoc-in [:routes (:route-id r)] r) s)
  (commit-record! [s record]
    (swap! a update :records (fnil conj []) record) s)
  (append-ledger! [s fact]
    (swap! a update :ledger (fnil conj []) fact) s))

(defn mem-store
  ([] (mem-store {}))
  ([seed] (->MemStore (atom (merge {:workers {} :routes {} :records [] :ledger []}
                                    seed)))))
