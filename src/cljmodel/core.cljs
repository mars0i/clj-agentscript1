;; Marshall Abrams
;; Clojurescript port of jsmodel.html from the Agentscript site
;; Copyright 2016 Marshall Abrams. Released under GPL 3.0.

(ns cljmodel.core)

(enable-console-print!) ; makes (println ...) calls like console.log(...) calls

;; Parameters that will be passed to the model (i.e. sim or this-inst below):
(def sim-params (clj->js {:div "layers"
                          :size 13  ; scale relative to browser
                          :minX -16 ; number of patches left of center patch
                          :maxX 16  ; i.e. actual width is now 33
                          :minY -16 ; patches below (?) center patch
                          :maxY 16  ; i.e. height is also 33
                          :isTorus true}))

;; convenience defs for use below
(def abm (this-as this-top (.-ABM this-top))) ; uses this = top-level window
(def util (.-Util abm))
(def Model (.-Model abm))
(def prototype (.-prototype Model))

;; startup runs when an instance of Model is created
(set! (.-startup prototype) (fn [] (println "startup")))

;; setup runs when an instance of Model is created, and during reset
(set! (.-setup prototype)
      (fn []
        (println "setup")
        (this-as this-inst   ; this: the current instance of Model
          (let [turtles (.-turtles this-inst)
                patches (.-patches this-inst)]

            (set! (.-refreshPatches this-inst) false) ; not updating patches
            (set! (.-refreshLinks this-inst) false)   ; no links to update

            (doseq [patch patches]
              (set! (.-color patch) (.randomGray util)))

            (set! (.-population this-inst) 100)       ; how many turtles?
            (set! (.-speed this-inst) 0.5)            ; how fast do they go?
            (set! (.-wiggle this-inst) (.degToRad util 30)) ; random turn param
            (.setUseSprites turtles)                  ; faster bitmap turtles
            (.create turtles (.-population this-inst))

            (doseq [turtle turtles]
              (let [point (js->clj (.randomPt patches))]
                (.setXY turtle (first point) (second point))))

            (println "patches; "  (count patches) " turtles: " (count turtles))))))

;; step is what runs on every tick
(set! (.-step prototype)
      (fn []
        (this-as this-inst   ; this: the current instance of Model
           ;(when (== 0 (mod (.-ticks (.-anim this-inst)) 100))
           ;  (println (.toString (.-anim this-inst))))
           (doseq [t (.-turtles this-inst)]
             (.rotate t (.randomCentered util (.-wiggle this-inst)))
             (.forward t (.-speed this-inst))))))


;; Create the model:
(defonce sim (Model. sim-params)) ; '(new Model sim-params)' works, too
(.debug sim) ; print to console, model vars in global, turtle "sprite sheet" on right
(.start sim) ; Run it!  (.reset sim true) works here, too

;; Runs when figwheel reloads the code:
(defn on-js-reload []
  (.reset sim true))
;; resets and restarts the model. (.start ...) above is redundant on reload (?)

;; Note: Above we use defonce rather than def, and the call to reset in 
;; on-js-reload in order to work with figwheel.  Otherwise there 
;; would be will be multiple instances of the model running at once 
;; and/or things would get confused in the browser in other ways.
