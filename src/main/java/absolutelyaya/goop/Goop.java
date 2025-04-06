package absolutelyaya.goop;

import absolutelyaya.goop.network.PacketRegistry;
import absolutelyaya.goop.network.ServerConnectionPayload;
import absolutelyaya.goop.particle.ParticleEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Goop implements ModInitializer
{
	public static final String MOD_ID = "goop";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	public static boolean CLIENT_ONLY = true;
	
	@Override
	public void onInitialize()
	{
		ParticleEffects.register();
		PacketRegistry.register();
		
		ServerPlayConnectionEvents.JOIN.register((handler, packetSender, minecraftServer) -> ServerPlayNetworking.send(handler.getPlayer(), new ServerConnectionPayload()));
	}
	
	public static Identifier id(String path)
	{
		if(path.contains(":"))
			return Identifier.tryParse(path);
		return Identifier.of(MOD_ID, path);
	}
}
