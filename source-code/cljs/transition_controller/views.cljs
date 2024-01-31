
(ns transition-controller.views
    (:require [reagent.core :as reagent]
              [transition-controller.side-effects :as side-effects]
              [transition-controller.env :as env]
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
        transition-name     (env/get-transition-name     controller-id)
        transition-duration (env/get-transition-duration controller-id)]
       (letfn [(f0 [dex {:keys [id content]}]
                   [:div (str dex id content)])]
                   ;[:> css-transition {:in            (= id active-content-id)
                    ;                   :timeout       (-> transition-duration)
                    ;                   :classNames    {:enter  :pr-mount   :enterActive  :pr-mounting   :enterDone  :pr-mounted
                    ;;                                   :exit   :pr-unmount :exitActive   :pr-unmounting :exitDone   :pr-unmounted
                      ;                                 :appear :pr-appear  :appearActive :pr-appearing  :appearDone :pr-appeared
                      ;                 :appear        true
                      ;                 :unmountOnExit true
                      ;                (-> content)]}])]
              ;[:> transition-group]
              (hiccup/put-with-indexed [:<>] content-pool f0 :id))))
              ;[:div (str controller-id "\"" transition-duration)])))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn view-lifecycles
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ; @param (*) initial-content
  ; @param (map) options
  [controller-id initial-content options])
  ;(reagent/create-class {:component-did-mount    (fn [_ _] (side-effects/set-content!  controller-id initial-content options))
  ;                       :component-will-unmount (fn [_ _] (side-effects/clear-controller-state! controller-id))
  ;                       :reagent-render         (fn [_ _] [transition-controller                controller-id])])

(defn view
  ; @param (keyword) controller-id
  ; @param (*)(opt) initial-content
  ; @param (map)(opt) options
  ;
  ; @usage
  ; [view :my-transition-controller]
  ;
  ; @usage
  ; [view :my-transition-controller [:div "My initial content"]]
  ;
  ;; @usage
  ; [view :my-transition-controller [:div "My initial content"] {:transition-duration 250 :transition-name :fade-in}]
  ([controller-id]
   [view controller-id nil])

  ([controller-id initial-content]
   [view controller-id initial-content {}])

  ([controller-id initial-content options]
   [view-lifecycles controller-id initial-content options]))
