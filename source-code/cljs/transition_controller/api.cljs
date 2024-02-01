
(ns transition-controller.api
    (:require [transition-controller.views :as views]
              [transition-controller.side-effects :as side-effects]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @tutorial How to use?
;
; @code Usage
; (ns my-namespace
;     (:require [transition-controller.api :as transition-controller]))
;
; (defn change-content-button
;   []
;   [:button {:on-click #(transition-controller/set-content! :my-transition-controller [:div "Another content"])}
;            "Change content!"])
;
; (defn hide-content-button
;   []
;   [:button {:on-click #(transition-controller/hide-content! :my-transition-controller)}
;            "Hide content!"])
;
; (defn my-ui
;   []
;   [:div [change-content-button]
;         [hide-content-button]
;         [transition-controller/view :my-transition-controller [:div "My initial content"] {:transition-duration 250}]])
; @---



; @tutorial Applied CSS classes
;
; @code
; :tc-appear, :tc-appear-active, :tc-appear-done,
; :tc-enter,  :tc-enter-active,  :tc-enter-done,
; :tc-exit,   :tc-exit-active,   :tc-exit-done

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (transition-controller.side-effects/*)
(def set-content!  side-effects/set-content!)
(def hide-content! side-effects/hide-content!)

; @redirect (transition-controller.views/*)
(def view views/view)
