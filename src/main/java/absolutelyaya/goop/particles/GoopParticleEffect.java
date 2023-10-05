package absolutelyaya.goop.particles;

import absolutelyaya.goop.api.ExtraGoopData;
import absolutelyaya.goop.api.IGoopEffectFactory;
import absolutelyaya.goop.api.WaterHandling;
import absolutelyaya.goop.registries.ParticleRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

public class GoopParticleEffect extends AbstractGoopParticleEffect
{
	public static final Codec<GoopParticleEffect> CODEC;
	protected final Vec3d dir;
	ExtraGoopData extraData;
	
	/**
	 * @param color The Particles Color.
	 * @param scale The Particles Scale.
	 * @param dir The normal direction of the surface the particle is attached to.
	 * @param mature Whether the Particle is Mature Content
	 * @param waterHandling How this Goop Particle Effect should interact with water
	 * @param extraData Extra Arguments for custom Particle Effects extending this Class.
	 */
	public GoopParticleEffect(Vec3d color, float scale, Vec3d dir, boolean mature, WaterHandling waterHandling, ExtraGoopData extraData)
	{
		super(color, scale, mature, extraData, waterHandling);
		this.dir = dir;
		this.extraData = extraData;
	}
	
	@ApiStatus.Internal
	public GoopParticleEffect(Vec3d color, float scale, Vec3d dir, boolean mature, byte waterHandling, ExtraGoopData extraData)
	{
		super(color, scale, mature, extraData, WaterHandling.values()[waterHandling]);
		this.dir = dir;
		this.extraData = extraData;
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleRegistry.GOOP;
	}
	
	public Vec3d getDir()
	{
		return dir;
	}
	
	public static class Factory implements ParticleEffect.Factory<GoopParticleEffect>, IGoopEffectFactory
	{
		@Override
		public GoopParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' ');
			Vec3d color = AbstractGoopParticleEffect.readVec3(reader);
			reader.expect(' ');
			float scale = reader.readFloat();
			reader.expect(' ');
			Vec3d dir = readVec3(reader);
			reader.expect(' ');
			boolean mature = reader.readBoolean();
			return new GoopParticleEffect(color, scale, dir, mature, WaterHandling.REPLACE_WITH_CLOUD_PARTICLE, new ExtraGoopData());
		}
		
		@Override
		public GoopParticleEffect read(ParticleType type, PacketByteBuf buf)
		{
			return new GoopParticleEffect(readVec3(buf), buf.readFloat(), readVec3(buf), buf.readBoolean(),
					buf.readEnumConstant(WaterHandling.class), ExtraGoopData.read(buf));
		}
		
		@Override
		public Class<? extends AbstractGoopParticleEffect> getParticleEffectClass()
		{
			return GoopParticleEffect.class;
		}
	}
	
	static
	{
		CODEC = RecordCodecBuilder.create(
				(instance) -> instance.group(Vec3d.CODEC.fieldOf("color").forGetter((effect) -> effect.color),
						Codec.FLOAT.fieldOf("scale").forGetter((effect) -> effect.scale),
						Vec3d.CODEC.fieldOf("dir").forGetter((effect) -> effect.dir),
						Codec.BOOL.fieldOf("mature").forGetter((effect) -> effect.mature),
						Codec.BYTE.fieldOf("waterHandling").forGetter((effect) -> (byte)effect.waterHandling.ordinal()),
						ExtraGoopData.getCodec().fieldOf("extra").forGetter((effect) -> effect.extraData))
						.apply(instance, GoopParticleEffect::new));
	}
}
