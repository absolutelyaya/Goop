package absolutelyaya.goop.data;

import absolutelyaya.goop.particle.WaterHandling;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record ModularGoopData(IColor color, Calculatable size, boolean mature, WaterHandling waterHandling, boolean drip, boolean deform)
{
	public static final Codec<ModularGoopData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.either(IColor.Simple.CODEC, Calculatable.Color.CODEC).xmap(Either::unwrap, Either::left).fieldOf("color").forGetter(ModularGoopData::color),
			Calculatable.CODEC.fieldOf("size").forGetter(ModularGoopData::size),
			Codec.BOOL.optionalFieldOf("mature", false).forGetter(ModularGoopData::mature),
			WaterHandling.CODEC.optionalFieldOf("waterhandling", WaterHandling.REPLACE_WITH_CLOUD_PARTICLE).forGetter(ModularGoopData::waterHandling),
			Codec.BOOL.optionalFieldOf("drip", true).forGetter(ModularGoopData::drip),
			Codec.BOOL.optionalFieldOf("deform", true).forGetter(ModularGoopData::deform)
	).apply(instance, ModularGoopData::new));
	
	public FinalGoopData calculate(Map<String, Float> vars)
	{
		return new FinalGoopData(color().getColor(vars), size.calculate(vars), mature, waterHandling, drip, deform);
	}
}
