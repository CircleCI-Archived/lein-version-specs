(defproject lein-version-spec "0.0.2"
  :version-spec "0.1.~{:env/circle_build_num}-~{:env/circle_sha1}"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/core.incubator "0.1.1"]]
  :eval-in-leiningen true)
