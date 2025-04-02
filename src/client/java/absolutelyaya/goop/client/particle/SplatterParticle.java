package absolutelyaya.goop.client.particle;

import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.particle.BaseGoopData;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class SplatterParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	final BaseGoopData data;
	final float rotSpeed;
	final Identifier effectOverride;
	
	protected SplatterParticle(ClientWorld clientWorld, Vec3d pos, Vec3d vel, SpriteProvider provider, BaseGoopData data, Identifier effectOverride)
	{
		super(clientWorld, pos.x, pos.y, pos.z);
		spriteProvider = provider;
		this.data = data;
		this.scale = data.scale() - (data.scale() > 1f ? 1.25f * (data.scale() / 2f) : 0f);
		this.rotSpeed = (random.nextFloat() - 0.5f) / 4f / scale;
		this.effectOverride = effectOverride;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 1 + scale / 2;
		maxAge = 300;
		collidesWithWorld = true;
		float[] c = new Color(GoopClient.getColorOrCensor(data), true).getColorComponents(null);
		if(c.length >= 3)
			setColor(c[0], c[1], c[2]);
		if(c.length >= 4)
			alpha = c[3];
		
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
		lastAngle = angle;
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
					world.addParticleClient(new DustParticleEffect(GoopClient.getColorOrCensor(data), scale * 2.5f), x, y, z,
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
				processCollisions(movement, collisions);
		}
		super.move(dx, dy, dz);
	}
	
	void processCollisions(Vec3d movement, List<VoxelShape> collisions)
	{
		Vec3d offset = Vec3d.ZERO;
		for (Direction.Axis axis : Entity.getAxisCheckOrder(movement))
		{
			double axisLength = movement.getComponentAlongAxis(axis);
			if (axisLength != 0.0)
			{
				double e = VoxelShapes.calculateMaxOffset(axis, getBoundingBox().offset(offset), collisions, axisLength);
				offset = offset.withAxis(axis, e);
			}
		}
		offset = offset.normalize();
		Vec3d pos = new Vec3d(x, y, z);
		Direction dir;
		if(Math.abs(offset.y) > Math.abs(offset.x) && Math.abs(offset.y) > Math.abs(offset.z))
			dir = offset.y > 0 ? Direction.UP : Direction.DOWN;
		else if(Math.abs(offset.x) > Math.abs(offset.z))
			dir = offset.x > 0 ? Direction.EAST : Direction.WEST;
		else
			dir = offset.z > 0 ? Direction.SOUTH : Direction.NORTH;
		placePuddle(pos, dir);
	}
	
	void placePuddle(Vec3d pos, Direction dir)
	{
		HitResult hit = world.raycast(new RaycastContext(pos.offset(dir.getOpposite(), 0.2), pos.offset(dir, 16), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
		Vec3d correctedHitPos = (hit.getType().equals(HitResult.Type.MISS) ? pos : hit.getPos()).offset(dir.getOpposite(), 0.005);
		world.addParticleClient(new PuddleParticleEffect(data, dir.getOpposite()), correctedHitPos.x, correctedHitPos.y, correctedHitPos.z, 0, 0, 0);
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
