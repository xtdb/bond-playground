(ns user
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]
            [xtdb.node :as xtn]))

(def bond-tx-ops
  (vec
   (for [doc (read-string (slurp (io/resource "james-bond.edn")))]
     [:put (:type doc)
      (-> doc
          (dissoc :type))])))

(def xt-node
  (let [node (xtn/start-node {})]
    (xt/submit-tx node bond-tx-ops)
    node))

(comment
  ;; `SELECT xt$id, person$name FROM person WHERE xt$id = 'daniel-craig'`
  (xt/q xt-node '(from :person [{:xt/id :daniel-craig}
                                xt/id person/name]))

  ;; Have fun!
  )
