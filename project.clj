(defproject site "0.1.0-SNAPSHOT"
  :description "My personal website"
  :url "http://samdoiron.co"
  :license {:name "GNU General Public License v3"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [
                 [org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [hiccup "1.0.5"]
                 [markdown-clj "0.9.86"]
                 [clj-toml "0.3.1"]
                 [compojure "1.5.0"]
                ]
  :main ^:skip-aot site.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
