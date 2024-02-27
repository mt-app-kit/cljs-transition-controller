
(ns transition-controller.env
    (:require [fruits.vector.api           :as vector]
              [transition-controller.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn get-controller-state
  ; @ignore
  ;
  ; @description
  ; Returns the stored state of the controller (optionally filtered to a specific key).
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
  ; Returns the active content of the controller.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (get-active-content :my-transition-controller)
  ; =>
  ; [:div "My content"]
  ;
  ; @return (hiccup or Reagent component)
  [controller-id]
  (if-let [active-content-id (get-controller-state controller-id :active-content-id)]
          (let [content-pool (get-controller-state controller-id :content-pool)]
               (letfn [(f0 [%] (-> % first (= active-content-id)))]
                      (second (vector/first-match content-pool f0))))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn controller-mounted?
  ; @description
  ; Returns TRUE if the controller is mounted.
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (controller-mounted? :my-transition-controller)
  ; =>
  ; true
  ;
  ; @return (boolean)
  [controller-id]
  (get-controller-state controller-id :mounted?))
