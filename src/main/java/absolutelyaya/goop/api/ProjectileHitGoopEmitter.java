package absolutelyaya.goop.api;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;

import java.util.function.BiFunction;

/**
 * For Emitting Goop when a Projectile hits something. All particle Values can be based on HitResult as well as the Projectiles data.
 * @see absolutelyaya.goop.api.AbstractGoopEmitter
 * @see absolutelyaya.goop.api.GoopEmitterRegistry
 */
public class ProjectileHitGoopEmitter<T extends ProjectileEntity> extends AbstractGoopEmitter<T>
{
	protected final BiFunction<T, HitResult, Integer> color;
	protected final BiFunction<T, HitResult, Vector4f> velocity;
	protected final BiFunction<T, HitResult, Integer> amount;
	protected final BiFunction<T, HitResult, Float> size;
	
	/**
	 * @param color The Color of the Particles.
	 * @param velocity The initial Velocity of the emitted Particles. W is used to add randomness to each Axis.
	 * @param amount The Amount of Particles emitted.
	 * @param size The Size of the emitted Particles
	 */
	public ProjectileHitGoopEmitter(BiFunction<T, HitResult, Integer> color, BiFunction<T, HitResult, Vector4f> velocity, BiFunction<T, HitResult, Integer> amount, BiFunction<T, HitResult, Float> size)
	{
		this.color = color;
		this.velocity = velocity;
		this.amount = amount;
		this.size = size;
	}
	
	@ApiStatus.Internal
	public void emit(T entity, HitResult hit)
	{
		emitInternal(entity,
				color.apply(entity, hit),
				velocity.apply(entity, hit),
				amount.apply(entity, hit),
				size.apply(entity, hit),
				waterHandling);
	}
}
