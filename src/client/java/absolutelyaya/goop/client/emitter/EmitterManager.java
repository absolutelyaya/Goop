package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.Goop;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmitterManager extends SinglePreparationResourceReloader<Map<Identifier, AbstractEmitter>> implements IdentifiableResourceReloadListener
{
	private final DynamicOps<JsonElement> ops;
	private final ResourceFinder finder;
	
	static Map<Identifier, AbstractEmitter> emittersById = Map.of();
	static Multimap<EmitterType, AbstractEmitter> emittersByType = ArrayListMultimap.create();
	
	public EmitterManager()
	{
		super();
		ops = JsonOps.INSTANCE;
		finder = ResourceFinder.json("goop_emitters");
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
	}
	
	@Override
	protected Map<Identifier, AbstractEmitter> prepare(ResourceManager manager, Profiler profiler)
	{
		ImmutableMap.Builder<Identifier, AbstractEmitter> map = ImmutableMap.builder();
		
		for (Map.Entry<Identifier, Resource> entry : finder.findResources(manager).entrySet())
		{
			Identifier key = entry.getKey();
			Identifier resourceId = finder.toResourceId(key);
			
			try (Reader reader = entry.getValue().getReader())
			{
				EmitterType.CODEC.parse(ops, JsonParser.parseReader(reader))
						.ifSuccess((value) -> map.put(resourceId, value))
						.ifError((error) -> Goop.LOGGER.error("Couldn't parse data file '{}' from '{}': {}",
								resourceId, key, error));
			}
			catch (IllegalArgumentException | IOException | JsonParseException e)
			{
				Goop.LOGGER.error("Couldn't parse data file '{}' from '{}'", resourceId, key, e);
			}
		}
		return map.build();
	}
	
	@Override
	protected void apply(Map<Identifier, AbstractEmitter> prepared, ResourceManager manager, Profiler profiler)
	{
		
		emittersById = prepared;
		emittersByType = ArrayListMultimap.create();
		for (AbstractEmitter emitter : prepared.values())
			emittersByType.put(emitter.getType(), emitter);
	}
	
	@Override
	public Identifier getFabricId()
	{
		return Goop.id("goop_emitters");
	}
	
	public static void onDamage(LivingEntity entity, RegistryEntry<DamageType> damageType, float amount)
	{
		for (AbstractEmitter i : emittersByType.get(EmitterType.DAMAGE))
		{
			if(!matchesAnyEntityType(i.targets, Registries.ENTITY_TYPE.getId(entity.getType())))
				continue;
			if(!(i instanceof DamageEmitter emitter))
				continue;
			Optional<RegistryKey<DamageType>> damageTypeKey = damageType.getKey();
			if((damageTypeKey.isPresent() && damageTypeKey.get().equals(DamageTypes.GENERIC_KILL)) || !matchesAnyDamageType(emitter.damageTypes, damageType))
				continue;
			emitter.emit(entity, amount);
		}
	}
	
	public static void onDeath(LivingEntity entity, RegistryEntry<DamageType> damageType)
	{
		for (AbstractEmitter i : emittersByType.get(EmitterType.DEATH))
		{
			if(!matchesAnyEntityType(i.targets, Registries.ENTITY_TYPE.getId(entity.getType())))
				continue;
			if(!(i instanceof DeathEmitter emitter))
				continue;
			if(!matchesAnyDamageType(emitter.damageTypes, damageType))
				continue;
			emitter.emit(entity);
		}
	}
	
	public static void onLand(LivingEntity entity, float fallHeight)
	{
		for (AbstractEmitter i : emittersByType.get(EmitterType.LANDING))
		{
			if(!matchesAnyEntityType(i.targets, Registries.ENTITY_TYPE.getId(entity.getType())))
				continue;
			if(!(i instanceof LandingEmitter emitter))
				continue;
			emitter.emit(entity, fallHeight);
		}
	}
	
	private static boolean matchesAnyEntityType(List<EntityTypeReference> list, Identifier id)
	{
		for (EntityTypeReference ref : list)
		{
			if(ref.isOf(id))
				return true;
		}
		return list.isEmpty();
	}
	
	private static boolean matchesAnyDamageType(List<DamageTypeReference> list, RegistryEntry<DamageType> entry)
	{
		for (DamageTypeReference ref : list)
		{
			if(ref.isOf(entry))
				return true;
		}
		return list.isEmpty();
	}
}
