(ns site.core
  (:gen-class))

(require '[ring.adapter.jetty :as ring-jetty]
         '[hiccup.core :as h]
         '[ring.middleware.reload :refer [wrap-reload]]
         '[ring.util.response :as r])

(defn string->vec [s] (seq s))

(defn vec->string [v] (apply str v))

(defn remove-newlines [s]
  (->> s
      string->vec
      (filter #(not= % \newline))
      vec->string))

(defn minify-css [css]
  (-> css 
      remove-newlines))

(defn static-file [file-name]
  (str "src/site/static/" file-name))

(defn read-css []
  (-> "site.css"
      static-file
      slurp
      minify-css))

(defn basic-page [title content]
  (h/html [:html
           [:head
            [:title title]
            [:style (read-css)]
            [:meta {"charset" "utf-8"}]]
           [:body content]
           ]))

(defn front-page []
  (basic-page 
    "Front page" 
    [:div.open-1 [ :div.open-2 [:div.open-3 [:h1 "Sam Doiron" ]]]]))

(defn hello-handler [request]
  (-> (r/response (front-page))
      (r/content-type "text/html")))

(def reloadable-app
  (wrap-reload #'hello-handler))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (ring-jetty/run-jetty #'reloadable-app {:port 3000}))
