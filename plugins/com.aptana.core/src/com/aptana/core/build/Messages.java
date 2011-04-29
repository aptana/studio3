package com.aptana.core.build;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.core.build.messages"; //$NON-NLS-1$
	public static String UnifiedBuilder_FinishedBuild;
	public static String UnifiedBuilder_PerformingFullBuildNullDelta;
	public static String UnifiedBuilder_PerformingFullBuld;
	public static String UnifiedBuilder_PerformingIncrementalBuild;
	public static String UnifiedBuilder_StartingBuild;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
