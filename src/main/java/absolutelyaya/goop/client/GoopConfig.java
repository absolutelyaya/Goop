package absolutelyaya.goop.client;

import absolutelyaya.goop.Goop;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Goop.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/light_blue_terracotta.png")
public class GoopConfig implements ConfigData
{
	@ConfigEntry.Gui.Tooltip
	public boolean showDev = false;
	@ConfigEntry.Gui.Tooltip
	public boolean fancyGoop = false;
	@ConfigEntry.Gui.Tooltip
	public boolean wrapToEdges = false;
	@ConfigEntry.Gui.Tooltip
	public boolean censorMature = false;
	@ConfigEntry.ColorPicker
	public int censorColor = 0xff0fc3;
	@ConfigEntry.Gui.Tooltip
	public boolean permanent = false;
	@ConfigEntry.Gui.Tooltip
	public boolean rainCleaning = true;
	@ConfigEntry.Gui.Tooltip
	public int goopCap = 320;
	@ConfigEntry.Category("debug")
	@ConfigEntry.Gui.Tooltip
	public boolean goopDebug = false;
}
