package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.particle.BaseGoopData;

import java.util.List;

public abstract class AbstractEmitter
{
	final List<EntityTypeReference> targets;
	final BaseGoopData goopData;
	
	public AbstractEmitter(List<EntityTypeReference> targets, BaseGoopData goopData)
	{
		this.targets = targets;
		this.goopData = goopData;
	}
	
	abstract EmitterType getType();
}
