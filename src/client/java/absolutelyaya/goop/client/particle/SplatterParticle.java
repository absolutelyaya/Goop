package absolutelyaya.goop.client.particle;

import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.data.FinalGoopData;
import absolutelyaya.goop.particle.PuddleParticleEffect;
import absolutelyaya.goop.particle.SplatterParticleEffect;
import absolutelyaya.goop.particle.WaterHandling;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class SplatterParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	final FinalGoopData data;
	final float rotSpeed;
	final Identifier effectOverride;
	
	protected SplatterParticle(ClientWorld clientWorld, Vec3d pos, Vec3d vel, SpriteProvider provider, FinalGoopData data, Identifier effectOverride)
	{
		super(clientWorld, pos.x, pos.y, pos.z);
		spriteProvider = provider;
		this.data = data;
		this.scale = data.scale() - 1.25f * (data.scale() / 2f);
		this.rotSpeed = (random.nextFloat() - 0.5f) / 4f / scale;
		this.effectOverride = effectOverride;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 1 + scale / 2;
		maxAge = 300;
		collidesWithWorld = true;
		
		int ci = GoopClient.getColorOrCensor(data);
		Color c = new Color(ci, (ci >> 24 & 0xff) > 0);
		setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		alpha = c.getAlpha() / 255f;
		
		if(vel.length() > 0f)
			setVelocity(vel.x, vel.y, vel.z);
		else
		{
			Vec3d dir = new Vec3d(0, 0, random.nextFloat() / 2f).rotateY((float)Math.toRadians(random.nextFloat() * 360f));
			setVelocity(dir.x, random.nextFloat() * 0.5f, dir.z);
		}
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
		prevAngle = angle;
		angle += rotSpeed;
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
					world.addParticle(new DustParticleEffect(GoopClient.getColorOrCensorVec(data), scale * 2.5f), x, y, z,
							random.nextFloat() * 0.1f, random.nextFloat() * 0.1f, random.nextFloat() * 0.1f);
					markDead();
				}
			}
		}
	}
	
	@Override
	public void move(double dx, double dy, double dz)
	{
		if(collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < 100 * 100)
		{
			Vec3d movement = new Vec3d(dx, dy, dz);
			List<VoxelShape> collisions = Entity.findCollisionsForMovement(null, world, List.of(), getBoundingBox().stretch(movement));
			if(!collisions.isEmpty())
				processImpactSurface(movement);
		}
		super.move(dx, dy, dz);
	}
	
	void processImpactSurface(Vec3d movement)
	{
		Vec3d pos = new Vec3d(x, y, z);
		HitResult hit = world.raycast(new RaycastContext(pos, pos.add(movement),
				RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
		if(!hit.getType().equals(HitResult.Type.MISS) && hit instanceof BlockHitResult bHit)
			placePuddle(bHit.getPos().offset(bHit.getSide().getOpposite(), 0), bHit.getSide().getOpposite());
		else
			markDead();
	}
	
	void placePuddle(Vec3d pos, Direction dir)
	{
		HitResult hit = world.raycast(new RaycastContext(pos.offset(dir.getOpposite(), 0.2), pos.offset(dir, 16),
				RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
		Vec3d correctedHitPos = (hit.getType().equals(HitResult.Type.MISS) ? pos : hit.getPos()).offset(dir.getOpposite(), 0.005);
		world.addParticle(new PuddleParticleEffect(data, dir.getOpposite()),
				correctedHitPos.x, correctedHitPos.y, correctedHitPos.z, 0, 0, 0);
		markDead();
	}
	
	public static class Factory implements ParticleFactory<SplatterParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(SplatterParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new SplatterParticle(world, new Vec3d(x, y, z), new Vec3d(velocityX, velocityY, velocityZ), spriteProvider, parameters.data(),
					parameters.effectOverride().orElse(null));
		}
	}
}
