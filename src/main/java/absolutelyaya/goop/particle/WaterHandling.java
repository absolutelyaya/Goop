package absolutelyaya.goop.particle;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

public enum WaterHandling
{
	REMOVE_PARTICLE,
	REPLACE_WITH_CLOUD_PARTICLE,
	IGNORE;
	
	public static final PrimitiveCodec<WaterHandling> CODEC = new PrimitiveCodec<>()
	{
		@Override
		public <T> DataResult<WaterHandling> read(final DynamicOps<T> ops, final T input)
		{
			return ops.getNumberValue(input).map(Number::byteValue).map(i -> values()[i]);
		}
		
		@Override
		public <T> T write(final DynamicOps<T> ops, final WaterHandling value)
		{
			return ops.createByte((byte)value.ordinal());
		}
		
		@Override
		public String toString()
		{
			return "WaterHandling";
		}
	};
}
