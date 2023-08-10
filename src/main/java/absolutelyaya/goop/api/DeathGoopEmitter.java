package absolutelyaya.goop.api;

import absolutelyaya.goop.registries.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.BiFunction;

/**
 * For Emitting Goop when an Entity is Killed. All particle Values can be based on fatal Damage Type as well as the killed Entity's data.
 * @param color The Color of the Particles.
 * @param velocity The initial Velocity of the emitted Particles. W is used to add randomness to each Axis.
 * @param amount The Amount of Particles emitted.
 * @param size The Size of the emitted Particles
 * @param mature Whether the Particles count as Mature Content.
 *               Clients can choose to replace "Mature" Particles colors with a Censor Color.
 * @see absolutelyaya.goop.api.GoopEmitterRegistry
 */
public record DeathGoopEmitter<T extends LivingEntity>(BiFunction<T, DamageSource, Integer> color,
													   BiFunction<T, DamageSource, Vector4f> velocity,
													   BiFunction<T, DamageSource, Integer> amount,
													   BiFunction<T, DamageSource, Float> size,
													   boolean mature,
													   WaterHandling waterHandling) implements IGoopEmitter
{
	@ApiStatus.Internal
	public void emit(T entity, DamageSource fatalDamage)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVector3f(entity.getPos().add(0, entity.getHeight() / 2, 0).toVector3f());
		buf.writeInt(color().apply(entity, fatalDamage));
		Vector4f vel = velocity().apply(entity, fatalDamage);
		buf.writeVector3f(new Vector3f(vel.x, vel.y, vel.z));
		buf.writeFloat(vel.w);
		buf.writeInt(amount().apply(entity, fatalDamage));
		buf.writeFloat(size().apply(entity, fatalDamage));
		buf.writeBoolean(mature());
		entity.getWorld().getPlayers().forEach(p -> {
			if(p instanceof ServerPlayerEntity serverPlayer)
				ServerPlayNetworking.send(serverPlayer, PacketRegistry.EMIT_GOOP_PACKET_ID, buf);
		});
	}
}
