(ns rethinkdb.database
  "RethinkDB interface for Joplin"
  (:use [joplin.core])
  (:require [bitemyapp.revise.query :as r]
            [bitemyapp.revise.connection :as rdb]
            [bitemyapp.revise.core :as rev]
            [ragtime.core :refer [Migratable]]))

;; =============================================================

(defn- ensure-migration-schema
  "Ensures the migration schema is loaded"
  [conn db]
  (-> (r/db db)
      (r/table-create-db :migrations)
      (rev/run conn)))

(defn get-connection [{:keys [host port auth-key]}]
  (if host
    (rdb/connect {:host     host
                  :port     (and port 28015)
                  :auth-key (and auth-key "")})
    (rdb/connect)))

;; ============================================================================
;; Ragtime interface

(defrecord RethinkDatabase [db-conf db-name]
  Migratable
  (add-migration-id [db id]
    (let [conn (get-connection db-conf)]
      (ensure-migration-schema conn db-name)
      (-> (r/db db-name)
          (r/table-db :migrations)
          (r/insert {:id id, :created_at (-> (r/now) (r/date))})
          (rev/run conn))))

  (remove-migration-id [db id]
    (let [conn (get-connection db-conf)]
      (ensure-migration-schema conn db-name)
      (-> (r/db db-name)
          (r/table-db :migrations)
          (r/get id)
          (r/delete)
          (rev/run conn))))

  (applied-migration-ids [db]
    (let [conn (get-connection db-conf)]
      (ensure-migration-schema conn db-name)
      (-> (r/db db-name)
          (r/table-db :migrations)
          (r/order-by (r/asc :created_at))
          (r/get-field :id)
          (rev/run conn)
          :response
          first)  )))

(defn- ->RDatabase [target]
  (map->RethinkDatabase
    {:db-conf (select-keys (:db target) [:host :port :auth-key ])
     :db-name (get-in target [:db :db-name])}) )

;; ============================================================================
;; Joplin interface

(defmethod migrate-db :rethinkdb [target & args]
  (do-migrate (get-migrations (:migrator target)) (->RDatabase target)))

(defmethod rollback-db :rethinkdb [target & [n]]
  (do-rollback (get-migrations (:migrator target))
               (->RDatabase target)
               n))

(defmethod seed-db :rethinkdb [target & args]
  (let [migrations (get-migrations (:migrator target))]
    (do-seed-fn migrations (->RDatabase target) target args)))

(defmethod reset-db :rethinkdb [target & args]
  (do-reset (->RDatabase target) target args))

(defmethod create-migration :rethinkdb [target & [id]]
  (do-create-migration target id "rethinkdb.database"))

