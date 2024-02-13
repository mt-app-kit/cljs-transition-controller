
(ns transition-controller.views
    (:require [react-transition-group]
              [fruits.hiccup.api                  :as hiccup]
              [fruits.random.api                  :as random]
              [reagent.core :as reagent]
              [transition-controller.config       :as config]
              [transition-controller.env          :as env]
              [transition-controller.side-effects :as side-effects]))

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
  ; @param (map) controller-props
  ; {:transition-duration (ms)(opt)
  ;   Default: 0}
  [controller-id {:keys [transition-duration] :or {transition-duration 0}}]
  (let [active-content-id (env/get-controller-state controller-id :active-content-id)
        content-pool      (env/get-controller-state controller-id :content-pool)
        content-visible?  (env/get-controller-state controller-id :content-visible?)]
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
  ; @param (map) controller-props
  [controller-id controller-props]
  (reagent/create-class {:component-did-mount    (fn [_ _] (side-effects/controller-did-mount    controller-id controller-props))
                         :component-will-unmount (fn [_ _] (side-effects/controller-will-unmount controller-id controller-props))
                         :component-did-update   (fn [%]   (side-effects/controller-did-update   controller-id controller-props %))
                         :reagent-render         (fn [_ _] [transition-controller                controller-id controller-props])}))

(defn view
  ; @description
  ; Transition controller component.
  ; Displays the initial content (if any) until the content is overriden by the 'set-content!' function.
  ;
  ; @param (keyword)(opt) controller-id
  ; @param (map) controller-props
  ; {:initial-content (*)(opt)
  ;  :rerender-same? (boolean)(opt)
  ;   Default: false
  ;  :transition-duration (ms)(opt)
  ;   Default: 0}
  ;
  ; @usage
  ; [view {...}]
  ;
  ; @usage
  ; [view :my-transition-controller {...}]
  ;
  ; @usage
  ; [view :my-transition-controller {:initial-content [:div "My content"] :transition-duration 250}]
  ([controller-props]
   [view (random/generate-keyword) controller-props])

  ([controller-id controller-props]
   [view-lifecycles controller-id controller-props]))
