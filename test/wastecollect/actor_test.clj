(ns wastecollect.actor-test
  (:require [clojure.test :refer [deftest is testing]]
            [wastecollect.actor :as actor]
            [wastecollect.store :as store]))

(defn- fresh-store []
  (let [st (store/mem-store)]
    (store/register-worker! st {:worker-id "worker-1" :name "Kenji Sato"})
    (store/register-route! st {:route-id "ROUTE-1" :name "Sato Ward Collection Route" :max-supply-cost 2000})
    st))

(deftest commits-a-registered-work-log
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:worker-id "worker-1" :op :log-work-record :stake :low
                  :route-id "ROUTE-1" :task "collection progress log"}
        result (actor/run-request! graph request {} "thread-1")]
    (is (= :done (:status result)))
    (is (some? (get-in result [:state :record])))
    (is (= 1 (count (store/records-of st "worker-1"))))))

(deftest holds-an-unregistered-route-proposal
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:worker-id "worker-1" :op :log-work-record :stake :low
                  :route-id "ROUTE-ghost" :task "collection progress log"}
        result (actor/run-request! graph request {} "thread-2")]
    (is (= :hold (:disposition (:state result))))
    (is (empty? (store/records-of st "worker-1")))))

(deftest interrupts-then-approves-safety-concern-on-human-approval
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:worker-id "worker-1" :op :flag-safety-concern :stake :low
                  :route-id "ROUTE-1" :hazard-type :vehicle-traffic-hazard}
        interrupted (actor/run-request! graph request {} "thread-3")]
    (is (= :interrupted (:status interrupted)))
    (is (empty? (store/records-of st "worker-1")))
    (let [resumed (actor/approve! graph "thread-3")]
      (is (= :done (:status resumed)))
      (is (= 1 (count (store/records-of st "worker-1")))))))

(deftest holds-a-scope-excluded-collection-execution-op-even-at-high-confidence
  (testing "an actor run can never commit a proposal that would finalize a collection-execution decision, regardless of disposition path"
    (let [st (fresh-store)
          graph (actor/build-graph {:store st})
          request {:worker-id "worker-1" :op :authorize-vehicle-route-operation :stake :low
                    :route-id "ROUTE-1" :task "route dispatch decision"}
          result (actor/run-request! graph request {} "thread-4")]
      (is (= :done (:status result)))
      (is (= :hold (:disposition (:state result))))
      (is (empty? (store/records-of st "worker-1"))))))

(deftest holds-a-scope-excluded-route-safety-clearance-op-even-at-high-confidence
  (testing "an actor run can never commit a proposal that would finalize a route-safety-clearance decision (e.g. declaring a route cleared for safety), regardless of disposition path"
    (let [st (fresh-store)
          graph (actor/build-graph {:store st})
          request {:worker-id "worker-1" :op :declare-route-safety-cleared :stake :low
                    :route-id "ROUTE-1" :task "route safety clearance"}
          result (actor/run-request! graph request {} "thread-5")]
      (is (= :done (:status result)))
      (is (= :hold (:disposition (:state result))))
      (is (empty? (store/records-of st "worker-1"))))))
