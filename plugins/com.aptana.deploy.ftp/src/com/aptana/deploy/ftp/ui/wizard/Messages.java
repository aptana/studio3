/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.deploy.ftp.ui.wizard.messages"; //$NON-NLS-1$

	public static String FTPDeployComposite_AutoSync;
	public static String FTPDeployComposite_Download;
	public static String FTPDeployComposite_Synchronize;
	public static String FTPDeployComposite_Upload;

	public static String FTPDeployWizardPage_ProtocolLabel;
	public static String FTPDeployWizardPage_RemoteInfoLabel;
	public static String FTPDeployWizardPage_SiteNameLabel;
	public static String FTPDeployWizardPage_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
