package absolutelyaya.goop.api;

import absolutelyaya.goop.particles.AbstractGoopParticleEffect;

public interface IGoopEffectFactory
{
	Class<? extends AbstractGoopParticleEffect> getParticleEffectClass();
}
