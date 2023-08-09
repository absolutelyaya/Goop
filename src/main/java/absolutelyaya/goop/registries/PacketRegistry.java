package absolutelyaya.goop.registries;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.particles.GoopDropParticleEffect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PacketRegistry
{
	public static final Identifier EMIT_GOOP_PACKET_ID = new Identifier(Goop.MOD_ID, "emit_goop");
	
	public static void registerClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.EMIT_GOOP_PACKET_ID, ((client, handler, buf, sender) -> {
			Vec3d pos = new Vec3d(buf.readVector3f());
			int color = buf.readInt();
			Vec3d baseVel = new Vec3d(buf.readVector3f());
			float randomness = buf.readFloat();
			int amount = buf.readInt();
			float size = buf.readFloat();
			boolean mature = buf.readBoolean();
			MinecraftClient.getInstance().execute(() -> {
				if(client.world == null)
					return;
				for (int i = 0; i < amount; i++)
				{
					Vec3d vel = baseVel.addRandom(client.world.random, randomness);
					client.world.addParticle(new GoopDropParticleEffect(Vec3d.unpackRgb(color), size, mature), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
				}
			});
		}));
	}
}
