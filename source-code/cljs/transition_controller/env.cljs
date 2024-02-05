
(ns transition-controller.env
    (:require [transition-controller.state :as state]
              [fruits.vector.api :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn get-controller-state
  ; @ignore
  ;
  ; @description
  ; Returns the stored state of a specific controller (optionally filtered to a specific key).
  ;
  ; @param (keyword) controller-id
  ; @param (keyword)(opt) item-key
  ;
  ; @usage
  ; (get-controller-state :my-transition-controller)
  ; =>
  ; {:content-pool [...]
  ;  ...}
  ;
  ; @usage
  ; (get-controller-state :my-transition-controller :content-pool)
  ; =>
  ; [...]
  ;
  ; @return (*)
  [controller-id & [item-key]]
  (if item-key (get-in @state/CONTROLLERS [controller-id item-key])
               (get-in @state/CONTROLLERS [controller-id])))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn get-active-content
  ; @ignore
  ;
  ; @description
  ; Returns the active content of a specific controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (get-active-content :my-transition-controller)
  ; =>
  ; [:div "My content"]
  ;
  ; @return (*)
  [controller-id]
  (if-let [active-content-id (get-controller-state controller-id :active-content-id)]
          (let [content-pool (get-controller-state controller-id :content-pool)]
               (letfn [(f0 [%] (-> % first (= active-content-id)))]
                      (second (vector/first-match content-pool f0))))))
