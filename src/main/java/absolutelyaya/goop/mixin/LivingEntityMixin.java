package absolutelyaya.goop.mixin;

import absolutelyaya.goop.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Inject(method = "damage", at = @At(value = "RETURN"))
	void onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if(getWorld().isClient)
			return;
		Optional<List<DamageGoopEmitter<?>>> emitters = GoopEmitterRegistry.getDamageEmitters((EntityType<? extends LivingEntity>)getType());
		if(emitters.isEmpty())
			return;
		for (DamageGoopEmitter emitter : emitters.get())
			emitter.emit((LivingEntity)(Object)this, new DamageData(source, amount));
	}
	
	@Inject(method = "onDeath", at = @At(value = "RETURN"))
	void onDeath(DamageSource damageSource, CallbackInfo ci)
	{
		if(getWorld().isClient)
			return;
		Optional<List<DeathGoopEmitter<?>>> emitters = GoopEmitterRegistry.getDeathEmitters((EntityType<? extends LivingEntity>)getType());
		if(emitters.isEmpty())
			return;
		for (DeathGoopEmitter emitter : emitters.get())
			emitter.emit((LivingEntity)(Object)this, damageSource);
	}
	
	@Override
	public void onLanding()
	{
		if (getWorld().isClient || fallDistance < 0.05)
		{
			super.onLanding();
			return;
		}
		Optional<List<LandingGoopEmitter<?>>> emitters = GoopEmitterRegistry.getLandingEmitters((EntityType<? extends LivingEntity>) getType());
		if (emitters.isEmpty())
		{
			super.onLanding();
			return;
		}
		for (LandingGoopEmitter emitter : emitters.get())
			emitter.emit((LivingEntity) (Object) this, fallDistance);
		super.onLanding();
	}
}
