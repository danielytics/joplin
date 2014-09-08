(ns leiningen.joplin
  (:require [leinjacker.deps :as deps])
  (:use [leiningen.run :only (run)]))

(def version "0.1.12-SNAPSHOT")

(def libs
  {:dt   "joplin.datomic"
   :cass "joplin.cassandra"
   :jdbc "joplin.jdbc"
   :es   "joplin.elasticsearch"
   :zk   "joplin.zookeeper"})

(defn- get-db-types [project]
  (->> project
       :joplin
       :databases
       vals
       (map :type)
       set))

(defn- add-dep [project type types]
  (if (and (contains? types type)
           (contains? libs type))
    (deps/add-if-missing project [(symbol (get libs type)) version])
    project))

(defn- add-joplin-deps [project]
  (let [types (get-db-types project)]
    (-> project
        (deps/add-if-missing '[joplin.core "0.1.12"])
        (add-dep :dt types)
        (add-dep :cass types)
        (add-dep :jdbc types)
        (add-dep :es types)
        (add-dep :zk types))))

(defn- get-require-string [types extra-libs]
  (->> types
       (keep (conj libs extra-libs))
       (map #(str % ".database"))
       (interpose ",")
       (apply str)))

(defn joplin
  "Migrate and seed datastores"
  [project command & args]
  (let [environments (-> project :joplin :environments)
        databases    (-> project :joplin :databases)
        migrators    (-> project :joplin :migrators)
        seeds        (-> project :joplin :seeds)
        extra-libs   (-> project :joplin :libs)
        project      (add-joplin-deps project)]
    (apply run project
           "-m" "joplin.main"
           "-r" (get-require-string (get-db-types project) extra-libs)
           "-e" environments
           "-d" databases
           "-m" migrators
           "-s" seeds
           command args)))
