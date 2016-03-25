(ns site.views
  (:require [ring.adapter.jetty :as ring-jetty]
            [markdown.core :as markdown]
            [hiccup.page :as h]
            [markdown.core :as markdown-clj]))

(defn- styles []
  [:style (slurp "resources/site.css")])

(defn- get-meta [article meta-key]
  (get (:metadata article) meta-key))

(defn markdown->html [markdown]
  (markdown-clj/md-to-html-string markdown))

(def navigation
   [:nav.navigation
     [:a.navigation-logo {"href" "/"} "Sam Doiron"]
     [:a.navigation-item  {"href" "/articles"} "Articles"]
     [:a.navigation-item {"href" "/projects"} "Projects"]
    ])

(defn- head []
  [:head
    [:title "Hello"]
    [:meta {"charset" "utf-8"}]
    [:meta {"http-equiv" "x-ua-compatible" "content" "ie=edge"}]
    [:meta {"name" "description"
            "content" "The personal website of Sam Doiron"}]
    [:meta {"name" "viewport"
            "content" "width=device-width, initial-scale=1"}]
    (styles)
   ])

(defn- link-to [article]
  (str "/article/" (:slug article)))

(defn- article-listing-item [article]
  [:a.articleListing-item {"href" (link-to article)}
   [:div.articleListing-item-flair
     [:img {"src" "/js.png" "alt" "javascript"}]]
   [:div.articleListing-item-details
     [:h1.articleListing-item-title (:title article)]
     [:span.articleListing-item-date "13 Feb 2015"]
   ]])

(defn- article-listing [articles]
  [:div.articleListing
   (map article-listing-item (flatten (take 25 (repeat articles))))])

(defn- basic-page [main]
  (h/html5 {"lang" "en"}
    (head)
    [:body
      [:nav.l-navigation navigation]
      [:main.l-main main]]))

;;;; Public

;;; View the full content of an article, on a dedicated page
(defn article [art]
  (basic-page
    [:article.article
     [:h1.article-title (:title art)]
     (markdown->html (:content art))
     ]))

;;; View the listing page for my article
(defn articles-list [articles]
  (basic-page
    (article-listing articles)))
