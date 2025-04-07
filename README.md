# Goop
Originally released as part of [ULTRACRAFT](https://github.com/absolutelyaya/ultracraft), Goop is now a standalone mod. It can be used to easily add Slime and Splatter Visual Effects. There have been some major improvements since its debut in ULTRACRAFT as well.

# Improvements in 0.4
As of 0.4, the mod has been overhauled and improved in many aspects and completely changing how it works.
- Calculations have been optimized
- More accurate surface detection and Puddle placement
- Improved Rendering
  - Puddle and drip Scale changes are now smooth instead of jittery
  - Fancy Goop now has dynamic faces on walls and ceilings as well
- Changed Emitter API to be Datadriven using Json files in Resourcepacks
  - You no longer need to make a mod with goop as a dependency! You can add your own emitters using mostly simple Json. [Here's a Guide on this](https://absolutelyaya.cool/docs/goop/chapter?=emitter).
- Added Client Only Mode
  - If the Server doesn't have Goop installed, Damage Emitters will still *somewhat* work. Vanilla doesn't send enough data to clients to make advanced emitters work, unfortunately.

# That's cool, but how do I use it
To make your own Goop emitters, start by making a Resourcepack.<br>
Now, add a `goop_emitters` directory in your namespace directory. In there you can put your Emitter Json files; and that's already it!
For in-depth documentation on how to write emitters, please check [here](https://absolutelyaya.cool/docs/goop/chapter?=emitter).