package com.aptana.terminal.server;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.terminal.server.messages"; //$NON-NLS-1$
	public static String HttpServer_Client_Accept_Error;
	public static String HttpServer_Process_ID_Already_In_Use;
	public static String HttpServer_Process_ID_Does_Not_Exist;
	public static String HttpServer_Unable_To_Open_Port;
	public static String HttpWorker_Error_Accessing_Output_Stream;
	public static String HttpWorker_Error_Locating_File;
	public static String HttpWorker_Error_Writing_To_Client;
	public static String HttpWorker_Malformed_URI;
	public static String HttpWorker_Not_Found4;
	public static String HttpWorker_Unrecognized_POST_URL;
	public static String ProcessReader_Error_Reading_From_Process;
	public static String ProcessWrapper_Error_Locating_Terminal_Executable;
	public static String ProcessWrapper_Error_Starting_Process;
	public static String ProcessWrapper_Malformed_Terminal_Executable_URI;
	public static String ProcessWrapper_Process_File_Does_Not_Exist;
	public static String ProcessWriter_Error_Wrinting_To_Process;
	public static String Request_Request_Processing_Error;
	public static String Terminal_View_Server_Running_On_Port0;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
