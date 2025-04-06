package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.data.ModularGoopData;
import absolutelyaya.goop.particle.PuddleParticleEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.Map;

public class LandingEmitter extends AbstractEmitter
{
	public static final MapCodec<LandingEmitter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityTypeReference.CODEC.listOf().optionalFieldOf("targets", List.of()).forGetter(emitter -> emitter.targets),
			ModularGoopData.CODEC.fieldOf("goop").forGetter(emitter -> emitter.goopData)
	).apply(instance, LandingEmitter::new));
	
	public LandingEmitter(List<EntityTypeReference> targets, ModularGoopData goopData)
	{
		super(targets, goopData);
	}
	
	@Override
	EmitterType getType()
	{
		return EmitterType.LANDING;
	}
	
	public void emit(LivingEntity entity, float amount)
	{
		HitResult hit = entity.getWorld().raycast(new RaycastContext(entity.getPos(), entity.getPos().add(0, -1, 0),
				RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
		Vec3d hitPos = hit.getPos();
		entity.getWorld().addParticleClient(new PuddleParticleEffect(goopData.calculate(Map.of("fallDist",amount)), Direction.UP),
				hitPos.getX(), hitPos.getY(), hitPos.getZ(), 0, 0, 0);
	}
}
