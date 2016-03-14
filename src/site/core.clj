(ns site.core
  (:gen-class))

(require '[ring.adapter.jetty :as ring-jetty]
         '[hiccup.core :as h]
         '[ring.middleware.reload :refer [wrap-reload]]
         '[ring.middleware.stacktrace :refer [wrap-stacktrace]]
         '[ring.util.response :as r]
         '[markdown.core :as markdown]
         '[clojure.string :as str]
         '[clj-toml.core :as toml])


(defn styles []
  [:style (slurp "static/site.css")])

(defn default [a b]
  (if (nil? a) b a))

(defn ls
  [path]
  (let [file (java.io.File. path)]
    (if (.isDirectory file)
      (seq (.list file))
      (when (.exists file)
        [path]))))

(defn list-articles []
   (ls "static/articles"))

(defn name->path [article-name]
  (str/join ["static/articles/" article-name]))

(defn parse-toml [string]
  (toml/parse-string string))

(defn load-config []
  (-> "config.toml"
      (slurp)
      (parse-toml)))

;;; Try to slurp a file. If it doesn't exist, return "".
(defn try-slurp [path]
  (let [file (java.io.File. path)]
    (if (.exists file) (slurp path)
      "")))

(defn load-article-file [article-name file-name]
  (let [art-path (name->path article-name)
        file-path (str/join "/" [art-path file-name])]
    (try-slurp file-path)))

(defn load-custom-script [article-name]
  (load-article-file article-name "script.js"))

(defn load-custom-style [article-name]
  (load-article-file article-name "style.css"))

(defn split-content-and-meta [article-text]
  (let [sections (str/split article-text #"~~~")]
    (if (= 1 (count sections))
      [(first sections) {}]
      [(second sections) (first sections)])))

(def parse-metadata parse-toml)

(defn load-content [article-name]
  (let [file-content (load-article-file article-name "content.md")
        [content metadata] (split-content-and-meta file-content)]
    [content (parse-metadata metadata)]))

(defn load-article [article-name]
  (let [[content metadata] (load-content article-name)]
    {:name article-name
     :content content
     :metadata metadata
     :script (load-custom-script article-name)
     :style (load-custom-style article-name)
     }))

(defn load-articles []
  (map load-article (list-articles)))

(defn article-view [article]
  [:article.article
   [:style (:style article)]
   [:script (:script article)]
   (markdown/md-to-html-string (:content article))
   ])

(defn front-page []
  (h/html
    [:html
     [:head
      (styles)
      [:title "Hello"]]
     [:body
     (map article-view (load-articles))
     ]]))

(defn hello-handler [request]
  (-> (r/response (front-page))
      (r/content-type "text/html")))

(def reloadable-app
  (-> #'hello-handler
      wrap-reload
      wrap-stacktrace))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (ring-jetty/run-jetty #'reloadable-app {:port 3000}))
