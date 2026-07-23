(ns wastecollect.advisor
  "Garbage and Recycling Collector Advisor — proposing a collection-route
  scheduling/logistics coordination operation (log a collection-log/
  progress record, schedule a crew operation, flag a safety concern,
  coordinate a collection-equipment/consumables procurement order) from
  a crew roster, route registration and safety-reporting policy.
  Swappable mock/llm; the advisor ONLY proposes —
  `wastecollect.governor` independently gates every proposal and always
  escalates safety concerns and above-threshold supply orders. The
  advisor never proposes to directly finalize a collection-execution
  decision (e.g. authorizing a specific vehicle route/loading
  operation) or a route-safety-clearance decision (e.g. declaring a
  route cleared for safety), and never proposes to override a route
  safety supervisor's judgment — those stay permanently out of this
  actor's scope. Modeled closely on cloud-itonami-isco-9212's
  livestockfarm.advisor for the outdoor-labour hazard-domain shape,
  extended with a second, independent vehicle-traffic/hazardous-
  material-handling hazard-scope dimension.

  A proposal: {:op :log-work-record|:schedule-crew-operation|
               :flag-safety-concern|:coordinate-supply-order
               :effect :propose :worker-id str :route-id str
               :cost number :hazard-type kw :task str :stake kw
               :confidence n :rationale str}"
  (:require #?(:clj [clojure.edn :as edn] :cljs [cljs.reader :as edn])))

(defprotocol Advisor
  (-advise [advisor store request] "request -> proposal map"))

(defn- rationale-for [op worker-id route-id hazard-type]
  (case op
    :log-work-record
    (str "logged work record for worker " worker-id " at route " route-id)

    :schedule-crew-operation
    (str "scheduled crew operation for collection route task at route " route-id)

    :flag-safety-concern
    (str "flagged " (name (or hazard-type :hazard)) " concern for worker "
         worker-id " at route " route-id " — routed for route safety supervisor review")

    :coordinate-supply-order
    (str "coordinated supply order for worker " worker-id " at route " route-id)

    (str "proposed " (name op) " for worker " worker-id " at route " route-id)))

(defn- infer [_store {:keys [op stake worker-id route-id cost hazard-type task]
                       :as request}]
  {:op op
   :effect :propose
   :worker-id worker-id
   :route-id route-id
   :cost cost
   :hazard-type hazard-type
   :task task
   :stake (or stake :low)
   :confidence (case (or stake :low) :high 0.7 :medium 0.85 :low 0.95)
   :rationale (rationale-for op worker-id route-id hazard-type)})

(defn mock-advisor []
  (reify Advisor
    (-advise [_ store request] (infer store request))))

(def ^:private system-prompt
  "You are a garbage-and-recycling collection-route scheduling/logistics
   coordination advisor. Given a request, propose an :op (one of
   :log-work-record, :schedule-crew-operation, :flag-safety-concern,
   :coordinate-supply-order), the :worker-id, :route-id, and any
   :cost/:hazard-type/:task fields, an honest :confidence and a
   :stake. Never propose an op outside this closed list, and never
   propose to directly finalize a collection-execution decision (e.g.
   authorizing a specific vehicle route or loading operation to
   proceed), or a route-safety-clearance decision (e.g. declaring a
   route cleared for safety), or to override a route safety
   supervisor's judgment — those are always out of this actor's scope;
   it coordinates route scheduling/logistics only and never performs
   collection work itself or makes route-safety-clearance decisions
   itself. Safety concerns always require human sign-off regardless of
   confidence.")

(defn- parse-proposal [content]
  (try
    (let [p (edn/read-string content)]
      (if (map? p)
        (assoc p :effect :propose)
        {:op :unknown :effect :propose :confidence 0.0 :stake :high
         :rationale "unparseable LLM response"}))
    (catch #?(:clj Exception :cljs js/Error) _
      {:op :unknown :effect :propose :confidence 0.0 :stake :high
       :rationale "LLM response parse failure"})))

(defn llm-advisor
  [chat-model model-generate-fn gen-opts]
  (reify Advisor
    (-advise [_ _store request]
      (let [msgs [{:role :system :content system-prompt}
                  {:role :user :content (str "operation request: " (pr-str request))}]
            resp (model-generate-fn chat-model msgs gen-opts)]
        (parse-proposal (:content resp))))))
