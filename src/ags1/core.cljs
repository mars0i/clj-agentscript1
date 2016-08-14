(ns ags1.core
  (:require ))

(enable-console-print!)
(println "This text is printed from src/ags1/core.cljs. Go ahead and edit it and see reloading in action.")

;; NOTE I can get rid of the this.setWorld etc errors by passing
;; this.prototype via .call() to Model in agentscript.js, but
;; then I get "abm is undefined" on the line that let-defines util.  WTF.
;; This *does not* happen when I use the original agentscript.js.

(defn on-js-reload [])

(def abm (this-as that (.-ABM that)))
(def util (.-Util abm))
(def model (.-Model abm))
(def prototype (.-prototype model))

;; STARTUP: leave as default

;; SETUP:
(set! (.-setup prototype)
      (fn []
        (println "setup")))

;; STEP:
(set! (.-step prototype)  ; STEP
      (fn []
        (println "step")))

(def sim-params (clj->js {:div "layers"
                          :size 13
                          :minX -16
                          :maxX 16
                          :minY -16
                          :maxY 16
                          :isTorus true}))

;(def sim (abm/Model sim-params)) ; doesn't run
;(def sim (ABM/Model sim-params)) ; doesn't run
;(def sim (model abm sim-params)) ; called this way, it complains because params it gets is ABM
;(def sim ((.Model abm) sim-params)) ; runs but complains because params it gets is ABM
;(def sim (.Model js/ABM sim-params)) ; runs, but 'this' points to ABM, which doesn't have prototype's functions
;(this-as this (def sim (.Model sim-params))) ; runs, but 'this' points to sim-params
;(this-as this (def sim (.Model this sim-params))) ; runs, but 'this' points to global which doesn't have prototype's functions
;(def sim (model model sim-params)) ; runs, but complains because params it gets is model
;(def sim (model js/this sim-params)) ; runs, but "this$" is undefined
;(this-as this (def sim (.Model this abm sim-params))) 
(def sim (model sim-params)) ; runs, but 'this' points to index.html, which doesn't have prototype's functions

(.debug sim)
(.start sim)
