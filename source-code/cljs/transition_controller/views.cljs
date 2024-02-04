
(ns transition-controller.views
    (:require [reagent.core :as reagent]
              [transition-controller.side-effects :as side-effects]
              [transition-controller.env :as env]
              [transition-controller.config :as config]
              [react-transition-group]
              [fruits.hiccup.api :as hiccup]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (react-transition-group/*)
(def transition       react-transition-group/Transition)
(def css-transition   react-transition-group/CSSTransition)
(def transition-group react-transition-group/TransitionGroup)

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn transition-controller
  ; @ignore
  ;
  ; @param (keyword) controller-id
  [controller-id]
  (let [content-pool        (env/get-content-pool        controller-id)
        active-content-id   (env/get-active-content-id   controller-id)
        content-visible?    (env/content-visible?        controller-id)
        transition-duration (env/get-transition-duration controller-id)]
       (letfn [(f0 [[id content]]
                   [:> css-transition {:in            (and content-visible? (= id active-content-id))
                                       :timeout       transition-duration
                                       :classNames    config/CLASS-NAMES
                                       :appear        true
                                       :unmountOnExit true}
                                      (-> content)])]
              ; [:> transition-group ...] <- Wraps the content with an unnecessary DIV.
              (hiccup/put-with [:<>] content-pool f0 first))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn view-lifecycles
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ; @param (*) initial-content
  ; @param (map) options
  [controller-id initial-content options]
  (reagent/create-class {:component-did-mount    (fn [_ _] (side-effects/init-controller-state!  controller-id initial-content options))
                         :component-will-unmount (fn [_ _] (side-effects/clear-controller-state! controller-id))
                         :reagent-render         (fn [_ _] [transition-controller                controller-id])}))

(defn view
  ; @note
  ; The provided options of this component are the default properties of any futher actions.
  ;
  ; @description
  ; Transition controller component.
  ; Displays the initial content (if any) until the content is overriden by any controller function.
  ;
  ; @param (keyword) controller-id
  ; @param (*)(opt) initial-content
  ; @param (map)(opt) options
  ; {:rerender-same? (boolean)(opt)
  ;   Default: false
  ;  :transition-duration (ms)(opt)
  ;   Default: 0}
  ;
  ; @usage
  ; [view :my-transition-controller]
  ;
  ; @usage
  ; [view :my-transition-controller [:div "My initial content"]]
  ;
  ; @usage
  ; [view :my-transition-controller [:div "My initial content"] {:transition-duration 250}]
  ([controller-id]
   [view controller-id nil])

  ([controller-id initial-content]
   [view controller-id initial-content {}])

  ([controller-id initial-content options]
   [view-lifecycles controller-id initial-content options]))
