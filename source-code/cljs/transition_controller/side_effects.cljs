
(ns transition-controller.side-effects
    (:require [transition-controller.state :as state]
              [transition-controller.env   :as env]
              [fruits.random.api :as random]
              [fruits.vector.api :as vector]
              [fruits.map.api :refer [dissoc-in]]
              [time.api :as time]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn store-content!
  ; @ignore
  ;
  ; @description
  ; Stores the given content in the content pool of a specific controller.
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ; @param (*) content
  ;
  ; @usage
  ; (store-content! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx [:div "My content"])
  [controller-id content-id content]
  (swap! state/CONTROLLERS update-in [controller-id :content-pool] vector/conj-item [content-id content]))

(defn store-transition-settings!
  ; @ignore
  ;
  ; @description
  ; Stores the given transition settings of a specific controller.
  ;
  ; @param (keyword) controller-id
  ; @param (map) options
  ; {:transition-duration (ms)(opt)}
  ;
  ; @usage
  ; (store-transition-settings! :my-transition-controller {...})
  [controller-id {:keys [transition-duration]}]
  (swap! state/CONTROLLERS assoc-in [controller-id :transition-duration] transition-duration))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn activate-content!
  ; @ignore
  ;
  ; @description
  ; Sets the given content ID as the active content ID of a specific controller.
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ;
  ; @usage
  ; (activate-content! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
  [controller-id content-id]
  (swap! state/CONTROLLERS assoc-in [controller-id :active-content-id] content-id))

(defn deactivate-content!
  ; @ignore
  ;
  ; @description
  ; Clears the active content ID of a specific controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (deactivate-content! :my-transition-controller)
  [controller-id]
  (swap! state/CONTROLLERS dissoc-in [controller-id :active-content-id]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn clean-content-pool!
  ; @ignore
  ;
  ; @description
  ; Removes former contents from the content pool of a specific controller (if not changed since the given content ID has been set).
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ;
  ; @usage
  ; (clean-content-pool! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
  [controller-id content-id]
  (let [active-content-id (env/get-active-content-id controller-id)]
       (when (= content-id active-content-id)
             (swap! state/CONTROLLERS update-in [controller-id :content-pool] vector/keep-last-item))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn set-content!
  ; @description
  ; Sets content for a specific transition controller optionally with animated transition.
  ;
  ; @param (keyword) controller-id
  ; @param (*) content
  ; @param (map)(opt) options
  ; {:transition-duration (ms)(opt)
  ;   Default: 0
  ;  :rerender-same? (boolean)(opt)
  ;   Default: false}
  ;
  ; @usage
  ; (set-content! :my-transition-controller [:div "My content"])
  ;
  ; @usage
  ; (set-content! :my-transition-controller [:div "My content"] {:transition-duration 250})
  ([controller-id content]
   (set-content! controller-id content {}))

  ([controller-id content {:keys [rerender-same? transition-duration] :or {transition-duration 0} :as options}]
   (let [content-id     (random/generate-keyword)
         active-content (env/get-active-content controller-id)]
        (when (or rerender-same? (not= active-content content))
              (store-transition-settings! controller-id options)
              (store-content!             controller-id content-id content)
              (activate-content!          controller-id content-id)
              (letfn [(f0 [] (clean-content-pool! controller-id content-id))]
                     (time/set-timeout! f0 transition-duration))))))

(defn hide-content!
  ; @description
  ; Hides the content of a specific transition controller optionally with animated transition.
  ;
  ; @param (keyword) controller-id
  ; @param (map)(opt) options
  ; {:transition-duration (ms)(opt)
  ;   Default: 0}
  ;
  ; @usage
  ; (hide-content! :my-transition-controller)
  ;
  ; @usage
  ; (hide-content! :my-transition-controller {:transition-duration 250})
  ([controller-id]
   (hide-content! controller-id {}))

  ([controller-id options]
   (store-transition-settings! controller-id options)
   (deactivate-content!        controller-id)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn init-controller-state!
  ; @ignore
  ;
  ; @description
  ; Initializes the controller state for a specific controller.
  ;
  ; @param (keyword) controller-id
  ; @param (*) initial-content
  ; @param (map) options
  ;
  ; @usage
  ; (init-controller-state! :my-transition-controller [:div "My content"] {})
  [controller-id initial-content options]
  (if initial-content (set-content! controller-id initial-content options)))

(defn clear-controller-state!
  ; @ignore
  ;
  ; @description
  ; Clears the controller state of a specific controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (clear-controller-state! :my-transition-controller)
  [controller-id]
  (swap! state/CONTROLLERS dissoc controller-id))
