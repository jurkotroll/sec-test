(ns sec-test.core
  (:require
	    [reagent.core :as r]
	    [secretary.core :as secretary]
	    [goog.events :as events]
      [goog.history.EventType :as EventType]


   ))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(def app-state (r/atom {:page :first}))
(println "initial state" @app-state)

(defn first []
  [:div
   [:h1 "First"][:br]

   [:a {:href "#/first"} "First"][:br]
   [:a {:href "#/second"} "Second"]])

(defn first []
  [:div
   [:h1 "Second"][:br]

   [:a {:href "#/first"} "First"][:br]
   [:a {:href "#/second"} "Second"]])
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn hook-browser-navigation! []
  (doto (History.)
  	(events/listen
    	EventType/NAVIGATE
    	(fn [event]
   			(secretary/dispatch! (.-token event))))
  	(.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/first" []
    (swap! app-state assoc :page :first))
  (defroute "/second" []
    (swap! app-state assoc :page :second))

  (println "from app-routes" @app-state)
  (hook-browser-navigation!))

(defmulti current-page #(@app-state :page))
(defmethod current-page :first []
  [first])
(defmethod current-page :second []
  [second])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; render function
(defn reload []
  (r/render [current-page]
            (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (app-routes)
  (reload))
