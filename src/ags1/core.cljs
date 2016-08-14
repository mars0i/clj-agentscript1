(ns ags1.core
  (:require ))

(enable-console-print!)
(println "This text is printed from src/ags1/core.cljs. Go ahead and edit it and see reloading in action.")

;; NOTE I can get rid of the this.setWorld etc errors by passing
;; this.prototype via .call() to Model in agentscript.js, but
;; then I get "abm is undefined" on the line that let-defines util.  WTF.
;; This *does not* happen when I use the original agentscript.js.

(defn on-js-reload [])

(defn run-sim []
  (this-as that
    (let [abm (.-ABM that)
          util (.-Util abm)
          model (.-Model abm)
          prototype (.-prototype model)]
      ;(println (.keys js/Object (.-ABM that))) ; DEBUG
      ;(println (.keys js/Object abm)) ; DEBUG

      ;; STARTUP: leave as default
      ;; SETUP:
      (set! (.-setup prototype)
            (fn []
              (println "setup")))
      ;; STEP:
      (set! (.-step prototype)  ; STEP
            (fn []
              (println "step")))
      (let [sim (model (clj->js {:div "layers"  ; sim is called 'model' in jsmodel.html
                                 :size 13
                                 :minX -16
                                 :maxX 16
                                 :minY -16
                                 :maxY 16
                                 :isTorus true}))]
        (.debug sim)
        (.start sim)))))

(run-sim)
