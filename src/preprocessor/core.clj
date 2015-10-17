(ns preprocessor.core
  (:require [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [clojure.data.xml :as xml]
            [clojure.xml])
  (:gen-class))

(defn parse-rate [file-name key-rate]
  (let [rate (re-find #"\d+" file-name)]
    (cond
     (keyword? key-rate) (keyword rate)
     :else (Integer. rate))))

(defn grade [file-name grades]
  (let [rate (parse-rate file-name (first (keys grades)))]
    (grades rate)))

(defn file-element [file-name grades]
  {:tag (keyword file-name) :content [(grade file-name grades)]})

(defn create-content [file-names grades]
  (doall (map #(file-element % grades) file-names)))

(defn write-xml [result-path grades file-names]
  (spit (str result-path "/" "result.xml") (with-out-str (clojure.xml/emit {:tag "result" :content (create-content (apply concat file-names) grades)}))))

(defn copy-file [file dir-name result-path]
  (let [file-name (str dir-name "-" (fs/name file))]
    (fs/copy+ file (str result-path "/" file-name))
    file-name))

(defn handle-dir [dir result-path]
  (let [dir-name (fs/name dir)]
    (println "processing directory: " dir-name)
    (doall (map #(copy-file % dir-name result-path) (fs/list-dir dir)))))

(defn start [{:keys [dir-path result-path grades-path]}]
  (println "Start")
  (let [grades (read-string (slurp grades-path))]
    (write-xml result-path grades (doall (map #(handle-dir % result-path) (fs/list-dir dir-path))))))

(defn -main [& args]
  (let [[options args banner]
        (cli/cli args
                 ["-h" "--help" :flag true :default false]
                 ["--dir-path" "Directory with images." :default "/"]
                 ["--result-path" "Directory for result." :default "./preprocesing-result"]
                 ["--grades-path" "File with grades."])]
    (if (:help options)
      (println banner)
        (start options))))
