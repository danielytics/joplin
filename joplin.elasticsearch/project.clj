(defproject joplin.elasticsearch "0.1.12-SNAPSHOT"
  :description "ElasticSearch support for Joplin"
  :url "http://github.com/juxt/joplin"
  :scm {:name "git"
        :url "https://github.com/juxt/joplin"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clojurewerkz/elastisch "2.1.0-beta6"]
                 [joplin.core "0.1.12-SNAPSHOT"]
                 [clj-time "0.7.0"]])
