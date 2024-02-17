package absolutelyaya.goop.registries;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.api.ExtraGoopData;
import absolutelyaya.goop.api.GoopEmitterRegistry;
import absolutelyaya.goop.api.WaterHandling;
import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.particles.GoopDropParticleEffect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.InvocationTargetException;

public class PacketRegistry
{
	public static final Identifier EMIT_GOOP_PACKET_ID = new Identifier(Goop.MOD_ID, "emit_goop");
	
	public static void registerClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.EMIT_GOOP_PACKET_ID, ((client, handler, buf, sender) -> {
			if(buf.readBoolean() && !GoopClient.getConfig().showDev)
				return;
			Vec3d pos = new Vec3d(buf.readVector3f());
			int color = buf.readInt();
			Vec3d baseVel = new Vec3d(buf.readVector3f());
			float randomness = buf.readFloat();
			int amount = buf.readInt();
			float size = buf.readFloat();
			boolean mature = buf.readBoolean(), drip = buf.readBoolean(), deform = buf.readBoolean();
			WaterHandling waterHandling = buf.readEnumConstant(WaterHandling.class);
			
			if(mature && GoopClient.hideMature())
				return;
			
			boolean isOverridden = buf.readBoolean();
			Identifier effectOverride = isOverridden ? buf.readIdentifier() : null;
			ExtraGoopData data;
			if(isOverridden)
			{
				try
				{
					data = (ExtraGoopData)GoopEmitterRegistry.getExtraDataType(buf.readIdentifier()).getMethod("read", PacketByteBuf.class).invoke(null, buf);
				}
				catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
				{
					throw new RuntimeException(e);
				}
			}
			else
				data = new ExtraGoopData();
			
			MinecraftClient.getInstance().execute(() -> {
				if(client.world == null)
					return;
				for (int i = 0; i < amount; i++)
				{
					Vec3d vel = baseVel.addRandom(client.world.random, randomness);
					if(isOverridden)
						client.world.addParticle(new GoopDropParticleEffect(Vec3d.unpackRgb(color), size, mature, waterHandling, effectOverride, data).setDrip(drip).setDeform(deform),
								pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
					else
						client.world.addParticle(new GoopDropParticleEffect(Vec3d.unpackRgb(color), size, mature, waterHandling).setDrip(drip).setDeform(deform),
								pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
				}
			});
		}));
	}
}
