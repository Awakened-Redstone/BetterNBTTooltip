package net.awakenedredstone.nbttooltip.config;

public class ConfigInstance {

	public boolean showSeparator;
	public int maxLinesShown;
	public boolean requiresf3;
	public boolean showDelimiters;
	public boolean compress;
	public boolean hybridRender;
	public int maxWidth;
	public int ticksBeforeScroll;
	public boolean ctrlSuppressesRest;
	
	public ConfigInstance(boolean showSeparator, int maxLinesShown, boolean requiresf3, boolean showDelimiters, boolean compress, int ticksBeforeScroll, boolean ctrlSuppressesRest, boolean hybridRender, int maxWidth) {
		super();
		this.showSeparator = showSeparator;
		this.maxLinesShown = maxLinesShown;
		this.requiresf3 = requiresf3;
		this.showDelimiters = showDelimiters;
		this.compress = compress;
		this.hybridRender  = hybridRender;
		this.maxWidth = maxWidth;
		this.ticksBeforeScroll = ticksBeforeScroll;
		this.ctrlSuppressesRest = ctrlSuppressesRest;
	}
	
}
