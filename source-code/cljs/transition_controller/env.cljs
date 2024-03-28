
(ns transition-controller.env
    (:require [fruits.vector.api           :as vector]
              [common-state.api :as common-state]))

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
  (if-let [active-content-id (common-state/get-state :transition-controller :controllers controller-id :active-content-id)]
          (let [content-pool (common-state/get-state :transition-controller :controllers controller-id :content-pool)]
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
  (common-state/get-state :transition-controller :controllers controller-id :mounted?))
