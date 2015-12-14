(ns site.core
  (:gen-class))

(require '[ring.adapter.jetty :as ring-jetty]
         '[hiccup.core :as h]
         '[ring.middleware.reload :refer [wrap-reload]]
         '[ring.middleware.stacktrace :refer [wrap-stacktrace]]
         '[ring.util.response :as r])

(def test-images 
  [
   {:url "http://proof.nationalgeographic.com/files/2014/11/PastedGraphic-2.jpg" :color "#132635"}
   {:url "https://drscdn.500px.org/photo/52866292/m=2048_k=1_a=1/2683707825210f5bb6d26e7c32d14738" :color "#687275"}
   {:url "http://proof.nationalgeographic.com/files/2015/04/150501-bestpod-volcANO.jpg" :color "#07213F"}
   ])

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

(defn static-file-path [file-name]
  (str "src/site/static/" file-name))

(defn read-css []
  (-> "site.css"
      static-file-path
      slurp
      minify-css))

(defn read-script []
  (-> "site.js"
      static-file-path
      slurp))

(defn basic-page [title content]
  (h/html [:html
           [:head
            [:title title]
            [:style (read-css)]
            [:script { "async" "async" } (read-script)]
            [:meta { "name" "viewport" "content" "width=device-width,initial-scale=1"}]
            [:meta {"charset" "utf-8"}]]
           content
           ]))

(defn navigation-item [item]
  [:a.navigation-item { "href" (:url item) } (:title item) ])

;;; items: { :url url, :title title }
(defn navigation [items]
  [:nav.primary-navigation
   [:div.primary-navigation-shadow
    [:h1.primary-navigation-title "Sam Doiron"]
    (map navigation-item items)]])

(defn image-style [img]
  (str 
    "background-image: url(" (:url img) ");"
    "background-color:" (:color img)))

(defn article [art]
    [:a.article { "href" "http://www.google.com" "style" (image-style (rand-nth test-images)) }
       [:h1.article-title (:title art)]])

(defn articles [arts]
  [:div.articles 
   (navigation [{ :url "#" :title "Projects" }
                { :url "#" :title "Blog" }
                { :url "#" :title "Resume" }])
   (splash)
   (map article arts)])

(defn splash []
  [:div.splash
   [:div.splash-greeting
     [:img.splash-me { "src" "http://avatars2.githubusercontent.com/u/3770220?v=3&s=460" }]
     [:h1.splash-title 
      "Hi, I'm Sam Doiron!"]]
   [:p.splash-body "I'm a "
                [:span.computer-programmer "Computer Programmer"]
                " studying Computer Science at "
                [:span.dalhousie "Dalhousie University"]
                "."]
   [:p.splash-body 
    "My interests include "
    [:span.design "Design"]
    ", "
    [:span.software-architecture "Software Architecture"]
    [:span.comma ","]
    " and the "
    [:span.oxford-comma "Oxford Comma"]]])

(defn front-page []
  (basic-page 
    "Front page" 
    [:body
     (articles (take 10 
                     (repeat { :title "A Brief Review of Clojure" })))]))

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
