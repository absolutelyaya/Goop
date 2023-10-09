package absolutelyaya.goop.particles;

import absolutelyaya.goop.api.WaterHandling;
import absolutelyaya.goop.client.GoopClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

public class GoopParticle extends SurfaceAlignedParticle
{
	static Queue<GoopParticle> GOOP_QUEUE = new ArrayDeque<>();
	
	private final Vec3d color;
	private final float size;
	private final float normalAlpha;
	private final int appearTicks;
	final boolean mature, drip;
	final WaterHandling waterHandling;
	float rain;
	
	protected GoopParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, Vec3d color, float scale, Vec3d dir, boolean mature, boolean drip, boolean deform, WaterHandling waterHandling)
	{
		super(world, x, y, z, spriteProvider, color, scale, dir, deform);
		this.maxAge = config.permanent ? Integer.MAX_VALUE : 200 + random.nextInt(100);
		this.alpha = Math.min(random.nextFloat() + 0.5f, 1);
		this.color = mature && config.censorMature ? Vec3d.unpackRgb(config.censorColor) : color;
		this.scale = 0;
		this.size = scale;
		this.normalAlpha = alpha;
		this.appearTicks = random.nextInt(4) + 3;
		this.mature = mature;
		this.drip = drip;
		this.waterHandling = waterHandling;
		GOOP_QUEUE.add(this);
		if(GOOP_QUEUE.size() > config.goopCap)
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
			scale = MathHelper.clampedLerp(0f, size, ((float)age / appearTicks));
		else if(age >= maxAge - 60 && !config.permanent)
		{
			scale = MathHelper.clampedLerp(size, size * 0.5f, (age - (maxAge - 60)) / 60f);
			alpha = MathHelper.clampedLerp(normalAlpha, 0f, (age - (maxAge - 60)) / 60f);
		}
		else
		{
			//Rain cleaning
			if(GoopClient.getConfig().rainCleaning  && waterHandling != WaterHandling.IGNORE &&
					   world.isRaining() && world.isSkyVisible(new BlockPos((int)x, (int)y, (int)z)))
			{
				rain = rain + 1f / 100f;
				scale = MathHelper.clampedLerp(size, size * 1.25f, rain / 10f);
				alpha = MathHelper.clampedLerp(normalAlpha, 0f, rain / 10f);
				if(rain > 10f)
					markDead();
			}
		}
		//Ceiling Drips
		if(drip && dir.getY() < 0 && random.nextInt(120) == 0)
			world.addParticle(new GoopStringParticleEffect(color, 0.25f, mature),
					x + random.nextFloat() * scale - scale / 2f, y + (y < 0 ? 1 : 0), z + random.nextFloat() * scale - scale / 2f,
					0, 0, 0);
		//Fluid handling
		if(world.getFluidState(new BlockPos((int)x, (int)y, (int)z)).isIn(FluidTags.LAVA))
			markDead();
		if(waterHandling == WaterHandling.IGNORE)
			return;
		if(world.isWater(new BlockPos((int)x, (int)y, (int)z)))
		{
			switch(waterHandling)
			{
				case REMOVE_PARTICLE -> markDead();
				case REPLACE_WITH_CLOUD_PARTICLE ->
				{
					world.addParticle(new DustParticleEffect(color.toVector3f(), scale), x, y, z,
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
	
	public static class Factory implements ParticleFactory<GoopParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(GoopParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new GoopParticle(world, x, y, z, spriteProvider, parameters.getColor(), parameters.getScale(), parameters.getDir(), parameters.isMature(),
					parameters.isDrip(), parameters.isDeform(), parameters.getWaterHandling());
		}
	}
}
