package absolutelyaya.goop.client.emitter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringIdentifiable;

public enum EmitterType implements StringIdentifiable
{
	DAMAGE(DamageEmitter.CODEC),
	DEATH(DeathEmitter.CODEC),
	LANDING(LandingEmitter.CODEC);
	public static final Codec<AbstractEmitter> CODEC =
			StringIdentifiable.createCodec(EmitterType::values).dispatch("type", AbstractEmitter::getType, EmitterType::getCodec);
	
	public final MapCodec<? extends AbstractEmitter> codec;
	
	EmitterType(MapCodec<? extends AbstractEmitter> codec)
	{
		this.codec = codec;
	}
	
	@Override
	public String asString()
	{
		return name();
	}
	
	public MapCodec<? extends AbstractEmitter> getCodec()
	{
		return codec;
	}
}
