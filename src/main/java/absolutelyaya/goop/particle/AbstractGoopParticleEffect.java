package absolutelyaya.goop.particle;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractGoopParticleEffect implements ParticleEffect
{
	protected final int color;
	protected final float scale;
	protected final boolean mature;
	protected final WaterHandling waterHandling;
	protected boolean drip = true, deform = true;
	
	public AbstractGoopParticleEffect(int color, float scale, boolean mature, WaterHandling waterHandling)
	{
		this.color = color;
		this.scale = MathHelper.clamp(scale, 0.01f, 4f);
		this.mature = mature;
		this.waterHandling = waterHandling;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public float getScale() {
		return this.scale;
	}
	
	public boolean isMature()
	{
		return mature;
	}
	
	public WaterHandling getWaterHandling()
	{
		return waterHandling;
	}
	
	public AbstractGoopParticleEffect setDrip(boolean drip)
	{
		this.drip = drip;
		return this;
	}
	
	public boolean isDrip()
	{
		return drip;
	}
	
	public AbstractGoopParticleEffect setDeform(boolean deform)
	{
		this.deform = deform;
		return this;
	}
	
	public boolean isDeform()
	{
		return deform;
	}
}
