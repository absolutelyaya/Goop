package absolutelyaya.goop.registries;

import absolutelyaya.goop.Goop;
import absolutelyaya.goop.particles.EggGoopParticleEffect;
import absolutelyaya.goop.particles.GoopDropParticleEffect;
import absolutelyaya.goop.particles.GoopParticleEffect;
import absolutelyaya.goop.particles.GoopStringParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleRegistry
{
	public static final ParticleType<GoopDropParticleEffect> GOOP_DROP =
			Registry.register(Registry.PARTICLE_TYPE, new Identifier(Goop.MOD_ID, "goop_drop"),
					FabricParticleTypes.complex(new GoopDropParticleEffect.Factory()));
	public static final ParticleType<GoopParticleEffect> GOOP =
			Registry.register(Registry.PARTICLE_TYPE, new Identifier(Goop.MOD_ID, "goop"),
					FabricParticleTypes.complex(new GoopParticleEffect.Factory()));
	public static final ParticleType<GoopStringParticleEffect> GOOP_STRING =
			Registry.register(Registry.PARTICLE_TYPE, new Identifier(Goop.MOD_ID, "goop_string"),
					FabricParticleTypes.complex(new GoopStringParticleEffect.Factory()));
	public static final ParticleType<EggGoopParticleEffect> EGG_GOOP =
			Registry.register(Registry.PARTICLE_TYPE, new Identifier(Goop.MOD_ID, "egg_goop"),
					FabricParticleTypes.complex(new EggGoopParticleEffect.Factory()));
}
