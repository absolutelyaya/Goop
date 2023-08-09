package absolutelyaya.goop;

import absolutelyaya.goop.api.GoopEmitterRegistry;
import absolutelyaya.goop.api.GoopInitializer;
import absolutelyaya.goop.registries.TagRegistry;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public class Goop implements ModInitializer
{
	public static final String MOD_ID = "goop";
	public static final Logger LOGGER = LogUtils.getLogger();
	
	@Override
	public void onInitialize()
	{
		TagRegistry.register();
		for (GoopInitializer initializer : FabricLoader.getInstance().getEntrypoints("goop", GoopInitializer.class))
			initializer.registerGoopEmitters();
		GoopEmitterRegistry.freeze();
	}
	
	public static void LogInfo(String warning)
	{
		LOGGER.info(warning);
	}
	
	public static void LogWarning(String warning)
	{
		LOGGER.warn(warning);
	}
}
