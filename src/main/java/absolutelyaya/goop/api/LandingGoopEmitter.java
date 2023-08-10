package absolutelyaya.goop.api;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;

import java.util.function.BiFunction;

/**
 * For Emitting Goop when an Entity is Damaged. All particle Values can be based on how long the entities drop was.
 * @see absolutelyaya.goop.api.AbstractGoopEmitter
 * @see absolutelyaya.goop.api.GoopEmitterRegistry
 */
public class LandingGoopEmitter<T extends LivingEntity> extends AbstractGoopEmitter<T>
{
	protected final BiFunction<T, Float, Integer> color;
	protected final BiFunction<T, Float, Vector4f> velocity;
	protected final BiFunction<T, Float, Integer> amount;
	protected final BiFunction<T, Float, Float> size;
	
	/**
	 * @param color The Color of the Particles.
	 * @param velocity The initial Velocity of the emitted Particles. W is used to add randomness to each Axis.
	 * @param amount The Amount of Particles emitted.
	 * @param size The Size of the emitted Particles
	 */
	public LandingGoopEmitter(BiFunction<T, Float, Integer> color, BiFunction<T, Float, Vector4f> velocity, BiFunction<T, Float, Integer> amount, BiFunction<T, Float, Float> size)
	{
		this.color = color;
		this.velocity = velocity;
		this.amount = amount;
		this.size = size;
	}
	
	@ApiStatus.Internal
	public void emit(T entity, float height)
	{
		emitInternal(entity,
				color.apply(entity, height),
				velocity.apply(entity, height),
				amount.apply(entity, height),
				size.apply(entity, height),
				waterHandling);
	}
}
