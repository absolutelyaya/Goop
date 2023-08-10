package absolutelyaya.goop.particles;

import absolutelyaya.goop.registries.ParticleRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;

public class GoopStringParticleEffect extends AbstractGoopParticleEffect
{
	public GoopStringParticleEffect(Vec3d color, float scale, boolean mature)
	{
		super(color, scale, mature, null);
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleRegistry.GOOP_STRING;
	}
	
	public static class Factory implements ParticleEffect.Factory<GoopStringParticleEffect>
	{
		@Override
		public GoopStringParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' ');
			Vec3d color = AbstractGoopParticleEffect.readVec3(reader);
			reader.expect(' ');
			float size = reader.readFloat();
			reader.expect(' ');
			boolean mature = reader.readBoolean();
			return new GoopStringParticleEffect(color, size, mature);
		}
		
		@Override
		public GoopStringParticleEffect read(ParticleType type, PacketByteBuf buf)
		{
			return new GoopStringParticleEffect(readVec3(buf), buf.readFloat(), buf.readBoolean());
		}
	}
}
