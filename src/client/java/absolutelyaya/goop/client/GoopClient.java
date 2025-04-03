package absolutelyaya.goop.client;

import absolutelyaya.goop.client.config.GoopClientConfig;
import absolutelyaya.goop.client.particle.DripParticle;
import absolutelyaya.goop.client.particle.PuddleParticle;
import absolutelyaya.goop.client.particle.SplatterParticle;
import absolutelyaya.goop.client.registries.KeybindRegistry;
import absolutelyaya.goop.particle.BaseGoopData;
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
		
		new GoopClientConfig();
		KeybindRegistry.register();
	}
	
	public static int getColorOrCensor(BaseGoopData data)
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
