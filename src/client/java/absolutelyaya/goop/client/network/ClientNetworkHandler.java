package absolutelyaya.goop.client.network;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.client.emitter.EmitterManager;
import absolutelyaya.goop.network.EntityDamagePayload;
import absolutelyaya.goop.network.EntityDeathPayload;
import absolutelyaya.goop.network.EntityLandPayload;
import absolutelyaya.goop.network.ServerConnectionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ClientNetworkHandler
{
	public static void register()
	{
		ClientPlayNetworking.registerGlobalReceiver(ServerConnectionPayload.ID, (payload, context) -> Goop.CLIENT_ONLY = false);
		
		ClientPlayNetworking.registerGlobalReceiver(EntityDamagePayload.ID, (payload, context) -> {
			MinecraftClient client = context.client();
			if(client.world == null)
				return;
			Entity entity = client.world.getEntityById(payload.entityId());
			if(entity instanceof LivingEntity living)
				EmitterManager.onDamage(living, payload.damageType(), payload.amount());
		});
		ClientPlayNetworking.registerGlobalReceiver(EntityDeathPayload.ID, (payload, context) -> {
			MinecraftClient client = context.client();
			if (client.world == null)
				return;
			Entity entity = client.world.getEntityById(payload.entityId());
			if (entity instanceof LivingEntity living)
				EmitterManager.onDeath(living, payload.damageType());
		});
		ClientPlayNetworking.registerGlobalReceiver(EntityLandPayload.ID, (payload, context) -> {
			MinecraftClient client = context.client();
			if (client.world == null)
				return;
			Entity entity = client.world.getEntityById(payload.entityId());
			if (entity instanceof LivingEntity living)
				EmitterManager.onLand(living, payload.fallDistance());
		});
	}
}
