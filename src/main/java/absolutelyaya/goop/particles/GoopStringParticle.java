package absolutelyaya.goop.particles;

import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.client.GoopConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class GoopStringParticle extends SpriteAAParticle
{
	protected GoopStringParticle(ClientWorld world, Vec3d pos, SpriteProvider spriteProvider, Vec3d color, float scale, boolean mature)
	{
		super(world, pos.x, pos.y - 0.25, pos.z, spriteProvider);
		gravityStrength = random.nextFloat() * 0.25f + 0.1f;
		maxAge = random.nextInt(15) + 20;
		GoopConfig config = GoopClient.getConfig();
		color = mature && GoopClient.recolorMature() ? Vec3d.unpackRgb(config.censorColor) : color;
		setColor((float)color.getX(), (float)color.getY(), (float)color.getZ());
		this.scale = this.scale.multiply(scale);
		collidesWithWorld = true;
		alpha = 0;
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public void move(double dx, double dy, double dz)
	{
		super.move(dx, dy, dz);
		if (!onGround)
			scale = scale.add(-0.001f, (float)-dy, -0.001f);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		alpha = MathHelper.lerp((float)age / maxAge, 1.2f, 0f);
	}
	
	public static class Factory implements ParticleFactory<GoopStringParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(GoopStringParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new GoopStringParticle(world, new Vec3d(x, y, z), spriteProvider, parameters.getColor(), parameters.getScale(), parameters.isMature());
		}
	}
}
