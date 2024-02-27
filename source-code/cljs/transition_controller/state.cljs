
(ns transition-controller.state
    (:require [reagent.core :as reagent]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @ignore
;
; @atom (map)
; {:my-transition-controller (map)
;   {:active-content-id (keyword)
;    :content-pool (vector)
;     [(keyword) content-id
;      (hiccup or Reagent component) content]
;    ...}}
(def CONTROLLERS (reagent/atom {}))
