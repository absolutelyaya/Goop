package absolutelyaya.goop.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class GoopDropParticleEffect extends AbstractGoopParticleEffect
{
	Identifier effectOverride;
	public static final MapCodec<GoopDropParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codecs.RGB.fieldOf("color").forGetter(AbstractGoopParticleEffect::getColor),
					Codecs.POSITIVE_FLOAT.fieldOf("scale").forGetter(AbstractGoopParticleEffect::getScale),
					PrimitiveCodec.BOOL.fieldOf("mature").forGetter(AbstractGoopParticleEffect::isMature),
					WaterHandling.CODEC.fieldOf("waterhandling").forGetter(AbstractGoopParticleEffect::getWaterHandling)
					).apply(instance, GoopDropParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, GoopDropParticleEffect> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, AbstractGoopParticleEffect::getColor,
			PacketCodecs.FLOAT, AbstractGoopParticleEffect::getScale,
			PacketCodecs.BOOLEAN, AbstractGoopParticleEffect::isMature,
			PacketCodecs.indexed(i -> WaterHandling.values()[i], WaterHandling::ordinal), AbstractGoopParticleEffect::getWaterHandling,
			GoopDropParticleEffect::new
	);
	
	public GoopDropParticleEffect(int color, float scale, boolean mature, WaterHandling waterHandling)
	{
		super(color, scale, mature, waterHandling);
	}
	
	public GoopDropParticleEffect(int color, float scale, boolean mature, WaterHandling waterHandling, Identifier effectOverride)
	{
		super(color, scale, mature, waterHandling);
		this.effectOverride = effectOverride;
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleEffects.GOOP_DROP;
	}
	
	public Identifier getEffectOverride()
	{
		return effectOverride;
	}
}
