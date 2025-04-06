package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.particle.BaseGoopData;
import absolutelyaya.goop.particle.SplatterParticleEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Optional;

public class DeathEmitter extends AbstractEmitter
{
	public static final MapCodec<DeathEmitter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityTypeReference.CODEC.listOf().optionalFieldOf("targets", List.of()).forGetter(emitter -> emitter.targets),
			BaseGoopData.CODEC.fieldOf("goop").forGetter(emitter -> emitter.goopData),
			DamageTypeReference.CODEC.listOf().optionalFieldOf("damage-types", List.of()).forGetter(emitter -> emitter.damageTypes)
	).apply(instance, DeathEmitter::new));
	List<DamageTypeReference> damageTypes;
	
	public DeathEmitter(List<EntityTypeReference> targets, BaseGoopData goopData, List<DamageTypeReference> damageTypes)
	{
		super(targets, goopData);
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
		for (int i = 0; i < 8; i++)
		{
			Vec3d pos = entity.getPos().add(new Vec3d(0, 0, 0).addRandom(rand, entity.getWidth()).multiply(1, 0, 1)
													.add(0, rand.nextFloat() * entity.getHeight(), 0));
			entity.getWorld().addParticleClient(new SplatterParticleEffect(goopData, Optional.empty()),
					pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
		}
	}
}
