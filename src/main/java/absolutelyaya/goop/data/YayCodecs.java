package absolutelyaya.goop.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.awt.*;
import java.util.HexFormat;
import java.util.List;

public class YayCodecs
{
	public static final Codec<Integer> HEX = Codec.STRING.xmap(HexFormat::fromHexDigits, Integer::toHexString);
	
	public static final Codec<Integer> COLOR = Codec.either(Codec.either(HEX, Codec.INT).xmap(Either::unwrap, Either::left),
			Codec.FLOAT.listOf(3, 4).comapFlatMap(YayCodecs::colorFromFloats, YayCodecs::colorComponentsFromColor)).xmap(Either::unwrap, Either::left);
	
	public static DataResult<Integer> colorFromFloats(List<Float> floats)
	{
		if(floats.size() == 3 || floats.size() == 4)
			return DataResult.success(new Color(floats.get(0), floats.get(1), floats.get(2), floats.size() == 4 ? floats.get(3) : 1f).getRGB());
		return DataResult.error(() -> "Input is not a List of 3 or 4 Elements.");
	}
	
	public static List<Float> colorComponentsFromColor(int color)
	{
		float[] components = new Color(color).getColorComponents(null);
		if(components.length == 3 || components.length == 4)
			return List.of(components[0], components[1], components[2], components.length == 4 ? components[3] : 1f);
		return List.of(1f, 0f, 1f, 1f);
	}
}
