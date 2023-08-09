package absolutelyaya.goop.registries;

import absolutelyaya.goop.Goop;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TagRegistry
{
	public static final TagKey<DamageType> PHYSICAL = TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Goop.MOD_ID, "physical"));
	
	public static void register()
	{
	
	}
}
