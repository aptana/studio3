/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.messages"; //$NON-NLS-1$

	public static String DiffFormatter_NoContent;
	public static String GitUIPlugin_GitInstallationValidator;
	public static String GitUIPlugin_ConfiguringGitSupportTitle;
	public static String GitUIPlugin_ThisPathIsNotValid;
	public static String GitUIPlugin_ConfigureGitPluginMessage;
	public static String GitUIPlugin_ConfiguringGitSupport;
	public static String GitUIPlugin_GitInstallError;
	public static String GitUIPlugin_GitConfigurationIncomplete;
	public static String GitUIPlugin_GitExecutableFiles;
	public static String GitUIPlugin_SelectGitExecutableLocation;
	public static String GitUIPlugin_InvalidPathSpecified;
	public static String GitUIPlugin_ToggleMessage;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
