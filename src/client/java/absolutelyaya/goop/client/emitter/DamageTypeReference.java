package absolutelyaya.goop.client.emitter;

import com.mojang.serialization.Codec;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Objects;

public interface DamageTypeReference
{
	Codec<DamageTypeReference> CODEC = Codec.stringResolver(Objects::toString, DamageTypeReference::create);
	
	boolean isOf(RegistryEntry<DamageType> type);
	
	static DamageTypeReference create(String value)
	{
		if(value.startsWith("#"))
		{
			Identifier tag = Identifier.tryParse(value.substring(1));
			if(tag == null)
				return null;
			return new DamageTypeReference.Tag(TagKey.of(RegistryKeys.DAMAGE_TYPE, tag));
		}
		Identifier id = Identifier.tryParse(value);
		if(id == null)
			return null;
		return new DamageTypeReference.Single(id);
	}
	
	record Tag(TagKey<DamageType> tag) implements DamageTypeReference
	{
		@Override
		public boolean isOf(RegistryEntry<DamageType> type)
		{
			return type.isIn(tag);
		}
	}
	
	record Single(Identifier v) implements DamageTypeReference
	{
		@Override
		public boolean isOf(RegistryEntry<DamageType> type)
		{
			return v.equals(Identifier.tryParse(type.getIdAsString()));
		}
	}
}
