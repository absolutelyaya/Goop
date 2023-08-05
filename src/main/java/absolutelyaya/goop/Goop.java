package absolutelyaya.goop;

import absolutelyaya.goop.particles.GoopDropParticle;
import absolutelyaya.goop.particles.GoopParticle;
import absolutelyaya.goop.particles.GoopStringParticle;
import absolutelyaya.goop.registries.ParticleRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class GoopTechnology implements ModInitializer
{
	public static String MOD_ID = "goop";
	
	@Override
	public void onInitialize()
	{
		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(ParticleRegistry.GOOP_DROP, GoopDropParticle.Factory::new);
		particleRegistry.register(ParticleRegistry.GOOP, GoopParticle.Factory::new);
		particleRegistry.register(ParticleRegistry.GOOP_STRING, GoopStringParticle.Factory::new);
	}
}
