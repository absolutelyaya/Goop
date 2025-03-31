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
	//public static final ParticleType<GoopParticleEffect> PUDDLE =
	//		Registry.register(Registries.PARTICLE_TYPE, Goop.id( "puddle"),
	//				FabricParticleTypes.complex(new GoopParticleEffect.Factory()));
	//public static final ParticleType<GoopStringParticleEffect> DRIP =
	//		Registry.register(Registries.PARTICLE_TYPE, Goop.id( "drip"),
	//				FabricParticleTypes.complex(new GoopStringParticleEffect.Factory()));
	
	public static void register()
	{
	
	}
}
