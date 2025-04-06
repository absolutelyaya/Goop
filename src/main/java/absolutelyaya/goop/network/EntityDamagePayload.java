package absolutelyaya.goop.network;

import absolutelyaya.goop.Goop;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record EntityDamagePayload(int entityId, RegistryEntry<DamageType> damageType, float amount) implements CustomPayload
{
	public static Id<EntityDamagePayload> ID = new Id<>(Goop.id("entity_damage"));
	public static PacketCodec<RegistryByteBuf, EntityDamagePayload> CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, EntityDamagePayload::entityId,
			DamageType.ENTRY_PACKET_CODEC, EntityDamagePayload::damageType,
			PacketCodecs.FLOAT, EntityDamagePayload::amount,
			EntityDamagePayload::new
	);
	
	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
