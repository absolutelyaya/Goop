package absolutelyaya.goop.client;

import absolutelyaya.goop.client.config.GoopClientConfig;
import absolutelyaya.yayconfig.gui.screen.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<ConfigScreen> getModConfigScreenFactory()
	{
		return i -> new ConfigScreen(GoopClientConfig.INSTANCE, i);
	}
}
