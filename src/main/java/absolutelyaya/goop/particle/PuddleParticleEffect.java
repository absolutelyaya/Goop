package absolutelyaya.goop.particle;

import absolutelyaya.goop.data.FinalGoopData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Direction;

public record PuddleParticleEffect(FinalGoopData data, Direction up) implements ParticleEffect
{
	public static final MapCodec<PuddleParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					FinalGoopData.CODEC.forGetter(PuddleParticleEffect::data),
					Direction.CODEC.fieldOf("up").forGetter(PuddleParticleEffect::up)
			).apply(instance, PuddleParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, PuddleParticleEffect> PACKET_CODEC = PacketCodec.tuple(
			FinalGoopData.PACKET_CODEC, PuddleParticleEffect::data,
			Direction.PACKET_CODEC, PuddleParticleEffect::up,
			PuddleParticleEffect::new
	);
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleEffects.PUDDLE;
	}
}
