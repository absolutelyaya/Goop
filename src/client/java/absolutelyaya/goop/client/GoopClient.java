package absolutelyaya.goop.client;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.client.config.GoopClientConfig;
import absolutelyaya.goop.client.emitter.EmitterManager;
import absolutelyaya.goop.client.network.ClientNetworkHandler;
import absolutelyaya.goop.client.particle.DripParticle;
import absolutelyaya.goop.client.particle.PuddleParticle;
import absolutelyaya.goop.client.particle.SplatterParticle;
import absolutelyaya.goop.client.registries.KeybindRegistry;
import absolutelyaya.goop.data.FinalGoopData;
import absolutelyaya.goop.particle.ParticleEffects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;

import java.util.Optional;

public class GoopClient implements ClientModInitializer
{
	int serverCheckTimer;
	
	@Override
	public void onInitializeClient()
	{
		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(ParticleEffects.SPLATTER, SplatterParticle.Factory::new);
		particleRegistry.register(ParticleEffects.PUDDLE, PuddleParticle.Factory::new);
		particleRegistry.register(ParticleEffects.DRIP, DripParticle.Factory::new);
		
		new GoopClientConfig();
		KeybindRegistry.register();
		ClientNetworkHandler.register();
		
		new EmitterManager();
		
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			PuddleParticle.removeAll(); //it'd be rude to leave a mess behind
			Goop.CLIENT_ONLY = true;
		});
		
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			serverCheckTimer = 100;
		});
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if(serverCheckTimer-- == 0 && Goop.CLIENT_ONLY)
			{
				if(client.player != null)
					client.player.sendMessage(Text.translatable("message.goop.client_only_notice"), false);
			}
		});
		
		Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Goop.MOD_ID);
		ResourceManagerHelper.registerBuiltinResourcePack(Goop.id("everything_bleeds"),
				modContainer.orElseThrow(), Text.translatable("resourcepack.everything_bleeds.name"), ResourcePackActivationType.NORMAL);
		ResourceManagerHelper.registerBuiltinResourcePack(Goop.id("sensible_blood"),
				modContainer.orElseThrow(), Text.translatable("resourcepack.sensible_blood.name"), ResourcePackActivationType.DEFAULT_ENABLED);
		ResourceManagerHelper.registerBuiltinResourcePack(Goop.id("slime"),
				modContainer.orElseThrow(), Text.translatable("resourcepack.slime.name"), ResourcePackActivationType.DEFAULT_ENABLED);
	}
	
	public static int getColorOrCensor(FinalGoopData data)
	{
		return getColorOrCensor(data.color(), data.mature());
	}
	
	public static int getColorOrCensor(int color, boolean mature)
	{
		if(mature && GoopClientConfig.INSTANCE.censor.getValue())
			return GoopClientConfig.INSTANCE.censorColor.getValue();
		return color;
	}
}
