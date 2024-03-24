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

(set! *warn-on-infer* false) ;disables type inference warning

(defn sceneCreate []
  (this-as this
           (. (. this -add) image 400 300 "sky") ; this step has to be done before
           (let [add (. this -add)
                 physics (. this -physics)
                 add-physics (. physics -add)
                     ;----
                 p (. add particles 0 0 "red" particle-setting)

                     ; above causes a warning
                 logo (. add-physics image 400 100 "logo")]
             (println "Loading textures")
                 ;(. add image 400 300 "logo")
             (. logo setVelocity 100 200)
             (. logo setBounce 1 1)
             (. logo setCollideWorldBounds true)
             (. p startFollow logo))))


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