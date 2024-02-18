package absolutelyaya.goop.registries;

import absolutelyaya.goop.particles.GoopParticle;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeybindRegistry
{
	public static final KeyBinding CLEAR_GOOP = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.goop.clear", InputUtil.Type.KEYSYM,
					InputUtil.UNKNOWN_KEY.getCode(), "category.goop"));
	
	static boolean clearPressed;
	
	public static void register()
	{
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			while (CLEAR_GOOP.wasPressed() && !clearPressed)
			{
				GoopParticle.removeAll();
				clearPressed = true;
			}
			while (CLEAR_GOOP.wasPressed()); //remove stored presses
			clearPressed = CLEAR_GOOP.isPressed();
		});
	}
}
