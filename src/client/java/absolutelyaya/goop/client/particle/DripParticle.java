package absolutelyaya.goop.client.particle;

import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.particle.DripParticleEffect;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class DripParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	
	Vec3d curScale, lastScale;
	float speed, baseAlpha;
	
	protected DripParticle(ClientWorld world, Vec3d pos, SpriteProvider spriteProvider, int color, float scale)
	{
		super(world, pos.x, pos.y - 0.25, pos.z);
		this.spriteProvider = spriteProvider;
		sprite = spriteProvider.getSprite(random);
		maxAge = random.nextInt(30) + 20;
		curScale = new Vec3d(random.nextFloat() * scale * 0.33f, 0, random.nextFloat() * scale * 0.33f);
		lastScale = curScale;
		collidesWithWorld = true;
		alpha = 0;
		speed = 1.5f + random.nextFloat() * 1.5f;
		
		Color c = new Color(color, (color >> 24 & 0xff) > 0);
		setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		baseAlpha = c.getAlpha() / 255f * 2f;
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
		alpha = Math.min(MathHelper.lerp((float)age / maxAge, baseAlpha, 0f), 1f);
		if (!onGround)
		{
			lastScale = curScale;
			curScale = new Vec3d(curScale.x, Math.pow(age / (float)maxAge, speed) * 2, curScale.z);
		}
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float delta)
	{
		Vec3d camPos = camera.getPos();
		Vec3d dir = new Vec3d(x, y, z).subtract(camPos).normalize();
		float dx = (float)(MathHelper.lerp(delta, prevPosX, x) - camPos.getX());
		float dy = (float)(MathHelper.lerp(delta, prevPosY, y) - camPos.getY());
		float dz = (float)(MathHelper.lerp(delta, prevPosZ, z) - camPos.getZ());
		
		Vec3d[] Vec3ds = new Vec3d[]{
				new Vec3d(-1f, -2f, 0f),
				new Vec3d(-1f, 0f, 0f),
				new Vec3d(1f, 0f, 0f),
				new Vec3d(1f, -2f, 0f)};
		
		for(int k = 0; k < 4; ++k)
			Vec3ds[k] = Vec3ds[k].rotateY((float)Math.atan2(dir.x, dir.z)).multiply(lastScale.lerp(curScale, delta)).add(dx, dy, dz);
		
		int n = this.getBrightness(delta);
		vertexConsumer.vertex((float)Vec3ds[0].getX(), (float)Vec3ds[0].getY(), (float)Vec3ds[0].getZ())
				.texture(getMaxU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n);
		vertexConsumer.vertex((float)Vec3ds[1].getX(), (float)Vec3ds[1].getY(), (float)Vec3ds[1].getZ())
				.texture(getMaxU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n);
		vertexConsumer.vertex((float)Vec3ds[2].getX(), (float)Vec3ds[2].getY(), (float)Vec3ds[2].getZ())
				.texture(getMinU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n);
		vertexConsumer.vertex((float)Vec3ds[3].getX(), (float)Vec3ds[3].getY(), (float)Vec3ds[3].getZ())
				.texture(getMinU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n);
	}
	
	public static class Factory implements ParticleFactory<DripParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(DripParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new DripParticle(world, new Vec3d(x, y, z), spriteProvider, GoopClient.getColorOrCensor(parameters.color(), parameters.mature()), parameters.scale());
		}
	}
}
