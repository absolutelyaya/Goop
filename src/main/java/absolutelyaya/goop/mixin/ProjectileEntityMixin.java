package absolutelyaya.goop.mixin;

import absolutelyaya.goop.api.GoopEmitterRegistry;
import absolutelyaya.goop.api.ProjectileHitGoopEmitter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity
{
	public ProjectileEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Inject(method = "onEntityHit", at = @At(value = "RETURN"))
	void onEntityHit(EntityHitResult hit, CallbackInfo ci)
	{
		if(getWorld().isClient)
			return;
		onHit(hit);
	}
	@Inject(method = "onBlockHit", at = @At(value = "RETURN"))
	void onBlockHit(BlockHitResult hit, CallbackInfo ci)
	{
		if(getWorld().isClient)
			return;
		onHit(hit);
	}
	
	void onHit(HitResult hit)
	{
		Optional<List<ProjectileHitGoopEmitter<?>>> emitters = GoopEmitterRegistry.getProjectileHitEmitters((EntityType<? extends LivingEntity>)getType());
		if(emitters.isEmpty())
			return;
		for (ProjectileHitGoopEmitter emitter : emitters.get())
			emitter.emit((ProjectileEntity)(Object)this, hit);
	}
}
