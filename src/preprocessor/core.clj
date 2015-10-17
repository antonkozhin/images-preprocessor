(ns preprocessor.core
  (:require [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [clojure.data.xml :as xml]
            [clojure.xml])
  (:gen-class))

(defn parse-int [s]
  (Integer. (re-find #"\d+" s)))

(defn grade [file-name grades]
  (let [rate (parse-int file-name)]
    (nth grades (dec rate))))

#_(defn file-element [file-name grades]
  (xml/element (keyword file-name) {} (grade file-name grades)))

#_(defn write-xml [result-path file-names grades]
    (let [elements (map #(file-element % grades) (apply concat file-names))
          res (xml/element :result {} elements)]
      (with-open [out-file (java.io.OutputStreamWriter.
                            (java.io.FileOutputStream. (str result-path "/" "result.xml")) "UTF-8")]
        (xml/emit res out-file))))

(defn file-element [file-name grades]
  {:tag (keyword file-name) :content [(grade file-name grades)]})

(defn create-content [file-names grades]
  (mapv #(file-element % grades) file-names))

(defn write-xml [result-path file-names grades]
  (spit (str result-path "/" "result.xml") (with-out-str (clojure.xml/emit {:tag "result" :content (create-content  (apply concat file-names) grades)}))))

(defn copy-file [file dir-name result-path]
  (let [file-name (str dir-name "-" (fs/name file))]
    (fs/copy+ file (str result-path "/" file-name))
    file-name))

(defn handle-dir [dir result-path]
  (let [dir-name (fs/name dir)]
    (println "processing directory: " dir-name)
    (doall (map #(copy-file % dir-name result-path) (fs/list-dir dir)))))

(defn start [{:keys [dir-path result-path grades]}]
  (println "Start")
  (write-xml result-path (doall (map #(handle-dir % result-path) (fs/list-dir dir-path))) (read-string grades)))

(defn -main [& args]
  (let [[options args banner]
        (cli/cli args
                 ["-h" "--help" :flag true :default false]
                 ["--dir-path" "Directory with images" :default "/"]
                 ["--result-path" "Directory for result" :default "./preprocesing-result"]
                 ["--grades" "List of grades. It should be 5 grades"])]
    (if (:help options)
      (println banner)
      (if (not= (count (read-string (:grades options))) 5)
        (do
          (println "Count of grades isn't equal to five")
          (println banner))
        (start options)))))
