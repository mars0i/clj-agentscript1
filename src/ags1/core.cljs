;; Marshall Abrams
;; port of jsmodel.html from the agentscript site to Clojurescript.
;; Copyright 2016 Marshall Abrams. Released under GPL 3.0.

;; NOTES:
;; 
;; 1. The definition of Model in agentscript.js makes repeated references
;; to 'this', which in that context apparently refers to the instance
;; of the Model.  Works fine when run from Javascript, but when I run it
;; from Clojurescript (with :optimizations :none, so no name munging),
;; by default this in the Model def refers to the top-level window
;; (and I don't see any way to fix this using 'this-as').
;; i.e. that's what happens if you create a new model using e.g. (.Model ...).
;; However, if you create the new model using 'new', it gets the right 'this'.
;; Then I can refer to the appropriate object in my code by referring, e.g.
;; in the step function, to the model that I new'ed.  So far so good.
;; 
;; 2. Note however that since I attach code to the prototype that manipulates
;; the model, which hasn't yet been created, I am kludging this problem
;; by putting the model in an atom.  Ugh.  But ok.  (Javascript solves this 
;; using 'this'.)
;;
;; 3. You also define your own setup() function on the Model prototype, which
;; is then used by the model you create using '(new Model ...)'.
;; This function is normally run when you new the Model; it's called by
;; setupAndEmit().  Unfortunately, in Clojurescript, when this is happening,
;; the variable that refers to my new model hasn't quite been defined, so
;; when setup() is called in the construction process, it won't run properly,
;; i.e. because the code inside it uses a variable that hasn't yet been
;; defined when this code is run.  So ...
;; in the setup() function, I test whether the model (in sim) that the
;; code will refer to has been defined yet.  When step() runs in the Model
;; constructor, @sim is nil, so none of the rest of the code in step() will
;; run.  However, we still need to run step() !   So I call it explicitly
;; after I've reset! sim, but before calling start().

(ns ags1.core
  (:require ))

(enable-console-print!)

(def tick (atom 0))
(def sim (atom nil)) ; we'll put sim (model) here so we can refer to it in its methods before it's defined

(defn on-js-reload []
  ;(.reset @sim) ; doesn't work
  )


(def abm (this-as that (.-ABM that)))
(def util (.-Util abm))
(def model (.-Model abm))
(def prototype (.-prototype model))

(def sim-params (clj->js {:div "layers"
                          :size 13
                          :minX -16
                          :maxX 16
                          :minY -16
                          :maxY 16
                          :isTorus true}))

; STARTUP: leave default

; SETUP:
(set! (.-setup (.-prototype (.-Model abm))) ; what doesn't work: (set! (.-setup prototype) ...)
      (fn []
        (println "setup")

        ;; Kludge: This function gets called via setupAndEmit() when the 
        ;; Model is new'd below, but at that point we can't refer to the
        ;; object from Clojurescript.  So test whether @sim has a model
        ;; in it, and if not, do nothing more.  Then call this function
        ;; explicitly after the model has been created.
        (when-let [s @sim]
          (let [turtles (.-turtles s)
                patches (.-patches s)]
            
            ;; TODO:
            ;; When I reload with figwheel, the old turtle icons seem to
            ;; hang around, although I don't think the turtles exist.
            ;; Some failed attempts to fix this:
            ;(.clear (.-turtles s)) 
            ;(.clear (.-drawing s))
            ;(.reset s)

            ;; Note that set! needs to see the literal field access; you can't 
            ;; get the field access's result in a variable and then set! it.
            (set! (.-refreshPatches s) false)
            (set! (.-refreshLinks s) false)
            (.setUseSprites (.-turtles s))
            (set! (.-population s) 100)
            (set! (.-speed s) 0.5)
            (set! (.-wiggle s) (.degToRad util 30))

            (doseq [p patches]
              (set! (.-color p) (.randomGray util)))

            (.create turtles (.-population s))
            (doseq [t turtles]
              (let [pt (js->clj (.randomPt (.-patches s)))]
                (.setXY t (first pt) (second pt))))

            (println "patches; "  (count patches)
                     " turtles: " (count turtles))))))

; STEP:
(set! (.-step (.-prototype (.-Model abm))) ; what doesn't work: (set! (.-step prototype) ...)
      (fn []
        ;(swap! tick inc)
        ;(when (> @tick 500) (.stop @sim))
        (doseq [t (.-turtles @sim)]
          (.rotate t (.randomCentered util (.-wiggle @sim)))
          (.forward t (.-speed @sim)))))

;; Create the model:
(reset! sim (new model sim-params))

(.debug @sim) ; Put Model vars in global name space
(.setup @sim) ; Call explicitly since we disabled it during the 'new' call
(.start @sim) ; Run the model!
