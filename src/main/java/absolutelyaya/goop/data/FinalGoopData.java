package absolutelyaya.goop.data;

import absolutelyaya.goop.particle.WaterHandling;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record FinalGoopData(int color, float scale, boolean mature, WaterHandling waterHandling, boolean drip, boolean deform)
{
	public static final MapCodec<FinalGoopData> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codecs.ARGB.fieldOf("color").forGetter(FinalGoopData::color),
					Codecs.POSITIVE_FLOAT.fieldOf("scale").forGetter(FinalGoopData::scale),
					Codec.BOOL.optionalFieldOf("mature", false).forGetter(FinalGoopData::mature),
					WaterHandling.CODEC.optionalFieldOf("waterhandling", WaterHandling.REPLACE_WITH_CLOUD_PARTICLE).forGetter(FinalGoopData::waterHandling),
					Codec.BOOL.optionalFieldOf("drip", true).forGetter(FinalGoopData::drip),
					Codec.BOOL.optionalFieldOf("deform", true).forGetter(FinalGoopData::deform)
			).apply(instance, FinalGoopData::new)
	);
	public static final PacketCodec<ByteBuf, FinalGoopData> PACKET_CODEC = PacketCodecs.codec(CODEC.codec());
}