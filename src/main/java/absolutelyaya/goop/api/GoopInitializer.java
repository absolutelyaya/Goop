package absolutelyaya.goop.api;

@FunctionalInterface
public interface GoopInitializer
{
	/**
	 * Register your Goop Emitters in here!
	 * @see absolutelyaya.goop.api.Examples
	 * @see absolutelyaya.goop.api.GoopEmitterRegistry
	 */
	void registerGoopEmitters();
}
