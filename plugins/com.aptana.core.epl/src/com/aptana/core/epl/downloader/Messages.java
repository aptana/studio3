package com.aptana.core.epl.downloader;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.core.epl.downloader.messages"; //$NON-NLS-1$
	public static String FileReader_cancelHandler;
	public static String FileReader_connectionRetryMeggage;
	public static String FileReader_fileTrasportReader;
	public static String FileReader_initializationError;
	public static String ProgressStatistics_fetching_1;
	public static String ProgressStatistics_fetching_2;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
