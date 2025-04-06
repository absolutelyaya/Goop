package absolutelyaya.goop.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record BaseGoopData(int color, float scale, boolean mature, WaterHandling waterHandling, boolean drip, boolean deforms)
{
	public static final MapCodec<BaseGoopData> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codecs.ARGB.fieldOf("color").forGetter(BaseGoopData::color),
					Codecs.POSITIVE_FLOAT.fieldOf("scale").forGetter(BaseGoopData::scale),
					Codec.BOOL.optionalFieldOf("mature", false).forGetter(BaseGoopData::mature),
					WaterHandling.CODEC.optionalFieldOf("waterhandling", WaterHandling.REPLACE_WITH_CLOUD_PARTICLE).forGetter(BaseGoopData::waterHandling)
			).apply(instance, BaseGoopData::new)
	);
	public static final PacketCodec<RegistryByteBuf, BaseGoopData> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, BaseGoopData::color,
			PacketCodecs.FLOAT, BaseGoopData::scale,
			PacketCodecs.BOOLEAN, BaseGoopData::mature,
			PacketCodecs.indexed(i -> WaterHandling.values()[i], WaterHandling::ordinal), BaseGoopData::waterHandling,
			BaseGoopData::new
	);
	
	public BaseGoopData(int color, float scale, boolean mature, WaterHandling waterHandling)
	{
		this(color, scale, mature, waterHandling, true, true);
	}
}