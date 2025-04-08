package absolutelyaya.goop.client.particle;

import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.client.config.GoopClientConfig;
import absolutelyaya.goop.data.FinalGoopData;
import absolutelyaya.goop.particle.DripParticleEffect;
import absolutelyaya.goop.particle.PuddleParticleEffect;
import absolutelyaya.goop.particle.WaterHandling;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class PuddleParticle extends SurfaceAlignedParticle
{
	static final Queue<PuddleParticle> GOOP_QUEUE = new ArrayDeque<>();
	
	private final float baseAlpha;
	private final int appearTicks;
	float rain;
	float lastScale, curScale;
	
	protected PuddleParticle(ClientWorld clientWorld, Vec3d pos, SpriteProvider spriteProvider, FinalGoopData data, Direction up)
	{
		super(clientWorld, pos, spriteProvider, data, up);
		baseAlpha = alpha;
		scale = 0f;
		appearTicks = random.nextInt(2) + 3;
		GOOP_QUEUE.add(this);
		if(GOOP_QUEUE.size() > GoopClientConfig.INSTANCE.goopCap.getValue())
			GOOP_QUEUE.remove().markDead();
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		lastScale = curScale;
		//scale / alpha animations
		if(age <= appearTicks)
			curScale = MathHelper.clampedLerp(0f, data.scale(), ((float)age / appearTicks));
		else if(age >= maxAge - 60 && !GoopClientConfig.INSTANCE.permanent.getValue())
		{
			curScale = MathHelper.clampedLerp(data.scale(), data.scale() * 0.5f, (age - (maxAge - 60)) / 60f);
			alpha = MathHelper.clampedLerp(baseAlpha, 0f, (age - (maxAge - 60)) / 60f);
		}
		else
		{
			//Rain cleaning
			if(GoopClientConfig.INSTANCE.rainCleaning.getValue() && data.waterHandling() != WaterHandling.IGNORE &&
					   world.isRaining() && world.isSkyVisible(new BlockPos((int)x, (int)y, (int)z)))
			{
				rain = rain + 1f / 100f;
				curScale = MathHelper.clampedLerp(data.scale(), data.scale() * 1.25f, rain / 10f);
				alpha = MathHelper.clampedLerp(baseAlpha, 0f, rain / 10f);
				if(rain > 10f)
					markDead();
			}
		}
		if(data.drip() && up.equals(Direction.DOWN) && random.nextInt(120) == 0)
		{
			Vec3d pos = new Vec3d(x + random.nextFloat() * scale - scale / 2f, y, z + random.nextFloat() * scale - scale / 2f);
			HitResult hitUp = world.raycast(new RaycastContext(pos, pos.add(0f, 0.05f, 0f),
					RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
			HitResult hitDown = world.raycast(new RaycastContext(pos, pos.add(0f, -0.05f, 0f),
					RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
			if(!hitUp.getType().equals(HitResult.Type.MISS) && hitDown.getType().equals(HitResult.Type.MISS))
				world.addParticle(new DripParticleEffect(GoopClient.getColorOrCensor(data), 0.25f, data.mature()),
						pos.x, pos.y, pos.z, 0, 0, 0);
		}
		//Fluid handling
		if(world.getFluidState(new BlockPos((int)x, (int)y, (int)z)).isIn(FluidTags.LAVA))
			markDead();
		if(data.waterHandling() == WaterHandling.IGNORE)
			return;
		if(world.isWater(new BlockPos((int)x, (int)y, (int)z)))
		{
			switch(data.waterHandling())
			{
				case REMOVE_PARTICLE -> markDead();
				case REPLACE_WITH_CLOUD_PARTICLE ->
				{
					world.addParticle(new DustParticleEffect(GoopClient.getColorOrCensorVec(data), scale), x, y, z,
							random.nextFloat() * 0.1f, random.nextFloat() * 0.1f, random.nextFloat() * 0.1f);
					markDead();
				}
			}
		}
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float delta)
	{
		scale = MathHelper.clampedLerp(lastScale, curScale, delta);
		super.buildGeometry(vertexConsumer, camera, delta);
	}
	
	@Override
	public void markDead()
	{
		super.markDead();
		GOOP_QUEUE.remove(this);
	}
	
	public static void removeAll()
	{
		new ArrayList<>(GOOP_QUEUE).forEach(PuddleParticle::markDead);
	}
	
	public static class Factory implements ParticleFactory<PuddleParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(PuddleParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new PuddleParticle(world, new Vec3d(x, y, z), spriteProvider, parameters.data(), parameters.up());
		}
	}
}
