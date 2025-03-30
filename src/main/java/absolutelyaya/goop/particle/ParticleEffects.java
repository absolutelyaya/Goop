package absolutelyaya.goop.particle;

import absolutelyaya.goop.Goop;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ParticleEffects
{
	public static final ParticleType<GoopDropParticleEffect> GOOP_DROP =
			Registry.register(Registries.PARTICLE_TYPE, Goop.id( "goop_drop"),
					FabricParticleTypes.complex(GoopDropParticleEffect.CODEC, GoopDropParticleEffect.PACKET_CODEC));
	public static final ParticleType<GoopParticleEffect> GOOP =
			Registry.register(Registries.PARTICLE_TYPE, Goop.id( "goop"),
					FabricParticleTypes.complex(new GoopParticleEffect.Factory()));
	public static final ParticleType<GoopStringParticleEffect> GOOP_STRING =
			Registry.register(Registries.PARTICLE_TYPE, Goop.id( "goop_string"),
					FabricParticleTypes.complex(new GoopStringParticleEffect.Factory()));
	
	public static void register()
	{
	
	}
}
