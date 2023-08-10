package absolutelyaya.goop.particles;

import absolutelyaya.goop.api.ExtraGoopData;
import absolutelyaya.goop.registries.ParticleRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class GoopDropParticleEffect extends AbstractGoopParticleEffect
{
	Identifier effectOverride;
	
	public GoopDropParticleEffect(Vec3d color, float scale, boolean mature)
	{
		super(color, scale, mature, null);
	}
	
	public GoopDropParticleEffect(Vec3d color, float scale, boolean mature, Identifier effectOverride, ExtraGoopData extraData)
	{
		super(color, scale, mature, extraData);
		this.effectOverride = effectOverride;
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleRegistry.GOOP_DROP;
	}
	
	public Identifier getEffectOverride()
	{
		return effectOverride;
	}
	
	public static class Factory implements ParticleEffect.Factory<GoopDropParticleEffect>
	{
		@Override
		public GoopDropParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' ');
			Vec3d color = AbstractGoopParticleEffect.readVec3(reader);
			reader.expect(' ');
			float size = reader.readFloat();
			reader.expect(' ');
			boolean mature = reader.readBoolean();
			return new GoopDropParticleEffect(color, size, mature);
		}
		
		@Override
		public GoopDropParticleEffect read(ParticleType type, PacketByteBuf buf)
		{
			Vec3d color = readVec3(buf);
			float scale = buf.readFloat();
			boolean mature = buf.readBoolean();
			if(buf.readBoolean())
			{
				return new GoopDropParticleEffect(color, scale, mature, buf.readIdentifier(), ExtraGoopData.read(buf));
			}
			return new GoopDropParticleEffect(readVec3(buf), buf.readFloat(), buf.readBoolean());
		}
	}
}
