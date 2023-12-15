(ns user
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]
            [xtdb.node :as xtn]))

(def bond-tx-ops
  (vec
   (for [doc (read-string (slurp (io/resource "james-bond.edn")))]
     (xt/put (:type doc)
             (-> doc
                 (dissoc :type))))))

(def xt-node
  (let [node (xtn/start-node {})]
    (xt/submit-tx node bond-tx-ops)
    node))

(comment
  ;; `SELECT xt$id, person$name FROM person WHERE xt$id = 'daniel-craig'`
  (xt/q xt-node '(from :person [{:xt/id :daniel-craig}
                                xt/id person/name]))

  (xt/q xt-node '(-> (unify (from :film [{:film/bond :daniel-craig}
                                         film/bond film/name])
                            (from :person [{:xt/id film/bond} person/name]))
                     (without :film/bond)))

  (xt/q xt-node '(-> (unify (from :film [{:film/bond-girls bond-girls} film/name])
                            (unnest {bond-girl bond-girls})
                            (from :person [{:xt/id bond-girl} person/name]))
                     (return film/name person/name)
                     (limit 2)))

  ;; Have fun!
  )
