package absolutelyaya.goop.particle;

import com.mojang.serialization.Codec;

public enum WaterHandling
{
	REMOVE_PARTICLE,
	REPLACE_WITH_CLOUD_PARTICLE,
	IGNORE;
	
	public static final Codec<WaterHandling> CODEC = Codec.stringResolver(WaterHandling::name, WaterHandling::valueOf);
}
