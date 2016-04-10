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
     [:a.navigation-item.is-current  {"href" "/articles"} "Articles"]
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
     [:img {"src" "//c.tadst.com/gfx/750w/sunrise-sunset-sun-calculator.jpg?1" "alt" "javascript"}]]
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
     [:script {"src" "//cdn.jsdelivr.net/hyphenator/4.3.0/hyphenator.min.js"}]
     [:script "Hyphenator.run()"]
      [:nav.l-navigation navigation]
      [:main.l-main main]]))

(defn- my-portrait [size]
  (str "//gravatar.com/avatar/2e45496b21f05fc9d7b80789d3f126f3?s=" size))

(def welcome-hero
  [:div.welcomeHero
   [:img.welcomeHero-picture {"src" (my-portrait 256)}]
   [:div.welcomeHero-text
    [:h1.welcomeHero-title "Hi, I'm "
     [:span "Sam Doiron"]]
    [:p "I am a Computer Science student studying at "
      [:span.welcomeHero-highlight "Dalhousie University"] "."]
    [:p "My interests include "
     [:span.welcomeHero-highlight "Web Development"] ", "
     [:span.welcomeHero-highlight "Design"] ", and "
     [:span.welcomeHero-highlight "Software Architecture"] "."]
    ]])

;;;; Public

(defn front-page [articles]
  (basic-page
    [:div
      welcome-hero
      [:div.cardGroup
        [:div.cardGroup-card "Resume"]
        [:div.cardGroup-card "Articles"]
        [:div.cardGroup-card "Projects"]]
      ]
    ))

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
