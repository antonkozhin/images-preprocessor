(defproject images-preprocessor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/data.xml "0.0.8"]]
  :source-paths ["src"]
  :uberjar-name "images-preprocessor.jar"
  :main ^:skip-aot preprocessor.core
  :profiles {:uberjar {:aot :all}})
