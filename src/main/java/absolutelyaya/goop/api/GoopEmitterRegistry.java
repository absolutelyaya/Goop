package absolutelyaya.goop.api;

import absolutelyaya.goop.Goop;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class GoopEmitterRegistry
{
	static final Map<EntityType<? extends LivingEntity>, List<DamageGoopEmitter<? extends LivingEntity>>> DamageGoopEmitters = new HashMap<>();
	static final Map<EntityType<? extends LivingEntity>, List<DeathGoopEmitter<? extends LivingEntity>>> DeathGoopEmitters = new HashMap<>();
	static final Map<EntityType<? extends LivingEntity>, List<LandingGoopEmitter<? extends LivingEntity>>> LandingGoopEmitters = new HashMap<>();
	static boolean frozen = false;
	
	/**
	 * Registers a new Goop Emitter. Please only register new Emitters in the "goop" Entrypoint!
	 * @param entityType Entity to register as an Emitter
	 * @param emitter The Emitter
	 * @see absolutelyaya.goop.api.DamageGoopEmitter
	 * @see absolutelyaya.goop.api.LandingGoopEmitter
	 */
	public static void register(EntityType<? extends LivingEntity> entityType, IGoopEmitter emitter)
	{
		if(frozen)
		{
			Goop.LogWarning("Tried to register a new Goop Emitter after Registry was frozen. Please only register Goop Emitters via the \"goop\" Entrypoint!");
			return;
		}
		if(emitter instanceof DamageGoopEmitter<?> damageEmitter)
			registerInternal(DamageGoopEmitters, entityType, damageEmitter);
		else if (emitter instanceof LandingGoopEmitter<?> landingEmitter)
			registerInternal(LandingGoopEmitters, entityType, landingEmitter);
		else if(emitter instanceof DeathGoopEmitter<?> deathEmitter)
			registerInternal(DeathGoopEmitters, entityType, deathEmitter);
	}
	
	private static <T extends IGoopEmitter> void registerInternal(Map<EntityType<? extends LivingEntity>, List<T>> map,
								 EntityType<? extends LivingEntity> entityType,
								 T emitter)
	{
		if(!map.containsKey(entityType))
			map.put(entityType, new ArrayList<>());
		map.get(entityType).add(emitter);
		Goop.LogInfo(String.format("Registered new Goop Emitter for EntityType '%s'", entityType));
	}
	
	@ApiStatus.Internal
	public static Optional<List<DamageGoopEmitter<?>>> getDamageEmitters(EntityType<? extends LivingEntity> entityType)
	{
		if(!DamageGoopEmitters.containsKey(entityType))
			return Optional.empty();
		return Optional.of(DamageGoopEmitters.get(entityType));
	}
	
	@ApiStatus.Internal
	public static Optional<List<DeathGoopEmitter<?>>> getDeathEmitters(EntityType<? extends LivingEntity> entityType)
	{
		if(!DeathGoopEmitters.containsKey(entityType))
			return Optional.empty();
		return Optional.of(DeathGoopEmitters.get(entityType));
	}
	
	@ApiStatus.Internal
	public static Optional<List<LandingGoopEmitter<?>>> getLandingEmitters(EntityType<? extends LivingEntity> entityType)
	{
		if(!LandingGoopEmitters.containsKey(entityType))
			return Optional.empty();
		return Optional.of(LandingGoopEmitters.get(entityType));
	}
	
	@ApiStatus.Internal
	public static void freeze()
	{
		frozen = true;
	}
}
