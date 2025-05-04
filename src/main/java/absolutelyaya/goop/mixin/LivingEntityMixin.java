package absolutelyaya.goop.mixin;

import absolutelyaya.goop.network.EntityDamagePayload;
import absolutelyaya.goop.network.EntityDeathPayload;
import absolutelyaya.goop.network.EntityLandPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Inject(method = "damage", at = @At("TAIL"))
	void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if(world.isClient)
			return;
		getNearbyPlayers().forEach(p -> ServerPlayNetworking.send(p,
				new EntityDamagePayload(getId(), RegistryEntry.of(source.getType()), amount)));
	}
	
	@Inject(method = "handleFallDamage", at = @At("HEAD"))
	void onLanded(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir)
	{
		getNearbyPlayers().forEach(p -> ServerPlayNetworking.send(p,
				new EntityLandPayload(getId(), fallDistance)));
	}
	
	@Inject(method = "onDeath", at = @At("TAIL"))
	void onDeath(DamageSource damageSource, CallbackInfo ci)
	{
		if(getWorld().isClient)
			return;
		getNearbyPlayers().forEach(p -> ServerPlayNetworking.send(p,
				new EntityDeathPayload(getId(), RegistryEntry.of(damageSource.getType()))));
	}
	
	@Unique
	List<ServerPlayerEntity> getNearbyPlayers()
	{
		return getWorld().getEntitiesByType(TypeFilter.instanceOf(ServerPlayerEntity.class), Box.from(getPos()).expand(32), i -> true);
	}
}
