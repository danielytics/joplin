(defproject joplin "0.2.3-SNAPSHOT"
  :description "Flexible datastore migration and seeding"
  :url "http://github.com/juxt/joplin"
  :scm {:name "git"
        :url "https://github.com/juxt/joplin"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [joplin.core "0.2.3-SNAPSHOT"]
                 [joplin.jdbc "0.2.3-SNAPSHOT"]
                 [joplin.elasticsearch "0.2.3-SNAPSHOT"]
                 [joplin.zookeeper "0.2.3-SNAPSHOT"]
                 [joplin.datomic "0.2.3-SNAPSHOT"]
                 [joplin.cassandra "0.2.3-SNAPSHOT"]
                 [clojurewerkz/ragtime "0.4.0"]]
  :plugins [[lein-sub "0.3.0"]]
  :sub ["joplin.core" "joplin.jdbc" "joplin.elasticsearch" "joplin.zookeeper"
        "joplin.datomic" "joplin.cassandra" "joplin.lein"])
