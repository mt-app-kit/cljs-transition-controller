
(ns transition-controller.state
    (:require [reagent.api :refer [ratom]]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @ignore
;
; @atom (map)
; {:my-controller (map)
;   {:active-content-id (keyword)
;    :content-pool (vector)
;     [(keyword) content-id
;      (*) content]
;    ...}}
(def CONTROLLERS (ratom {}))
