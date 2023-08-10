package absolutelyaya.goop.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;

import java.util.function.BiFunction;

/**
 * For Emitting Goop when an Entity is Killed. All particle Values can be based on fatal Damage Type as well as the killed Entity's data.
 * @see absolutelyaya.goop.api.AbstractGoopEmitter
 * @see absolutelyaya.goop.api.GoopEmitterRegistry
 */
public class DeathGoopEmitter<T extends LivingEntity> extends AbstractGoopEmitter<T>
{
	protected final BiFunction<T, DamageSource, Integer> color;
	protected final BiFunction<T, DamageSource, Vector4f> velocity;
	protected final BiFunction<T, DamageSource, Integer> amount;
	protected final BiFunction<T, DamageSource, Float> size;
	
	/**
	 * @param color The Color of the Particles.
	 * @param velocity The initial Velocity of the emitted Particles. W is used to add randomness to each Axis.
	 * @param amount The Amount of Particles emitted.
	 * @param size The Size of the emitted Particles
	 */
	public DeathGoopEmitter(BiFunction<T, DamageSource, Integer> color, BiFunction<T, DamageSource, Vector4f> velocity, BiFunction<T, DamageSource, Integer> amount, BiFunction<T, DamageSource, Float> size)
	{
		this.color = color;
		this.velocity = velocity;
		this.amount = amount;
		this.size = size;
	}
	
	@ApiStatus.Internal
	public void emit(T entity, DamageSource fatalDamage)
	{
		emitInternal(entity,
				color.apply(entity, fatalDamage),
				velocity.apply(entity, fatalDamage),
				amount.apply(entity, fatalDamage),
				size.apply(entity, fatalDamage),
				waterHandling);
	}
}
