package absolutelyaya.goop.api;

import absolutelyaya.goop.registries.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class AbstractGoopEmitter<T extends Entity> implements IGoopEmitter
{
	protected Identifier particleEffectOverride;
	protected boolean mature = false, dev = false, drip = true, deform = true;
	protected WaterHandling waterHandling = WaterHandling.REPLACE_WITH_CLOUD_PARTICLE;
	protected ExtraGoopData extraGoopData = new ExtraGoopData();
	
	/**
	 * Replace the Goop Puddle Particle effect with a different one.<br>
	 * NOTE: The Effect HAS TO extend GoopParticleEffect and contain a Constructor taking the same arguments as the standard one!
	 * @param override The identifier of the replacement Particle Effect
	 * @param data Extra Data for the replacement Particle Effect. Override this class use it to supply any extra data your custom Goop particle needs.
	 * @see absolutelyaya.goop.particles.GoopParticleEffect
	 * @see absolutelyaya.goop.api.ExtraGoopData
	 */
	public AbstractGoopEmitter<T> setParticleEffectOverride(Identifier override, ExtraGoopData data)
	{
		this.particleEffectOverride = override;
		this.extraGoopData = data;
		return this;
	}
	
	/**
	 * Marks this emitter's Particles as Mature Content. Clients can choose to replace "Mature" Particles colors with a Censor Color.
	 */
	public AbstractGoopEmitter<T> markMature()
	{
		mature = true;
		return this;
	}
	
	/**
	 * Marks this emitter as Dev Content. It will only emit particles if the "Show Dev Particles" Client Setting is turned on.
	 */
	public AbstractGoopEmitter<T> markDev()
	{
		dev = true;
		return this;
	}
	
	/**
	 * Disables dripping for ceiling goop caused by this emitter.
	 */
	public AbstractGoopEmitter<T> noDrip()
	{
		drip = false;
		return this;
	}
	
	/**
	 * Disables deformation for ceiling & wall goop caused by this emitter.
	 */
	public AbstractGoopEmitter<T> noDeform()
	{
		deform = false;
		return this;
	}
	
	/**
	 * Define how this emitter's particles should react on contact with water.
	 * The Default is WaterHandling.REPLACE_WITH_CLOUD_PARTICLE
	 */
	public AbstractGoopEmitter<T> setWaterHandling(WaterHandling handling)
	{
		this.waterHandling = handling;
		return this;
	}
	
	@ApiStatus.Internal
	protected void emitInternal(T entity, int color, Vector4f velocity, int amount, float scale, WaterHandling waterHandling)
	{
		//TODO: fetch whether the target client even has dev emitters enabled; if not, don't send any data.
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(dev);
		buf.writeVector3f(entity.getPos().add(0, entity.getHeight() / 2, 0).subtract(entity.getVelocity().multiply(0.2f)).toVector3f());
		buf.writeInt(color);
		buf.writeVector3f(new Vector3f(velocity.x, velocity.y, velocity.z));
		buf.writeFloat(velocity.w);
		buf.writeInt(Math.max(amount, 0));
		buf.writeFloat(Math.max(scale, 0));
		buf.writeBoolean(mature);
		buf.writeBoolean(drip);
		buf.writeBoolean(deform);
		buf.writeEnumConstant(waterHandling);
		boolean b = particleEffectOverride != null;
		buf.writeBoolean(b);
		if(b)
		{
			buf.writeIdentifier(particleEffectOverride);
			extraGoopData.write(buf);
		}
		entity.getWorld().getPlayers().forEach(p -> {
			if(p instanceof ServerPlayerEntity serverPlayer)
				ServerPlayNetworking.send(serverPlayer, PacketRegistry.EMIT_GOOP_PACKET_ID, buf);
		});
	}
}
