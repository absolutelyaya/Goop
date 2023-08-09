package absolutelyaya.goop.api;

import absolutelyaya.goop.registries.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;

public class Examples implements GoopInitializer
{
	@Override
	public void registerGoopEmitters()
	{
		//Don't register these example emitters outside of development environments
		if(!FabricLoader.getInstance().isDevelopmentEnvironment())
			return;
		
		//This causes slimes to leave behind splodges of Green Goop when landing from a jump.
		GoopEmitterRegistry.register(EntityType.SLIME, new LandingGoopEmitter<SlimeEntity>(
				(slime, height) -> 0x2caa3b,
				(slime, height) -> new Vector4f(0f, -0f, 0f, 0.1f),
				(slime, height) -> 1,
				(slime, height) -> MathHelper.clamp(height / 4f, 0.25f, 1) * slime.getSize(),
				false,
				WaterHandling.REPLACE_WITH_CLOUD_PARTICLE
		));
		
		GoopEmitterRegistry.register(EntityType.SLIME, new DamageGoopEmitter<SlimeEntity>(
				(slime, data) -> 0x2caa3b,
				(slime, data) -> new Vector4f(0f, 0f, 0f, MathHelper.clamp(data.amount() / 8f, 0.25f, 2f)),
				(slime, data) -> data.source().isIn(TagRegistry.PHYSICAL) ? Math.round(MathHelper.clamp(data.amount() / 2f, 2f, 12f)) : 0,
				(slime, data) -> MathHelper.clamp(data.amount() / 4f, 0.25f, 1),
				false,
				WaterHandling.REPLACE_WITH_CLOUD_PARTICLE
		));
		
		//This causes Zombies to bleed when Damaged by a Physical Attack.
		//If the Client has Censor Mature Content on, these particles will render in their Censor Color.
		GoopEmitterRegistry.register(EntityType.ZOMBIE, new DamageGoopEmitter<ZombieEntity>(
				(zombie, data) -> 0x940904,
				(zombie, data) -> new Vector4f(0f, 0f, 0f, MathHelper.clamp(data.amount() / 8f, 0.25f, 2f)),
				(zombie, data) -> data.source().isIn(TagRegistry.PHYSICAL) ? Math.round(MathHelper.clamp(data.amount() / 2f, 2f, 12f)) : 0,
				(zombie, data) -> MathHelper.clamp(data.amount() / 4f, 0.25f, 1),
				true,
				WaterHandling.REPLACE_WITH_CLOUD_PARTICLE
		));
	}
}
