package absolutelyaya.goop.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EggGoopParticle extends GoopParticle
{
	protected EggGoopParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, Vec3d color, float scale, Vec3d dir, boolean mature)
	{
		super(world, x, y, z, spriteProvider, color, scale, dir, mature);
	}
	
	public static class Factory implements ParticleFactory<EggGoopParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(EggGoopParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new EggGoopParticle(world, x, y, z, spriteProvider, parameters.getColor(), parameters.getScale(), parameters.getDir(), parameters.isMature());
		}
	}
}
