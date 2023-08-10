package absolutelyaya.goop.particles;

import absolutelyaya.goop.api.ExtraGoopData;
import absolutelyaya.goop.api.IGoopEffectFactory;
import absolutelyaya.goop.registries.ParticleRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;

public class EggGoopParticleEffect extends GoopParticleEffect
{
	/**
	 * @param color     The Particles Color.
	 * @param scale     The Particles Scale.
	 * @param dir       The normal direction of the surface the particle is attached to.
	 * @param mature    Whether the Particle is Mature Content
	 * @param extraData Extra Arguments for custom Particle Effects extending this Class.
	 */
	public EggGoopParticleEffect(Vec3d color, float scale, Vec3d dir, boolean mature, ExtraGoopData extraData)
	{
		super(color, scale, dir, mature, extraData);
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleRegistry.EGG_GOOP;
	}
	
	public static class Factory implements ParticleEffect.Factory<EggGoopParticleEffect>, IGoopEffectFactory
	{
		@Override
		public EggGoopParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' ');
			Vec3d color = AbstractGoopParticleEffect.readVec3(reader);
			reader.expect(' ');
			float scale = reader.readFloat();
			reader.expect(' ');
			Vec3d dir = readVec3(reader);
			reader.expect(' ');
			boolean mature = reader.readBoolean();
			return new EggGoopParticleEffect(color, scale, dir, mature, new ExtraGoopData());
		}
		
		@Override
		public EggGoopParticleEffect read(ParticleType type, PacketByteBuf buf)
		{
			return new EggGoopParticleEffect(readVec3(buf), buf.readFloat(), readVec3(buf), buf.readBoolean(), ExtraGoopData.read(buf));
		}
		
		@Override
		public Class<? extends AbstractGoopParticleEffect> getParticleEffectClass()
		{
			return EggGoopParticleEffect.class;
		}
	}
}
