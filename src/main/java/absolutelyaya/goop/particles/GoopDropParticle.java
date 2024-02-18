package absolutelyaya.goop.particles;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.api.ExtraGoopData;
import absolutelyaya.goop.api.IGoopEffectFactory;
import absolutelyaya.goop.api.WaterHandling;
import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.client.GoopConfig;
import absolutelyaya.goop.registries.ParticleRegistry;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class GoopDropParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	protected final Vec3d color;
	protected final boolean mature, drip, deform;
	final float rotSpeed;
	final float totalScale;
	final Identifier effectOverride;
	final ExtraGoopData extraData;
	final WaterHandling waterHandling;
	
	protected GoopDropParticle(ClientWorld clientWorld, Vec3d pos, Vec3d vel, SpriteProvider spriteProvider, Vec3d color, float scale, boolean mature, boolean drip, boolean deform, WaterHandling waterHandling, Identifier effectOverride, ExtraGoopData extraData)
	{
		super(clientWorld, pos.x, pos.y, pos.z);
		GoopConfig config = GoopClient.getConfig();
		color = mature && config.censorMature ? Vec3d.unpackRgb(config.censorColor) : color;
		setColor((float)color.getX(), (float)color.getY(), (float)color.getZ());
		this.color = color;
		this.scale = scale - (scale > 1 ? 1.25f * (scale / 2) : 0f);
		totalScale = scale;
		this.spriteProvider = spriteProvider;
		this.mature = mature;
		this.drip = drip;
		this.deform = deform;
		this.effectOverride = effectOverride;
		this.extraData = extraData;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 1 + scale / 2;
		maxAge = 300;
		setVelocity(random.nextFloat() * 0.5 - 0.25, random.nextFloat() * 0.5, random.nextFloat() * 0.5 - 0.25);
		collidesWithWorld = true;
		this.waterHandling = waterHandling;
		
		rotSpeed = (random.nextFloat() - 0.5f) * 0.25f;
		
		if(vel.distanceTo(Vec3d.ZERO) > 0)
			setVelocity(vel.x, vel.y, vel.z);
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
		if(waterHandling == WaterHandling.IGNORE)
			return;
		if(world.isWater(new BlockPos((int)x, (int)y, (int)z)))
		{
			switch(waterHandling)
			{
				case REMOVE_PARTICLE -> markDead();
				case REPLACE_WITH_CLOUD_PARTICLE ->
				{
					world.addParticle(new DustParticleEffect(color.toVector3f(), totalScale * 2.5f), x, y, z,
							random.nextFloat() * 0.1f, random.nextFloat() * 0.1f, random.nextFloat() * 0.1f);
					markDead();
				}
			}
		}
	}
	
	ParticleType<GoopParticleEffect> getEffect(Identifier override)
	{
		if(override == null)
			return ParticleRegistry.GOOP;
		RegistryKey<ParticleType<?>> registryKey = RegistryKey.of(RegistryKeys.PARTICLE_TYPE, override);
		Optional<RegistryEntry.Reference<ParticleType<?>>> output = Registries.PARTICLE_TYPE.getReadOnlyWrapper().getOptional(registryKey);
		return (ParticleType<GoopParticleEffect>)output.orElseThrow(() -> new InvalidIdentifierException(String.format("Identifier '%s' is not a Valid Particle Type", override))).value();
	}
	
	Constructor<? extends AbstractGoopParticleEffect> getConstructor(ParticleType<?> type)
	{
		try
		{
			return ((IGoopEffectFactory)type.getParametersFactory()).getParticleEffectClass().getConstructor(Vec3d.class, float.class, Vec3d.class, boolean.class, WaterHandling.class, ExtraGoopData.class);
		}
		catch (NoSuchMethodException e)
		{
			Goop.LOGGER.error("Required Goop Particle Effect Constructor not found:\n\b" + e.getMessage());
			return null;
		}
	}
	
	void nextParticle(BlockPos pos, Vec3d dir)
	{
		VoxelShape shape = world.getBlockState(pos.subtract(new Vec3i(0, 0, 0))).getCollisionShape(world, pos);
		if(!shape.isEmpty())
		{
			dir = dir.normalize();
			
			float height = (float)shape.getMax(Direction.Axis.Y);
			Vec3d offset = new Vec3d(0.01, 0.01, 0.01);
			offset = offset.add(dir.x < 0 ? 0 : 1, dir.y < 0 ? 0 : height, dir.z < 0 ? 0 : 1);
			
			try
			{
				if(dir.y != 0)
					world.addParticle(getConstructor(getEffect(effectOverride))
											  .newInstance(color, totalScale * 2.5f, dir, mature, waterHandling, extraData).setDrip(drip).setDeform(deform),
							x, pos.getY() + dir.y * offset.y, z,
							0, 0, 0);
				else if(dir.x != 0)
					world.addParticle(getConstructor(getEffect(effectOverride))
											  .newInstance(color, totalScale * 2.5f, dir, mature, waterHandling, extraData).setDrip(drip).setDeform(deform),
							pos.getX() + dir.x * offset.x, y, z,
							0, 0, 0);
				else if(dir.z != 0)
					world.addParticle(getConstructor(getEffect(effectOverride))
											  .newInstance(color, totalScale * 2.5f, dir, mature, waterHandling, extraData).setDrip(drip).setDeform(deform),
							x, y, pos.getZ() + dir.z * offset.z,
							0, 0, 0);
			}
			catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
			{
				Goop.LOGGER.error(e.getMessage());
			}
		}
	}
	
	@Override
	public void move(double dx, double dy, double dz)
	{
		if (this.collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MathHelper.square(100.0))
		{
			Iterator<VoxelShape> it = world.getBlockCollisions(null, getBoundingBox().stretch(new Vec3d(dx, dy, dz))).iterator();
			VoxelShape closest = null;
			float closestDist = Float.MAX_VALUE;
			Vec3d pos = new Vec3d(x, y, z);
			while(it.hasNext())
			{
				VoxelShape shape = it.next();
				Optional<Vec3d> closestPoint = shape.getClosestPointTo(pos);
				if(closestPoint.isEmpty())
					continue;
				float dist = (float)pos.distanceTo(closestPoint.get());
				if(dist < closestDist)
				{
					closest = shape;
					closestDist = dist;
				}
			}
			if(closest != null)
			{
				Vec3d point = closest.getBoundingBox().getCenter();
				Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, List.of());
				Vec3d diff = vec3d.subtract(new Vec3d(dx, dy, dz)).normalize();
				
				nextParticle(BlockPos.ofFloored(point.x, point.y, point.z), diff);
				markDead();
			}
		}
		super.move(dx, dy, dz);
	}
	
	public static class Factory implements ParticleFactory<GoopDropParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(GoopDropParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new GoopDropParticle(world, new Vec3d(x, y, z), new Vec3d(velocityX, velocityY, velocityZ),
					spriteProvider, parameters.getColor(), parameters.getScale(), parameters.isMature(), parameters.isDrip(), parameters.isDeform(), parameters.getWaterHandling(),
					parameters.getEffectOverride(), parameters.getExtraData());
		}
	}
}
