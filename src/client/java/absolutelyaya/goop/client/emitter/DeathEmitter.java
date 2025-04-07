package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.data.Calculatable;
import absolutelyaya.goop.data.ModularGoopData;
import absolutelyaya.goop.particle.PuddleParticleEffect;
import absolutelyaya.goop.particle.SplatterParticleEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DeathEmitter extends AbstractEmitter
{
	public static final MapCodec<DeathEmitter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityTypeReference.CODEC.listOf().optionalFieldOf("targets", List.of()).forGetter(emitter -> emitter.targets),
			ModularGoopData.CODEC.fieldOf("goop").forGetter(emitter -> emitter.goopData),
			DamageTypeReference.CODEC.listOf().optionalFieldOf("damage-types", List.of()).forGetter(emitter -> emitter.damageTypes),
			Calculatable.CODEC.optionalFieldOf("count", new Calculatable("rand(4, 8)")).forGetter(emitter -> emitter.count),
			Calculatable.CODEC.optionalFieldOf("speed", new Calculatable("0")).forGetter(emitter -> emitter.speed)
	).apply(instance, DeathEmitter::new));
	List<DamageTypeReference> damageTypes;
	
	public DeathEmitter(List<EntityTypeReference> targets, ModularGoopData goopData, List<DamageTypeReference> damageTypes, Calculatable count, Calculatable speed)
	{
		super(targets, goopData, count, speed);
		this.damageTypes = damageTypes;
	}
	
	@Override
	EmitterType getType()
	{
		return EmitterType.DEATH;
	}
	
	public void emit(LivingEntity entity)
	{
		Random rand = entity.getRandom();
		Map<String, Float> vars = Map.of();
		float ccount = count.calculate(vars);
		if(ccount < 1)
		{
			HitResult hit = entity.getWorld().raycast(new RaycastContext(entity.getPos(), entity.getPos().add(0, -1, 0),
					RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
			Vec3d hitPos = hit.getPos();
			entity.getWorld().addParticleClient(new PuddleParticleEffect(goopData.calculate(vars), Direction.UP),
					hitPos.getX(), hitPos.getY(), hitPos.getZ(), 0, 0, 0);
			return;
		}
		for (int i = 0; i < count.calculate(vars); i++)
		{
			Vec3d pos = entity.getPos().add(new Vec3d(0, 0, 0).addRandom(rand, entity.getWidth()).multiply(1, 0, 1)
													.add(0, rand.nextFloat() * entity.getHeight(), 0));
			Vec3d vel = new Vec3d(0, 0, 0).addRandom(rand, speed.calculate(vars));
			entity.getWorld().addParticleClient(new SplatterParticleEffect(goopData.calculate(vars), Optional.empty()),
					pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
		}
	}
}
