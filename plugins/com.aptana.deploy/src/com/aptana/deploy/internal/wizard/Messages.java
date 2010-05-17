package com.aptana.deploy.internal.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.deploy.internal.wizard.messages"; //$NON-NLS-1$

	public static String FTPDeployComposite_AutoSync;
	public static String FTPDeployComposite_Download;
	public static String FTPDeployComposite_Upload;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
