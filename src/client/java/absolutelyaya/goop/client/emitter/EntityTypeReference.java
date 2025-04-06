package absolutelyaya.goop.client.emitter;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface EntityTypeReference
{
	Codec<EntityTypeReference> CODEC = Codec.stringResolver(Object::toString, EntityTypeReference::create);
	
	boolean isOf(EntityType<?> type);
	
	@SuppressWarnings("ConstantValue")
	default boolean isOf(Identifier id)
	{
		EntityType<?> type = Registries.ENTITY_TYPE.get(id);
		return type != null && isOf(type);
	}
	
	@SuppressWarnings("ConstantValue")
	static EntityTypeReference create(String value)
	{
		if(value.startsWith("#"))
		{
			Identifier tag = Identifier.tryParse(value.substring(1));
			if(tag == null)
				return null;
			return new Tag(TagKey.of(RegistryKeys.ENTITY_TYPE, tag));
		}
		Identifier id = Identifier.tryParse(value);
		if(id == null)
			return null;
		EntityType<?> type = Registries.ENTITY_TYPE.get(id);
		if(type == null)
			return null;
		return new Single(type);
	}
	
	record Tag(TagKey<EntityType<?>> tag) implements EntityTypeReference
	{
		@Override
		public boolean isOf(EntityType<?> type)
		{
			return type.isIn(tag);
		}
		
		@Override
		public String toString()
		{
			return "#" + tag.id();
		}
	}
	
	record Single(EntityType<?> type) implements EntityTypeReference
	{
		@Override
		public boolean isOf(EntityType<?> type)
		{
			return this.type.equals(type);
		}
		
		@Override
		public String toString()
		{
			return Registries.ENTITY_TYPE.getId(type).toString();
		}
	}
}
