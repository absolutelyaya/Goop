package absolutelyaya.goop.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Map;

public interface IColor
{
	int getColor(Map<String, Float> vars);
	
	record Simple(int color) implements IColor
	{
		public static final Codec<IColor> CODEC = YayCodecs.COLOR.comapFlatMap(i -> DataResult.success(new Simple(i)), i -> i.getColor(Map.of()));
		
		@Override
		public int getColor(Map<String, Float> vars)
		{
			return color;
		}
	}
}
