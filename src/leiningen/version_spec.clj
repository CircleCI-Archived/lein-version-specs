(ns leiningen.version-spec
  (:refer-clojure :exclude (resolve))
  (:require [clojure.string :as str]
            [clojure.core.strint :as strint]
            [leiningen.core.main :as main]
            [rewrite-clj.zip :as z]))

(defn env-keyword? [x]
  (and (keyword? x) (= "env" (namespace x))))

(defn form? [x]
  (and (list? x)
       (-> x first symbol?)))

(defn form-ns [x]
  (-> x first namespace))

(defn call-form [x]
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
      (leiningen.core.main/abort (str "env var " var-name " not found")))))

(defn resolve [x]
  (cond
   (env-keyword? x) (get-env x)
   (form? x) (call-form x)
   (string? x) x))

(defn parse-version-spec [spec]
  (apply str (map resolve (#'strint/interpolate spec))))

(defn new-project [project-zip new-version]
  (-> project-zip
      (z/find-value z/next 'defproject)
      z/right ;; project name
      z/right ;; version
      (z/replace new-version)))

(defn version-spec
  "rewrite the project.clj, updating the version number according to the version spec"
  [project & args]
  (let [project-path "project.clj"
        new-version (parse-version-spec (:version-spec project))
        project-zip (z/of-file project-path)
        updated-proj (new-project project-zip new-version)]
    (spit project-path (z/print-root updated-proj))
    (main/info "updated")
    (main/exit 0)))
