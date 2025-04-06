package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.data.FinalGoopData;
import absolutelyaya.goop.data.ModularGoopData;
import absolutelyaya.goop.particle.SplatterParticleEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DamageEmitter extends AbstractEmitter
{
	public static final MapCodec<DamageEmitter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityTypeReference.CODEC.listOf().optionalFieldOf("targets", List.of()).forGetter(emitter -> emitter.targets),
			ModularGoopData.CODEC.fieldOf("goop").forGetter(emitter -> emitter.goopData),
			DamageTypeReference.CODEC.listOf().optionalFieldOf("damage-types", List.of()).forGetter(emitter -> emitter.damageTypes)
	).apply(instance, DamageEmitter::new));
	
	public final List<DamageTypeReference> damageTypes;
	
	public DamageEmitter(List<EntityTypeReference> targets, ModularGoopData goopData, List<DamageTypeReference> damageTypes)
	{
		super(targets, goopData);
		this.damageTypes = damageTypes;
	}
	
	@Override
	EmitterType getType()
	{
		return EmitterType.DAMAGE;
	}
	
	public void emit(LivingEntity entity, float amount)
	{
		Random rand = entity.getRandom();
		
		for (int i = 0; i < Math.min(amount, 32); i++)
		{
			Vec3d pos = entity.getPos().add(new Vec3d(0, 0, 0).addRandom(rand, entity.getWidth()).multiply(1, 0, 1)
													.add(0, rand.nextFloat() * entity.getHeight(), 0));
			entity.getWorld().addParticleClient(new SplatterParticleEffect(goopData.calculate(Map.of("damage", amount)), Optional.empty()),
					pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
		}
	}
}
