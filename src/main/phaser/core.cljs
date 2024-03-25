(ns phaser.core
  (:require
   ["phaser" :refer [Scene
                     Game]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]))

(println :hellophaser)

(defn SceneExtension []
  (this-as this
           (.call Scene this)
           ;(let [mynewvalue (functioncall1 arg1)] ; setters here
           ;  (set! (.-myPhaserProperty this) mynewvalue))
           ))

; --------------------

(comment
  "javascript ES5 style inheritance. Unfortunately, Clojurescript does not
 support extending classes. This may feel a bit clumsy, but it still
 works quite well")

(set! (.. SceneExtension -prototype)
      (js/Object.create (.-prototype Scene)))

(set! (.. SceneExtension -prototype -constructor)
      SceneExtension)

; --------------------

(set!
 (.. SceneExtension -prototype -update)
 (fn []
   ;(println "Doing stuff")
   ))


(defn scenePreload []
  (this-as this
           (let [load (.-load this)]
             (. load setBaseURL "https://labs.phaser.io")
             (. load image "sky" "assets/skies/space3.png")
             (. load image "logo" "assets/sprites/phaser3-logo.png")
             (. load image "red" "assets/particles/red.png"))))

(set!
 (.. SceneExtension -prototype -preload)
 scenePreload)


(def scale
  #js {:start 1 :end 0})
(def particle-setting
  #js {:speed 100 :scale scale :blendMode "ADD"})

(set! *warn-on-infer* true) ;disables type inference warning if false

(comment

  "I decided to include just a couple of tips for cljs since
   you are probably going to need this in the future.")

(comment "
   Tip 1:

   Add ^:export for functions that will be used by vanilla
   js functions.

   Example below:
   ")

(defn ^:export add-two [number]
  (+ number 2))

(comment "Call the function using phaser.core.add_two(myNumber) in js.")

(comment "
   Tip 2:
  
   Add a typehint using ^js
          
   Example:
          (defn my-func [x]
          (.jsfunc ^js x))
   ")


(comment
  "
   Tip 3:

   Add an externs file.

   Update your shadow-cljs.edn compiler options to include
   a path to an externs file.

   This project includes a particle.js externs file from src/externs which
   is necessary for adding particles.

   The Closure compiler doesn't always understand when certain names should
   be minified or not
   ")


(defn sceneCreate []
  (this-as this
           (println "Loading textures")
           ; this step below has to be done before the let
           (. (. this -add) image 400 300 "sky")
           (let [p (.. this -add (particles 0 0 "red" particle-setting))
                 ; this step above requires an externs file for
                 ; release/production!!
                 ; see src/externs/particle.js!
                 logo (.. this -physics -add (image 400 100 "logo"))]
             (. logo setVelocity 100 200)
             (. logo setBounce 1 1)
             (. logo setCollideWorldBounds true)
             (. p  (startFollow  logo)))))


(set! (.. SceneExtension -prototype -create) sceneCreate)

(def y #js {:y 200})
(def arcade #js {:gravity y})
(def physics #js {:default "arcade" :arcade arcade})
(def myConfig #js {:type 1
                   :width 800
                   :height 600
                   :scene SceneExtension
                   :physics physics})



(defn initFn []
  (println "Starting game...")
  (let [game (new Game myConfig)]
    nil))