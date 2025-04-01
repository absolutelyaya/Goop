package absolutelyaya.goop.client;

import absolutelyaya.goop.client.particle.DripParticle;
import absolutelyaya.goop.client.particle.PuddleParticle;
import absolutelyaya.goop.client.particle.SplatterParticle;
import absolutelyaya.goop.particle.ParticleEffects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class GoopClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(ParticleEffects.SPLATTER, SplatterParticle.Factory::new);
		particleRegistry.register(ParticleEffects.PUDDLE, PuddleParticle.Factory::new);
		particleRegistry.register(ParticleEffects.DRIP, DripParticle.Factory::new);
	}
}
