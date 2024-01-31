
(ns transition-controller.api
    (:require [transition-controller.views :as views]
              [transition-controller.side-effects :as side-effects]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (transition-controller.side-effects/*)
(def set-content!  side-effects/set-content!)
(def hide-content! side-effects/hide-content!)

; @redirect (transition-controller.views/*)
(def view views/view)
