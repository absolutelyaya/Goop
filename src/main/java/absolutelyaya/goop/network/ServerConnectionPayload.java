package absolutelyaya.goop.network;

import absolutelyaya.goop.Goop;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ServerConnectionPayload() implements CustomPayload
{
	public static CustomPayload.Id<ServerConnectionPayload> ID = new CustomPayload.Id<>(Goop.id("connect"));
	public static PacketCodec<RegistryByteBuf, ServerConnectionPayload> CODEC = PacketCodec.unit(new ServerConnectionPayload());
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
