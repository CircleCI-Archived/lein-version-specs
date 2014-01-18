(ns leiningen.version-spec
  (:refer-clojure :exclude (resolve))
  (:require [clojure.string :as str]
            [clojure.core.strint :as strint]))

(defn env-keyword? [x]
  {:post [(do (println "env-keyword?" x "=" %) true)]}
  (and (keyword? x) (= "env" (namespace x))))

(defn form? [x]
  {:post [(do (println "form?" x "=" %) true)]}
  (and (list? x)
       (-> x first symbol?)))

(defn form-ns [x]
  (-> x first namespace))

(defn call-form [x]
  {:post [(do (println "call-form" x "=" %) true)]}
  (let [v (first x)
        v-ns (symbol (namespace v))
        v-name (symbol (name v))]
    (require v-ns)
    (let [v (ns-resolve v-ns v-name)]
      (assert v)
      (apply v (rest x)))))

(defn get-env [x]
  (let [var-name (str/upper-case (name x))
        val (System/getenv var-name)]
    (when-not val
      (leiningen.core.main/abort (str "env var " var-name "not found")))))

(defn resolve [x]
  (cond
   (env-keyword? x) (get-env x)
   (form? x) (call-form x)
   (string? x) x))

(defn parse-version-spec [spec]
  (apply str (map resolve (#'strint/interpolate spec))))

(defn version-spec
  "I don't do a lot."
  [project & args]
  (println (parse-version-spec (:version-spec project))))
