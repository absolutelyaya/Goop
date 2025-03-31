package absolutelyaya.goop.client.particle;

import absolutelyaya.goop.particle.BaseGoopData;
import absolutelyaya.goop.particle.PuddleParticleEffect;
import absolutelyaya.goop.particle.WaterHandling;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
	
	protected PuddleParticle(ClientWorld clientWorld, Vec3d pos, SpriteProvider spriteProvider, BaseGoopData data, Direction up)
	{
		super(clientWorld, pos, spriteProvider, data, up);
		alpha = Math.min(random.nextFloat() + 0.5f, 1);
		baseAlpha = 1f;
		scale = 0f;
		appearTicks = random.nextInt(4) + 3;
		GOOP_QUEUE.add(this);
		if(GOOP_QUEUE.size() > /*config.goopCap*/ 420) //TODO: add config
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
		
		//scale / alpha animations
		if(age <= appearTicks)
			scale = MathHelper.clampedLerp(0f, data.scale(), ((float)age / appearTicks));
		else if(age >= maxAge - 60 /*&& !config.permanent*/)
		{
			scale = MathHelper.clampedLerp(data.scale(), data.scale() * 0.5f, (age - (maxAge - 60)) / 60f);
			alpha = MathHelper.clampedLerp(baseAlpha, 0f, (age - (maxAge - 60)) / 60f);
		}
		else
		{
			//Rain cleaning
			if(/*GoopClient.getConfig().rainCleaning &&*/ data.waterHandling() != WaterHandling.IGNORE &&
					   world.isRaining() && world.isSkyVisible(new BlockPos((int)x, (int)y, (int)z)))
			{
				rain = rain + 1f / 100f;
				scale = MathHelper.clampedLerp(data.scale(), data.scale() * 1.25f, rain / 10f);
				alpha = MathHelper.clampedLerp(baseAlpha, 0f, rain / 10f);
				if(rain > 10f)
					markDead();
			}
		}
		//TODO: Ceiling Drips
		//if(data.drip() && up.equals(Direction.DOWN) && random.nextInt(120) == 0)
		//	world.addParticleClient(new GoopStringParticleEffect(color, 0.25f, mature),
		//			x + random.nextFloat() * scale - scale / 2f, y, z + random.nextFloat() * scale - scale / 2f,
		//			0, 0, 0);
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
					world.addParticleClient(new DustParticleEffect(data.color(), scale), x, y, z,
							random.nextFloat() * 0.1f, random.nextFloat() * 0.1f, random.nextFloat() * 0.1f);
					markDead();
				}
			}
		}
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
