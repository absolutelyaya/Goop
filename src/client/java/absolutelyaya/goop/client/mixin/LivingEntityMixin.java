package absolutelyaya.goop.client.mixin;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.client.emitter.EmitterManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
	@Shadow @Final private static TrackedData<Float> HEALTH;
	
	@Unique float lastHealth;
	
	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Inject(method = "onTrackedDataSet", at = @At("HEAD"))
	void onTrackedDataSet(TrackedData<?> data, CallbackInfo ci)
	{
		if(!Goop.CLIENT_ONLY)
			return;
		if(data.equals(HEALTH))
		{
			float delta = lastHealth - dataTracker.get(HEALTH);
			lastHealth = dataTracker.get(HEALTH);
			if(delta <= 0)
				return;
			getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getOrEmpty(DamageTypes.GENERIC).ifPresent(value -> {
				EmitterManager.onDamage((LivingEntity)((Object)this), RegistryEntry.of(value), delta);
			});
		}
	}
}
