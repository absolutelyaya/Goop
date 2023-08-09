package absolutelyaya.goop.client;

import absolutelyaya.goop.particles.GoopDropParticle;
import absolutelyaya.goop.particles.GoopParticle;
import absolutelyaya.goop.particles.GoopStringParticle;
import absolutelyaya.goop.registries.PacketRegistry;
import absolutelyaya.goop.registries.ParticleRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class GoopClient implements ClientModInitializer
{
	static ConfigHolder<GoopConfig> config;
	
	public static GoopConfig getConfig()
	{
		return config.getConfig();
	}
	
	@Override
	public void onInitializeClient()
	{
		config = AutoConfig.register(GoopConfig.class, GsonConfigSerializer::new);
		
		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(ParticleRegistry.GOOP_DROP, GoopDropParticle.Factory::new);
		particleRegistry.register(ParticleRegistry.GOOP, GoopParticle.Factory::new);
		particleRegistry.register(ParticleRegistry.GOOP_STRING, GoopStringParticle.Factory::new);
		
		PacketRegistry.registerClient();
	}
}
