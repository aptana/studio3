package com.aptana.terminal.server;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.terminal.server.messages"; //$NON-NLS-1$
	public static String HttpServer_Client_Accept_Error;
	public static String HttpServer_Process_ID_Already_In_Use;
	public static String HttpServer_Process_ID_Does_Not_Exist;
	public static String HttpServer_Unable_To_Open_Port;
	public static String HttpWorker_Unrecognized_POST_URL;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
