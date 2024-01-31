
(ns transition-controller.side-effects
    (:require [transition-controller.state :as state]
              [transition-controller.env   :as env]
              [fruits.random.api :as random]
              [fruits.vector.api :as vector]
              [time.api :as time]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn store-content!
  ; @ignore
  ;
  ; @description
  ; Stores the given content in the content pool.
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ; @param (*) content
  ; @param (map) options
  ;
  ; @usage
  ; (store-content! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx [:div "My content"] {...})
  [controller-id content-id content _]
  (swap! state/CONTROLLERS update-in [controller-id :content-pool] vector/conj-item [content-id content]))

(defn store-transition-settings!
  ; @ignore
  ;
  ; @description
  ; Stores the given transition settings.
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ; @param (*) content
  ; @param (map) options
  ; {:transition-duration (ms)(opt)
  ;  :transition-name (keyword)(opt)}
  ;
  ; @usage
  ; (store-transition-settings! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx [:div "My content"] {...})
  [controller-id _ _ {:keys [transition-duration transition-name]}]
  (swap! state/CONTROLLERS assoc-in [controller-id :transition-duration] transition-duration)
  (swap! state/CONTROLLERS assoc-in [controller-id :transition-name]     transition-name))

(defn activate-content!
  ; @ignore
  ;
  ; @description
  ; Sets the given content ID as the active content ID.
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ; @param (*) content
  ; @param (map) options
  ; {:transition-duration (ms)(opt)
  ;  :transition-name (keyword)(opt)}
  ;
  ; @usage
  ; (activate-content! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx [:div "My content"] {...})
  [controller-id content-id _ _]
  (swap! state/CONTROLLERS assoc-in [controller-id :active-content-id] content-id))

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
  ;  :transition-name (keyword)(opt)
  ;   E.g., :fade-in
  ;  :rerender-same? (boolean)(opt)
  ;   Default: false}
  ;
  ; @usage
  ; (set-content! :my-transition-controller [:div "My content"])
  ;
  ; @usage
  ; (set-content! :my-transition-controller [:div "My content"] {:transition-duration 250 :transition-name :fade-in})
  ([controller-id content]
   (set-content! controller-id content {}))

  ([controller-id content {:keys [rerender-same? transition-duration] :or {transition-duration 0} :as options}]
   (let [content-id     (random/generate-keyword)
         active-content (env/get-active-content controller-id)]
        (when (or rerender-same? (not= active-content content))
              (store-content!             controller-id content-id content options)
              (store-transition-settings! controller-id content-id content options)
              (activate-content!          controller-id content-id content options)
              (letfn [(f0 [] (clean-content-pool! controller-id content-id))]
                     (time/set-timeout! f0 (+ transition-duration 50)))))))

(defn hide-content!
  ; @description
  ; Hides the content of a specific transition controller optionally with animated transition.
  ;
  ; @param (keyword) controller-id
  ; @param (map)(opt) options
  ; {:transition-duration (ms)(opt)
  ;  :transition-name (keyword)(opt)
  ;   E.g., :fade-in}
  ;
  ; @usage
  ; (hide-content! :my-transition-controller)
  ([controller-id]
   (hide-content! controller-id {}))

  ([controller-id options]
   ()))

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
