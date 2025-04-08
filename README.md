# Goop
Originally released as part of [ULTRACRAFT](https://github.com/absolutelyaya/ultracraft), Goop is now a standalone mod. It can be used to easily add Slime and Splatter Visual Effects. There have been some major improvements since its debut in ULTRACRAFT as well.

# Improvements in 0.4
As of 0.4, the mod has been overhauled and improved in many aspects and completely changing how it works.
- The Mod is no longer purely a library; it adds some Optional Built-In Effects now!
- Calculations have been optimized
- More accurate surface detection and Puddle placement
- Improved Rendering
  - Puddle and drip Scale changes are now smooth instead of jittery
  - Fancy Goop now has dynamic faces on walls and ceilings as well
- Changed Emitter API to be Datadriven using Json files in Resourcepacks
  - You no longer need to make a mod with goop as a dependency! You can add your own emitters using mostly simple Json. [Here's a Guide on this](https://absolutelyaya.cool/docs/goop/chapter?=emitter).
- Added Client Only Mode
  - If the Server doesn't have Goop installed, Damage Emitters will still *somewhat* work. Vanilla doesn't send enough data to clients to make advanced emitters work, unfortunately.

# Built-in Effects
Since emitters now work using Resourcepacks, there's 3 Optional Resourcepacks included in the mod.
- Slimier Slimes (Enabled by Default)
  - Slimes and Magma Cubes leave behind puddles when they hit the ground and splatter when taking damage
- Sensible Blood (Enabled by Default)
  - Adds Damage Emitters to all Mobs where it makes sense that they could bleed
  - Some Mobs bleed different colors
  - Only Red Blood is marked as mature; all other effects are unaffected by the censor client config
- <code style="color:red">EVERYTHING BLEEDS</code> (Disabled by Default)
  - Adds Red Blood Damage Emitters to all Living Entities

## That's cool, but how do add my own // change them
To make your own Goop emitters, start by making a Resourcepack.<br>
Now, add a `goop_emitters` directory in your namespace directory. In there you can put your Emitter Json files; and that's already it!
For in-depth documentation on how to write emitters, please check [here](https://absolutelyaya.cool/docs/goop/chapter?=emitter).