package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.Goop;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;

public class EmitterManager extends JsonDataLoader<AbstractEmitter> implements SimpleSynchronousResourceReloadListener
{
	static Map<Identifier, AbstractEmitter> emittersById = Map.of();
	static Multimap<EmitterType, AbstractEmitter> emittersByType = ArrayListMultimap.create();
	
	public EmitterManager()
	{
		super(EmitterType.CODEC, ResourceFinder.json("goop_emitters"));
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
	}
	
	@Override
	public Identifier getFabricId()
	{
		return Goop.id("goop_emitters");
	}
	
	@Override
	public void reload(ResourceManager manager)
	{
		apply(prepare(manager, null), manager, null);
	}
	
	@Override
	protected void apply(Map<Identifier, AbstractEmitter> prepared, ResourceManager manager, Profiler profiler)
	{
		emittersById = prepared;
		emittersByType = ArrayListMultimap.create();
		for (AbstractEmitter emitter : prepared.values())
			emittersByType.put(emitter.getType(), emitter);
	}
	
	public static void onDamage(LivingEntity entity, RegistryEntry<DamageType> damageType, float amount)
	{
		for (AbstractEmitter i : emittersByType.get(EmitterType.DAMAGE))
		{
			if(!matchesAnyEntityType(i.targets, Registries.ENTITY_TYPE.getId(entity.getType())))
				continue;
			if(!(i instanceof DamageEmitter emitter))
				continue;
			if(damageType.equals(DamageTypes.GENERIC_KILL) || !matchesAnyDamageType(emitter.damageTypes, damageType))
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
