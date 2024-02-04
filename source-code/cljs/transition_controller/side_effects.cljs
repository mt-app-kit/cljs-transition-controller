
(ns transition-controller.side-effects
    (:require [transition-controller.state :as state]
              [transition-controller.env   :as env]
              [fruits.random.api :as random]
              [fruits.vector.api :as vector]
              [fruits.map.api :as map :refer [dissoc-in]]
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

(defn store-options!
  ; @ignore
  ;
  ; @description
  ; Stores the given options of a specific controller.
  ;
  ; @param (keyword) controller-id
  ; @param (map) options
  ;
  ; @usage
  ; (store-options! :my-transition-controller {...})
  [controller-id options]
  (swap! state/CONTROLLERS update controller-id map/merge-some options))

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

(defn set-content-visibility!
  ; @ignore
  ;
  ; @description
  ; Sets the content visibility of a specific controller.
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

(defn clear-former-contents!
  ; @ignore
  ;
  ; @description
  ; Removes former contents from the content pool of a specific controller (if not changed since the given content ID has been set).
  ;
  ; @param (keyword) controller-id
  ; @param (keyword) content-id
  ;
  ; @usage
  ; (clear-former-contents! :my-transition-controller :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
  [controller-id content-id]
  (let [active-content-id (env/get-active-content-id controller-id)]
       (when (= content-id active-content-id)
             (swap! state/CONTROLLERS update-in [controller-id :content-pool]       vector/keep-last-item)
             (swap! state/CONTROLLERS update-in [controller-id :content-lifecycles] map/keep-key content-id))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn set-content!
  ; @description
  ; Sets the content of a specific transition controller.
  ;
  ; @param (keyword) controller-id
  ; @param (*) content
  ;
  ; @usage
  ; (set-content! :my-transition-controller [:div "My content"])
  [controller-id content]
  (let [content-id             (random/generate-keyword)
        active-content         (env/get-active-content      controller-id)
        transition-duration    (env/get-transition-duration controller-id)
        rerender-same-content? (env/rerender-same-content?  controller-id)]
       (when (or rerender-same-content? (not= active-content content))
             (store-content!          controller-id content-id content)
             (activate-content!       controller-id content-id)
             (set-content-visibility! controller-id true)
             (letfn [(f0 [] (clear-former-contents! controller-id content-id))]
                    (time/set-timeout! f0 transition-duration)))))

(defn show-content!
  ; @description
  ; Shows the content of a specific transition controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (show-content! :my-transition-controller)
  [controller-id]
  (set-content-visibility! controller-id true))

(defn hide-content!
  ; @description
  ; Hides the content of a specific transition controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (hide-content! :my-transition-controller)
  [controller-id]
  (set-content-visibility! controller-id false))

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
  ; (init-controller-state! :my-transition-controller [:div "My content"] {...})
  [controller-id initial-content options]
  (store-options! controller-id options)
  (if initial-content (set-content! controller-id initial-content)))

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
