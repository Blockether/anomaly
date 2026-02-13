(ns build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def lib 'com.blockether/anomaly)
(def version
  (let [v (System/getenv "VERSION")]
    (if (and v (.startsWith v "v"))
      (subs v 1)
      (or v "0.0.1-SNAPSHOT"))))

(def class-dir "target/classes")
(def jar-file (format "target/%s.jar" (name lib)))
(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (clean nil)
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis @basis
                :src-dirs ["src"]
                :pom-data [[:description "Blockether anomalies - based on Cognitect anomalies"]
                           [:url "https://github.com/Blockether/anomaly"]
                           [:licenses
                            [:license
                             [:name "Eclipse Public License 1.0"]
                             [:url "http://www.eclipse.org/legal/epl-v10.html"]]]]})
  (b/copy-dir {:src-dirs ["src"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file})
  (println "Built:" jar-file "version:" version))

(defn deploy [_]
  (jar nil)
  (dd/deploy {:installer :remote
              :artifact jar-file
              :pom-file (b/pom-path {:lib lib :class-dir class-dir})}))
