(ns site.core
  (:require [ring.adapter.jetty :as ring-jetty]
            [hiccup.core :as h]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.util.response :as r]
            [clojure.string :as str]
            [site.views :as views]
            [clj-toml.core :as toml]
            [compojure.core :refer :all]
            [compojure.route :as route]
  (:gen-class)))

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
   (ls "resources/articles"))

(defn name->path [article-name]
  (str/join ["resources/articles/" article-name]))

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

;;; Article text contains metadata, above a "~~~" marker.
(defn split-content-and-meta [article-text]
  (let [sections (str/split article-text #"\n---\n")]
    (if (= 1 (count sections))
      [(first sections) {}]
      [(second sections) (first sections)])))

(def parse-metadata parse-toml)

(defn load-article-content [article-name]
  (let [file-content (load-article-file article-name "content.md")
        [content metadata] (split-content-and-meta file-content)]
    [content (parse-metadata metadata)]))

(defn load-article [slug]
  (let [[content metadata] (load-article-content slug)]
    {:title (get metadata "title")
     :slug slug
     :content content
     :metadata metadata
     :script (load-custom-script slug)
     :style (load-custom-style slug)
     }))

(defn load-articles []
  (map load-article (list-articles)))

(defn view-articles-list []
  (-> (r/response (views/articles-list (load-articles)))
      (r/content-type "text/html")))

(defn view-article [slug]
  (views/article (load-article slug)))

(defn view-front-page []
  (views/front-page (load-articles)))

(defroutes app-routes
  (GET "/" [] (view-front-page))
  (GET "/articles" [] (view-articles-list))
  (GET "/article/:slug" [slug] (view-article slug))
  (route/resources "/"))

(def reloadable-app
  (-> #'app-routes
      wrap-reload
      wrap-stacktrace))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (ring-jetty/run-jetty #'reloadable-app {:port 3000}))
