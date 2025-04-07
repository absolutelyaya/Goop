package absolutelyaya.goop.client.emitter;

import absolutelyaya.goop.data.Calculatable;
import absolutelyaya.goop.data.ModularGoopData;

import java.util.List;

public abstract class AbstractEmitter
{
	final List<EntityTypeReference> targets;
	final ModularGoopData goopData;
	final Calculatable count, speed;
	
	public AbstractEmitter(List<EntityTypeReference> targets, ModularGoopData goopData, Calculatable count, Calculatable speed)
	{
		this.targets = targets;
		this.goopData = goopData;
		this.count = count;
		this.speed = speed;
	}
	
	abstract EmitterType getType();
}
