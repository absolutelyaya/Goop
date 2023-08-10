package absolutelyaya.goop.api;

import absolutelyaya.goop.Goop;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * This Class purely exists for devs to extend and use for extra arguments needed for custom Particle Effects based on Goop.
 */
public class ExtraGoopData
{
	public static Codec<ExtraGoopData> getCodec()
	{
		return Codec.EMPTY.codec().comapFlatMap((coordinates) -> null, (data) -> null);
	}
	
	/**
	 * Reads this ExtraGoopData from the given PacketByteBuffer
	 */
	public static ExtraGoopData read(PacketByteBuf buf)
	{
		return null;
	}
	
	/**
	 * When creating a new ExtraGoopData Type, override this with your unique Type Identifier and register it in the GoopEmitterRegistry.<br>
	 * This will be used to get the correct packet reading method.
	 * @see absolutelyaya.goop.api.GoopEmitterRegistry#registerExtraDataType(Identifier, Class)
	 */
	public static Identifier getExtraDataType()
	{
		return new Identifier(Goop.MOD_ID, "default");
	}
	
	/**
	 * Writes this ExtraGoopData to the given PacketByteBuffer.
	 */
	public void write(PacketByteBuf buf)
	{
		buf.writeIdentifier(getExtraDataType());
	}
}
