# Goop
Originally released as part of [ULTRACRAFT](https://github.com/absolutelyaya/ultracraft), Goop is now a standalone Library. It can be used to easily implement Slime and Splatter Visual Effects. There have been some major improvements since its debut in ULTRACRAFT as well.

## Planned Future Features
I don't know when I'll work on these, but they're things I definitely want to do eventually:
- [ ] Datapack support
  - Add effects using Datapacks instead of having to make extension mods
- [ ] Client Side only Support
  - make effects work without servers needing the mod as well
  - add way to add effects client side; best case using in-game customization gui
- [ ] Forge Port
  - It'd be cool to figure out how to support *all* mod loaders regardless

# That's cool but how do I use it
## Importing the dependency
Feel free to ``include`` the Library, that way Users don't have to download it separately.
```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation(include 'com.github.absolutelyaya:goop:fabric-1.20.1-v0.2')
}
```
And that's pretty much it. You can now use the Goop Particles as you want; You can spawn them like any other particle; or we do something a bit cleaner.

## Registering Emitters
Alright, now that you got the dependency imported, we need to add a new Entrypoint. First, create a class that implements the ``GoopInitalizer`` Interface.
Then go to your ``mod.json`` and add it as the ``goop`` entrypoint like this:
```
"entrypoints": {
  "goop": [
    "absolutelyaya.goop.api.Examples"
  ]
}
```
^ That is where this mods Example emitters are registered. ^<br>
If you just wanna see some quick Example Emitters, check out the ``Examples`` Class.

Since I haven't lost you to the enticing promise of some quick, confusing free example code, let's keep going:<br>
Let's make Slimes a bit more satisfying. For this we will first register a "Damage Emitter". This Emitter will spray the surroundings with green Goop every time a slime takes damage (with certain exceptions).
### Damage Emitters
Go to your Goop Initializer Class. This next bit of Code is a bit intimidating, but once you understand how it works, I'm sure you'll be able to use it easily.
```
GoopEmitterRegistry.registerEmitter(EntityType.SLIME, new DamageGoopEmitter<SlimeEntity>(
		(slime, data) -> 0x2caa3b,
		(slime, data) -> new Vector4f(0f, 0f, 0f, MathHelper.clamp(data.amount() / 8f, 0.25f, 2f)),
		(slime, data) -> data.source().isIn(TagRegistry.PHYSICAL) ? Math.round(MathHelper.clamp(data.amount() / 2f, 2f, 12f)) : 0,
		(slime, data) -> MathHelper.clamp(data.amount() / 4f, 0.25f, 1)
));
```
Let's go through this beast step by step. This is a call to the method ``GoopEmitterRegistry#registerEmitter``.<br>
The first Argument is the EntityType you want to assign this Emitter to.<br>
Next is the actual Emitter; since we want a Damage Emitter, we instantiate ``DamageGoopEmitter<TargetEntityClass>``.
The most intimidating bit are the parameters for the Emitter;<br>
Each Argument is a ``BiFunction<>``. You get the instance of the entity and a ``DamageData`` which contains, damage amount and source. 
1. Color<br>Either hook up a method or use a Lambda to return an RGB color in int form.
2. Velocity<br>Return a Vector4f; the first 3 values are the direction and the fourth value is used for added randomness.<br>In The Example, Goop flies in all directions completely at random; the higher the damage, the higher the Velocity.
3. Amount<br>Return the amount of particles as an Int. In the example, Only Physical Damage will actually emit Goop; meaning Fire or Poison Damage would get ignored. If that's what you want is up to you of course.
4. Size<br>Finally return a float representing the size of the goop. In the example, higher damage results in bigger Goop.

And there you go, Slimes now splatter apart when damaged. Wonderful!<br>
All Emitter types work very similarily, but I'll go over the small differences anyways.
### Death Emitters
Death Emitters "data" is only the Fatal DamageSource.
```
//This causes Snow Golems to melt into blue Goop upon Death.
//Since the particle is supposed to resemble water, it will simply disappear when making content with actual Water.
GoopEmitterRegistry.registerEmitter(EntityType.SNOW_GOLEM, new DeathGoopEmitter<SnowGolemEntity>(
		(snowGolem, data) -> 0x4690da,
		(snowGolem, data) -> new Vector4f(0f, 0f, 0f, 0.5f),
		(snowGolem, data) -> 2 + snowGolem.getRandom().nextInt(4),
		(snowGolem, data) -> 0.5f + snowGolem.getRandom().nextFloat() / 0.5f
).setWaterHandling(WaterHandling.REMOVE_PARTICLE));
```
Water Handling will be looked at a bit further down; I just didn't want to remove it from the example Emitter as it's fitting for the effect.
### Landing Emitters
Going back to Slimes, this emitter will make them leave behind a trail of goop splodges. Landing Emitters "data" is a float representing how far the entity fell before landing.
```
//This causes slimes to leave behind splodges of Green Goop when landing from a jump.
GoopEmitterRegistry.registerEmitter(EntityType.SLIME, new LandingGoopEmitter<SlimeEntity>(
		(slime, height) -> 0x2caa3b,
		(slime, height) -> new Vector4f(0f, -0f, 0f, 0.1f),
		(slime, height) -> 1,
		(slime, height) -> MathHelper.clamp(height / 4f, 0.25f, 1) * slime.getSize()
));
```
The only kind of interesting thing about this example, is that it uses data from the entity to determine the goops size.
### Projectile Emitters
Projectile emitters data is the HitResult of the Projectile.
```
//Makes Eggs leave behind... egg.. when thrown at something.
GoopEmitterRegistry.registerProjectileEmitter(EntityType.EGG, new ProjectileHitGoopEmitter<EggEntity>(
		(egg, data) -> 0xffffff,
		(egg, data) -> {
			Vec3d vel = egg.getVelocity();
			return new Vector4f((float)vel.x, (float)vel.y, (float)vel.z, 0f);
		},
		(egg, data) -> 1,
		(egg, data) -> 0.5f
).noDrip().setParticleEffectOverride(new Identifier(Goop.MOD_ID, "egg_goop"), new ExtraGoopData()));
```
Since ProjectileEntities aren't LivingEntities, Projectile Emitters use a different register method (``GoopEmitterRegistry#registerProjectileEmitter``).<br>
"Effect Overrides" will be explained a bit further down.
## Advanced Stuff
Let's not start with effect overrides tho. I'd like to get more complex as we go.
### "Mature" Content flagging
This feature is intended to give Players the choice to disable VFX they might find distressing or gross. Please keep that in mind when adding Emitters and flag them accordingly.<br>
To flag an Emitter as "Mature" Content, use ``.markMature()`` right after instantiating the emitter.<br>
Already emitted particles won't be censored/uncensored retroactively if a client changes their settings. Rejoining the world will remove all existing Goop instantly though.
### "Dev" Content flagging
Dev Emitters (like all Example emitters for instance) will only emit Particles for players that have the "Show Dev Particles" Client Setting enabled.<br>
To flag an Emitter as "Dev Content" Content, use ``.markDev()`` right after instantiating the emitter.
### Disable Dripping
To make an emitters goop not drip when it covers a ceiling, use ``.noDrip()`` right after instantiating the emitter.
### Disable Warping/Deformation
To make an emitters goop not deform when it covers a wall or ceiling, use ``.noDeform()`` right after instantiating the emitter.
### Water Handling
Currently, there are three options how to handle contact with water:
1. REMOVE_PARTICLE<br>The Default; Removes Goop the moment they make contact with Water.
2. REPLACE_WITH_CLOUD_PARTICLE<br>Turns Goop into Cloud Particles of the same color and size when they touch water.
3. IGNORE<br>Does nothing.

You set an Emitters Water Handling type using ``.setWaterHandling(WaterHandling.X)`` right after instantiating the emitter.
### Effect Overrides
Oh boy. This is the most advanced feature in the Library, so I'll assume you have some experience adding your own Particles already.<br>
Alright, start by Making a new Particle extending ``GoopParticle`` and a new ParticleEffect extending ``GoopParticleEffect``.<br><br>
**If** your custom Effect needs more data than normal Goop, then also create a class extending ``ExtraGoopData``. You then need to register this new "Extra Data Type" in the GoopInitalizer using ``GoopEmitterRegistry#registerExtraDataType(Identifier, Class)``.<br><br>
Alright, now that you have made your own Goop Particle and an Emitter intending to use it, put ``.setParticleEffectOverride(new Identifier("examplemod", "coolgoop"), new ExtraGoopData())`` right behind the Emiters Instantiation. Replace the Identifier with your Particles and **if** you need your own additional arguments for your effect, replace ``new ExtraGoopData()`` with an instantiation of your own Extra Data Type.<br>
**Keep in mind that the constructor of your Goop Particle has to be the exact same as Default Goop.**<br><br>
And yea, that should be it! I look forward to seeing what you'll do with these Particle VFX.