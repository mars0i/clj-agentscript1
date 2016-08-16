;; Marshall Abrams
;; port of jsmodel.html from the agentscript site to Clojurescript.
;; Copyright 2016 Marshall Abrams. Released under GPL 3.0.

(ns ags1.core
  (:require ))

(enable-console-print!)

(def max-ticks 500) ; runs this many ticks
;(def max-ticks nil) ; runs forever

;; Parameters that will be passed to the model (sim, this-inst below):
(def sim-params (clj->js {:div "layers"
                          :size 13
                          :minX -16 ; number of patches left of center patch
                          :maxX 16  ; i.e. actual width is now 33
                          :minY -16 ; patches below (?) center
                          :maxY 16  ; i.e. height is also 33
                          :isTorus true}))

;; Uses this = top-level window:
(def abm (this-as this-top (.-ABM this-top)))   

(def util (.-Util abm))
(def model (.-Model abm)) ; lowercase clearer to distinguish our var from the accessor

; STARTUP: leave default

; SETUP:
(set! (.-setup (.-prototype (.-Model abm)))
      (fn []
        (this-as this-inst   ; this: the current instance of Model
          (let [turtles (.-turtles this-inst)
                patches (.-patches this-inst)]

            (set! (.-refreshPatches this-inst) false) ; not updating patches
            (set! (.-refreshLinks this-inst) false)   ; no links to update
            (.setUseSprites (.-turtles this-inst))    ; use faster bitmap turtle images
            (set! (.-population this-inst) 100)       ; how many turtles?
            (set! (.-speed this-inst) 0.5)            ; how fast do they go?
            (set! (.-wiggle this-inst) (.degToRad util 30))

            (println "patches; "  (count patches) " turtles: " (count turtles))

            (doseq [p patches]
              (set! (.-color p) (.randomGray util)))

            (.create turtles (.-population this-inst))
            (doseq [t turtles]
              (let [pt (js->clj (.randomPt (.-patches this-inst)))]
                (.setXY t (first pt) (second pt))))

            (println "patches; "  (count patches) " turtles: " (count turtles))
            ))))

; STEP:
(set! (.-step (.-prototype (.-Model abm)))
      (fn []
        (this-as this-inst   ; this: the current instance of Model
           (when (and max-ticks 
                      (> (.-ticks (.-anim this-inst)) max-ticks))
             (.stop this-inst))
           (doseq [t (.-turtles this-inst)]
                   (.rotate t (.randomCentered util (.-wiggle this-inst)))
                   (.forward t (.-speed this-inst))))))

;; Create the model:
(def sim (new model sim-params)) ; '(model. sim-params)' works, too
(.debug sim) ; print info to console, put model vars in global name space
(.start sim) ; Run the model!

;; TODO:
;; When I reload with figwheel, the old turtle icons seem to
;; hang around, although I don't think the turtles exist.  Here are
;; some failed attempts to fix this:
(defn on-js-reload []
  ;(.setup sim)
  ;(.start sim) ; Run the model!
  ;(.clear (.-turtles sim))  ; doesn't get rid of turtle ghosts, and no new turtles
  ;(.clear (.-drawing sim)) ; doesn't get rid of turtle ghosts
  ;(.reset sim) ; extreme: seems to break everything
  ;(doseq [t (.-turtles sim)] (.die t))
  ;(let [ctx (.-ctx (.-sprite (first (.-turtles sim))))
  ;      canvas (.-canvas ctx)]
  ;  (.setTransform ctx 1 0 0 1 0 0) ; set transform back to identity
  ;(set! (.-startup (.-prototype (.-Model abm)))
  ;     (fn []
  ;       (this-as this-inst
  ;         (let [ctx (.-ctx (.-sprite (first (.-turtles this-inst))))
  ;               canvas (.-canvas ctx)]
  ;           ;(.setTransform ctx 1 0 0 1 0 0) ; set transform back to identity
  ;           (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))))))
)

;(set! (.-startup (.-prototype (.-Model abm)))
;      (fn []
;        (this-as this-inst
;          (let [ctx (.-ctx (.-sprite (first (.-turtles this-inst))))
;                canvas (.-canvas ctx)]
;            ;(.setTransform ctx 1 0 0 1 0 0) ; set transform back to identity
;            (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))))))
