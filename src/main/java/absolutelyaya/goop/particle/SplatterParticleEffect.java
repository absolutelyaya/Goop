package absolutelyaya.goop.particle;

import absolutelyaya.goop.data.FinalGoopData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record SplatterParticleEffect(FinalGoopData data, Optional<Identifier> effectOverride) implements ParticleEffect
{
	public static final MapCodec<SplatterParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					FinalGoopData.CODEC.forGetter(SplatterParticleEffect::data),
					Identifier.CODEC.optionalFieldOf("effect-override").forGetter(SplatterParticleEffect::effectOverride)
			).apply(instance, SplatterParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, SplatterParticleEffect> PACKET_CODEC = PacketCodec.tuple(
			FinalGoopData.PACKET_CODEC, SplatterParticleEffect::data,
			PacketCodecs.optional(Identifier.PACKET_CODEC), SplatterParticleEffect::effectOverride,
			SplatterParticleEffect::new
	);
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleEffects.SPLATTER;
	}
}
