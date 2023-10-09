package absolutelyaya.goop.particles;

import absolutelyaya.goop.api.ExtraGoopData;
import absolutelyaya.goop.api.WaterHandling;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public abstract class AbstractGoopParticleEffect implements ParticleEffect
{
	protected final Vec3d color;
	protected final float scale;
	protected final boolean mature;
	protected final ExtraGoopData extraGoopData;
	protected final WaterHandling waterHandling;
	protected boolean drip = true, deform = true;
	
	public AbstractGoopParticleEffect(Vec3d color, float scale, boolean mature, ExtraGoopData extraGoopData, WaterHandling waterHandling)
	{
		this.color = color;
		this.scale = MathHelper.clamp(scale, 0.01f, 4f);
		this.mature = mature;
		this.extraGoopData = extraGoopData;
		this.waterHandling = waterHandling;
	}
	
	public static Vec3d readVec3(StringReader reader) throws CommandSyntaxException
	{
		float f = reader.readFloat();
		reader.expect(' ');
		float g = reader.readFloat();
		reader.expect(' ');
		float h = reader.readFloat();
		return new Vec3d(f, g, h);
	}
	
	public static Vec3d readVec3(PacketByteBuf buf)
	{
		return new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
	}
	
	@Override
	public void write(PacketByteBuf buf)
	{
		buf.writeFloat((float)this.color.getX());
		buf.writeFloat((float)this.color.getY());
		buf.writeFloat((float)this.color.getZ());
		buf.writeFloat(this.scale);
		buf.writeBoolean(this.mature);
		if(extraGoopData != null)
			extraGoopData.write(buf);
	}
	
	public String asString()
	{
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f, %s",
				Registries.PARTICLE_TYPE.getId(this.getType()), this.color.getX(), this.color.getY(), this.color.getZ(), this.scale, this.mature);
	}
	
	public Vec3d getColor() {
		return this.color;
	}
	
	public float getScale() {
		return this.scale;
	}
	
	public boolean isMature()
	{
		return mature;
	}
	
	public ExtraGoopData getExtraData()
	{
		return extraGoopData;
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
