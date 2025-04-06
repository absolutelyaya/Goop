package absolutelyaya.goop.network;

import absolutelyaya.goop.Goop;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EntityLandPayload(int entityId, float fallDistance) implements CustomPayload
{
	public static Id<EntityLandPayload> ID = new Id<>(Goop.id("entity_land"));
	public static PacketCodec<RegistryByteBuf, EntityLandPayload> CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, EntityLandPayload::entityId,
			PacketCodecs.FLOAT, EntityLandPayload::fallDistance,
			EntityLandPayload::new
	);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
