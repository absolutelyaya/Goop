package absolutelyaya.goop.network;

import absolutelyaya.goop.Goop;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record EntityDeathPayload(int entityId, RegistryEntry<DamageType> damageType) implements CustomPayload
{
	public static Id<EntityDeathPayload> ID = new Id<>(Goop.id("entity_death"));
	public static PacketCodec<RegistryByteBuf, EntityDeathPayload> CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, EntityDeathPayload::entityId,
			DamageType.ENTRY_PACKET_CODEC, EntityDeathPayload::damageType,
			EntityDeathPayload::new
	);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
