package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.data.ModularGoopData;

import java.util.List;

public abstract class AbstractEmitter
{
	final List<EntityTypeReference> targets;
	final ModularGoopData goopData;
	
	public AbstractEmitter(List<EntityTypeReference> targets, ModularGoopData goopData)
	{
		this.targets = targets;
		this.goopData = goopData;
	}
	
	abstract EmitterType getType();
}
