
(ns transition-controller.state
    (:require [reagent.core :refer [atom] :rename {atom ratom}]))

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
;    :transition-duration (ms)
;    :transition-name (keyword)}}
(def CONTROLLERS (ratom {}))
