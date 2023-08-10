package absolutelyaya.goop.api;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;

import java.util.function.BiFunction;

/**
 * For Emitting Goop when an Entity is Damaged. All particle Values can be based on Damage Type and Amount as well as the damaged Entity's data.
 * @see absolutelyaya.goop.api.AbstractGoopEmitter
 * @see absolutelyaya.goop.api.GoopEmitterRegistry
 */
public class DamageGoopEmitter<T extends LivingEntity> extends AbstractGoopEmitter<T>
{
	protected final BiFunction<T, DamageData, Integer> color;
	protected final BiFunction<T, DamageData, Vector4f> velocity;
	protected final BiFunction<T, DamageData, Integer> amount;
	protected final BiFunction<T, DamageData, Float> size;
	
	/**
	 * @param color The Color of the Particles.
	 * @param velocity The initial Velocity of the emitted Particles. W is used to add randomness to each Axis.
	 * @param amount The Amount of Particles emitted.
	 * @param size The Size of the emitted Particles
	 */
	public DamageGoopEmitter(BiFunction<T, DamageData, Integer> color, BiFunction<T, DamageData, Vector4f> velocity, BiFunction<T, DamageData, Integer> amount, BiFunction<T, DamageData, Float> size)
	{
		this.color = color;
		this.velocity = velocity;
		this.amount = amount;
		this.size = size;
	}
	
	@ApiStatus.Internal
	public void emit(T entity, DamageData data)
	{
		emitInternal(entity,
				color.apply(entity, data),
				velocity.apply(entity, data),
				amount.apply(entity, data),
				size.apply(entity, data),
				waterHandling);
	}
}
