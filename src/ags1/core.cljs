;; Marshall Abrams
;; port of jsmodel.html from the agentscript site to Clojurescript.
;; Copyright 2016 Marshall Abrams. Released under GPL 3.0.

;; NOTES:
;; 
;; The definition of Model in agentscript.js makes repeated references
;; to 'this', which in that context apparently refers to the instance
;; of the Model.  Works fine when run from Javascript, but when I run it
;; from Clojurescript (with :optimizations :none, so no name munging),
;; by default this in the Model def refers to the top-level window
;; (and I don't see any way to fix this using 'this-as').
;; i.e. that's what happens if you create a new model using e.g. (.Model ...).
;; However, if you create the new model using 'new', it gets the right 'this'.
;;
;; Then you can refer to the model from which you run setup and step functions
;; that you've defined by using this-as within these function defs.
;; There are other ways to do this with step(), but you have to jump through
;; hoops to avoid using this-as in setup, because it's called automatically
;; when you new the model, whose constructor calls setupAndEmit(), which
;; calls setup().
;;
;; Note that set! needs to see the literal field access; you can't 
;; put the field access result in a variable and then set! it.
;; If you are setting w.x.y.z, you apparently need to put the whole
;; path in: (set! (.-z (.-y (.-x w))) newval).

(ns ags1.core
  (:require ))

(enable-console-print!)

(defn on-js-reload [])

(def abm (this-as that (.-ABM that)))
(def util (.-Util abm))
(def model (.-Model abm))

(def sim-params (clj->js {:div "layers"
                          :size 13
                          :minX -16
                          :maxX 16
                          :minY -16
                          :maxY 16
                          :isTorus true}))

; STARTUP: leave default

; SETUP:
(set! (.-setup (.-prototype (.-Model abm)))
      (fn []
        (this-as this
          (let [turtles (.-turtles this)
                patches (.-patches this)]
            
            ;; TODO:
            ;; When I reload with figwheel, the old turtle icons seem to
            ;; hang around, although I don't think the turtles exist.
            ;; Some failed attempts to fix this:
            ;(.clear (.-turtles this)) 
            ;(.clear (.-drawing this))
            ;(.reset this)

            (set! (.-refreshPatches this) false)
            (set! (.-refreshLinks this) false)
            (.setUseSprites (.-turtles this))
            (set! (.-population this) 100)
            (set! (.-speed this) 0.5)
            (set! (.-wiggle this) (.degToRad util 30))

            (doseq [p patches]
              (set! (.-color p) (.randomGray util)))

            (.create turtles (.-population this))
            (doseq [t turtles]
              (let [pt (js->clj (.randomPt (.-patches this)))]
                (.setXY t (first pt) (second pt))))

            (println "patches; "  (count patches)
                     " turtles: " (count turtles))))))

; STEP:
;(def tick (atom 0))
(set! (.-step (.-prototype (.-Model abm)))
      (fn []
        (this-as this
          ;(swap! tick inc)
          ;(when (> @tick 500) (.stop this))
          (doseq [t (.-turtles this)]
            (.rotate t (.randomCentered util (.-wiggle this)))
            (.forward t (.-speed this))))))

;; Create the model:
(def sim (new model sim-params))
(.debug sim) ; Put Model vars in global name space
(.start sim) ; Run the model!
