package absolutelyaya.goop.client.config;

import absolutelyaya.goop.Goop;
import absolutelyaya.yayconfig.config.BooleanEntry;
import absolutelyaya.yayconfig.config.ClientConfig;
import absolutelyaya.yayconfig.config.Comment;
import absolutelyaya.yayconfig.config.IntegerEntry;

public class GoopClientConfig extends ClientConfig
{
	public static GoopClientConfig INSTANCE;
	
	public final IntegerEntry goopCap = new IntegerEntry("GoopCap", 420);
	public final BooleanEntry rainCleaning = new BooleanEntry("RainCleaning", true);
	public final BooleanEntry permanent = new BooleanEntry("Permanent", false);
	public final BooleanEntry puddleRot = new BooleanEntry("PuddleRotation", true);
	public final BooleanEntry fancy = new BooleanEntry("Fancy", true);
	public final BooleanEntry censor = new BooleanEntry("CensorMature", false);
	public final IntegerEntry censorColor = new IntegerEntry("CensorColor", 0xffef33ff);
	public final BooleanEntry debug = new BooleanEntry("Debug", false);
	
	public GoopClientConfig()
	{
		super(Goop.id("client"));
		
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(new Comment("     Welcome to Config Zone"));
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(goopCap);
		addEntry(rainCleaning);
		addEntry(permanent);
		addEntry(puddleRot);
		addEntry(fancy);
		addEntry(censor);
		addEntry(debug);
		
		INSTANCE = this;
	}
	
	@Override
	protected String getFileName()
	{
		return "client.properties";
	}
}
