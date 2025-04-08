package absolutelyaya.goop.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;

public record DripParticleEffect(int color, float scale, boolean mature) implements ParticleEffect
{
	public static final MapCodec<DripParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codecs.ARGB.fieldOf("color").forGetter(DripParticleEffect::color),
					Codecs.POSITIVE_FLOAT.fieldOf("scale").forGetter(DripParticleEffect::scale),
					PrimitiveCodec.BOOL.fieldOf("mature").forGetter(DripParticleEffect::mature)
			).apply(instance, DripParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, DripParticleEffect> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, DripParticleEffect::color,
			PacketCodecs.FLOAT, DripParticleEffect::scale,
			PacketCodecs.codec(PrimitiveCodec.BOOL), DripParticleEffect::mature,
			DripParticleEffect::new
	);
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleEffects.DRIP;
	}
}
