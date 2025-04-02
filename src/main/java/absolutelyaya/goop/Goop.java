package absolutelyaya.goop;

import absolutelyaya.goop.particle.ParticleEffects;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Goop implements ModInitializer
{
	public static final String MOD_ID = "goop";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize()
	{
		ParticleEffects.register();
	}
	
	public static Identifier id(String path)
	{
		if(path.contains(":"))
			return Identifier.tryParse(path);
		return Identifier.of(MOD_ID, path);
	}
}
