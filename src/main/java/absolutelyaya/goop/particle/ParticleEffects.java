package absolutelyaya.goop.particle;

import absolutelyaya.goop.Goop;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ParticleEffects
{
	public static final ParticleType<SplatterParticleEffect> SPLATTER =
			Registry.register(Registries.PARTICLE_TYPE, Goop.id( "splatter"),
					FabricParticleTypes.complex(SplatterParticleEffect.CODEC, SplatterParticleEffect.PACKET_CODEC));
	public static final ParticleType<PuddleParticleEffect> PUDDLE =
			Registry.register(Registries.PARTICLE_TYPE, Goop.id( "puddle"),
					FabricParticleTypes.complex(PuddleParticleEffect.CODEC, PuddleParticleEffect.PACKET_CODEC));
	public static final ParticleType<DripParticleEffect> DRIP =
			Registry.register(Registries.PARTICLE_TYPE, Goop.id( "drip"),
					FabricParticleTypes.complex(DripParticleEffect.CODEC, DripParticleEffect.PACKET_CODEC));
	
	public static void register()
	{
	
	}
}
