package absolutelyaya.goop.api;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.registries.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public class Examples implements GoopInitializer
{
	@Override
	public void registerGoopEmitters()
	{
		//Don't register these example emitters outside of development environments
		if(!FabricLoader.getInstance().isDevelopmentEnvironment())
			return;
		
		//All examples shown should work with any Entity, including ones added by other mods.
		//Entities can have multiple emitters, even multiple of the same type.
		
		//This causes slimes to leave behind splodges of Green Goop when landing from a jump.
		GoopEmitterRegistry.registerEmitter(EntityType.SLIME, new LandingGoopEmitter<SlimeEntity>(
				(slime, height) -> 0x2caa3b,
				(slime, height) -> new Vector4f(0f, -0f, 0f, 0.1f),
				(slime, height) -> 1,
				(slime, height) -> MathHelper.clamp(height / 4f, 0.25f, 1) * slime.getSize()
		));
		
		GoopEmitterRegistry.registerEmitter(EntityType.SLIME, new DamageGoopEmitter<SlimeEntity>(
				(slime, data) -> 0x2caa3b,
				(slime, data) -> new Vector4f(0f, 0f, 0f, MathHelper.clamp(data.amount() / 8f, 0.25f, 2f)),
				(slime, data) -> data.source().isIn(TagRegistry.PHYSICAL) ? Math.round(MathHelper.clamp(data.amount() / 2f, 2f, 12f)) : 0,
				(slime, data) -> MathHelper.clamp(data.amount() / 4f, 0.25f, 1)
		));
		
		//This causes Zombies to bleed when Damaged by a Physical Attack.
		//If the Client has Censor Mature Content on, these particles will render in their Censor Color.
		GoopEmitterRegistry.registerEmitter(EntityType.ZOMBIE, new DamageGoopEmitter<ZombieEntity>(
				(zombie, data) -> 0x940904,
				(zombie, data) -> new Vector4f(0f, 0f, 0f, MathHelper.clamp(data.amount() / 8f, 0.25f, 2f)),
				(zombie, data) -> data.source().isIn(TagRegistry.PHYSICAL) ? Math.round(MathHelper.clamp(data.amount() / 2f, 2f, 12f)) : 0,
				(zombie, data) -> MathHelper.clamp(data.amount() / 4f, 0.25f, 1)
		).markMature());
		
		//This causes Snow Golems to melt into blue Goop upon Death.
		//Since the particle is supposed to resemble water, it will simply disappear when making content with actual Water.
		GoopEmitterRegistry.registerEmitter(EntityType.SNOW_GOLEM, new DeathGoopEmitter<SnowGolemEntity>(
				(snowGolem, data) -> 0x4690da,
				(snowGolem, data) -> new Vector4f(0f, 0f, 0f, 0.5f),
				(snowGolem, data) -> 2 + snowGolem.getRandom().nextInt(4),
				(snowGolem, data) -> 0.5f + snowGolem.getRandom().nextFloat() / 0.5f
		).setWaterHandling(WaterHandling.REMOVE_PARTICLE));
		
		//Makes Eggs leave behind... egg.. when thrown at something.
		GoopEmitterRegistry.registerProjectileEmitter(EntityType.EGG, new ProjectileHitGoopEmitter<EggEntity>(
				(egg, data) -> 0xffffff,
				(egg, data) -> {
					Vec3d vel = egg.getVelocity();
					return new Vector4f((float)vel.x, (float)vel.y, (float)vel.z, 0f);
				},
				(egg, data) -> 1,
				(egg, data) -> 0.5f
		).markMature().setParticleEffectOverride(new Identifier(Goop.MOD_ID, "egg_goop"), new ExtraGoopData()));
	}
}
