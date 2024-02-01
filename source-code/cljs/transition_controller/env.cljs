
(ns transition-controller.env
    (:require [transition-controller.state :as state]
              [fruits.vector.api :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn get-content-pool
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (get-content-pool :my-transition-controller)
  ; =>
  ; [{:id :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx :content [:div "My content"]}]
  ;
  ; @return (maps in vector)
  [controller-id]
  (get-in @state/CONTROLLERS [controller-id :content-pool]))

(defn get-active-content-id
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (get-active-content-id :my-transition-controller)
  ; =>
  ; :xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
  ;
  ; @return (keyword)
  [controller-id]
  (get-in @state/CONTROLLERS [controller-id :active-content-id]))

(defn get-active-content
  ; @ignore
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
  (if-let [active-content-id (get-active-content-id controller-id)]
          (let [content-pool (get-content-pool      controller-id)]
               (letfn [(f0 [%] (-> % first (= active-content-id)))]
                      (second (vector/first-match content-pool f0))))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn rerender-same-content?
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (rerender-same-content? :my-transition-controller)
  ; =>
  ; true
  ;
  ; @return (boolean)
  [controller-id]
  (get-in @state/CONTROLLERS [controller-id :rerender-same?]))

(defn get-transition-duration
  ; @ignore
  ;
  ; @param (keyword) controller-id
  ;
  ; @usage
  ; (get-transition-duration :my-transition-controller)
  ; =>
  ; 250
  ;
  ; @return (ms)
  [controller-id]
  (let [transition-duration (get-in @state/CONTROLLERS [controller-id :transition-duration])]
       (or transition-duration 0)))
