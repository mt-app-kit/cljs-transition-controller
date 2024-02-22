
(ns transition-controller.side-effects
    (:require [fruits.map.api              :as map :refer [dissoc-in]]
              [fruits.random.api           :as random]
              [fruits.vector.api           :as vector]
              [reagent.tools.api :as reagent.tools]
              [time.api                    :as time]
              [transition-controller.env   :as env]
              [transition-controller.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn store-content!
  ; @ignore
  ;
  ; @description
  ; Stores the given content in the content pool of the controller.
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ; @param (*) content
  ;
  ; @usage
  ; (store-content! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx [:div "My content"])
  [controller-id content-id content]
  (swap! state/CONTROLLERS update-in [controller-id :content-pool] vector/conj-item [content-id content]))

(defn activate-content!
  ; @ignore
  ;
  ; @description
  ; Sets the given content ID as the active content ID of the controller.
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ;
  ; @usage
  ; (activate-content! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
  [controller-id content-id]
  (swap! state/CONTROLLERS assoc-in [controller-id :active-content-id] content-id))

(defn set-content-visibility!
  ; @ignore
  ;
  ; @description
  ; Sets the content visibility of the controller.
  ;
  ; @param (keyword) controller-id
  ; @param (boolean) visible?
  ;
  ; @usage
  ; (set-content-visibility! :my-transition-controller)
  [controller-id visible?]
  (swap! state/CONTROLLERS assoc-in [controller-id :content-visible?] visible?))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn mark-controller-as-mounted!
  ; @ignore
  ;
  ; @description
  ; Marks the controller as mounted (in the 'CONTROLLERS' atom).
  ;
  ; @param (keyword) controller-id
  ; @param (map) controller-props
  ;
  ; @usage
  ; (mark-controller-as-mounted! :my-transition-controller {...})
  [controller-id _]
  (swap! state/CONTROLLERS update controller-id assoc :mounted? true))

(defn store-controller-settings!
  ; @ignore
  ;
  ; @description
  ; Stores the settings of the controller (in the 'CONTROLLERS' atom)
  ; that are provided as view component parameters and required by the controller functions.
  ;
  ; @param (keyword) controller-id
  ; @param (map) controller-props
  ; {:rerender-same? (boolean)(opt)
  ;  :transition-duration (ms)(opt)
  ;  ...}
  ;
  ; @usage
  ; (store-controller-settings! :my-transition-controller {...})
  [controller-id {:keys [rerender-same? transition-duration]}]
  (swap! state/CONTROLLERS update controller-id merge {:rerender-same?      rerender-same?
                                                       :transition-duration transition-duration}))

(defn clear-former-contents!
  ; @ignore
  ;
  ; @description
  ; Removes former contents from the content pool of the controller (if not changed since the given content ID has been set).
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ;
  ; @usage
  ; (clear-former-contents! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
  [controller-id content-id]
  (let [active-content-id (env/get-controller-state controller-id :active-content-id)]
       (when (= content-id active-content-id)
             (swap! state/CONTROLLERS update-in [controller-id :content-pool]       vector/keep-last-item)
             (swap! state/CONTROLLERS update-in [controller-id :content-lifecycles] map/keep-key content-id))))

(defn clear-controller-state!
  ; @ignore
  ;
  ; @description
  ; Clears the state of the controller.
  ;
  ; @param (keyword) controller-id
  ; @param (map) controller-props
  ;
  ; @usage
  ; (clear-controller-state! :my-transition-controller {...})
  [controller-id _]
  (swap! state/CONTROLLERS dissoc controller-id))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn set-content!
  ; @description
  ; Sets the content of the controller.
  ;
  ; @param (keyword) controller-id
  ; @param (*) content
  ;
  ; @usage
  ; (set-content! :my-transition-controller [:div "My content"])
  [controller-id content]
  (let [content-id          (random/generate-keyword)
        active-content      (env/get-active-content   controller-id)
        transition-duration (env/get-controller-state controller-id :transition-duration)
        rerender-same?      (env/get-controller-state controller-id :rerender-same?)]
       (when (or rerender-same? (not= active-content content))
             (store-content!          controller-id content-id content)
             (activate-content!       controller-id content-id)
             (set-content-visibility! controller-id true)
             (letfn [(f0 [] (clear-former-contents! controller-id content-id))]
                    (time/set-timeout! f0 transition-duration)))))

(defn show-content!
  ; @description
  ; Shows the content of the controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (show-content! :my-transition-controller)
  [controller-id]
  (set-content-visibility! controller-id true))

(defn hide-content!
  ; @description
  ; Hides the content of the controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (hide-content! :my-transition-controller)
  [controller-id]
  (set-content-visibility! controller-id false))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn controller-did-mount
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ; @param (map) controller-props
  ; {:initial-content (*)(opt)}
  ;
  ; @usage
  ; (controller-did-mount :my-transition-controller {...})
  [controller-id {:keys [initial-content] :as controller-props}]
  (mark-controller-as-mounted! controller-id controller-props)
  (store-controller-settings!  controller-id controller-props)
  (if initial-content (set-content! controller-id initial-content)))

(defn controller-did-update
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ; @param (map) controller-props
  ;
  ; @usage
  ; (controller-did-update :my-transition-controller {...})
  [controller-id _ %]
  (let [[_ controller-props] (reagent.tools/arguments %)]
       (store-controller-settings! controller-id controller-props)))

(defn controller-will-unmount
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ; @param (map) controller-props
  ;
  ; @usage
  ; (controller-will-unmount :my-transition-controller {...})
  [controller-id controller-props]
  (clear-controller-state! controller-id controller-props))
