package absolutelyaya.goop.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PacketRegistry
{
	public static void register()
	{
		PayloadTypeRegistry.playS2C().register(ServerConnectionPayload.ID, ServerConnectionPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(EntityDamagePayload.ID, EntityDamagePayload.CODEC);
		PayloadTypeRegistry.playS2C().register(EntityDeathPayload.ID, EntityDeathPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(EntityLandPayload.ID, EntityLandPayload.CODEC);
	}
}
