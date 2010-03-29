package com.aptana.usage;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.usage.messages"; //$NON-NLS-1$

	public static String AptanaDB_Error_Execute_Query;
	public static String AptanaDB_ErrorShutdown;
	public static String AptanaDB_FailedToAccess;
	public static String AptanaDB_FailedToConnect;
	public static String AptanaDB_FailedToInstantiate;
	public static String AptanaDB_FailedToLoad;

	public static String PingStartup_ERR_FailedToContactServer;
	public static String PingStartup_ERR_IOException;
	public static String PingStartup_ERR_MalformedURL;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
