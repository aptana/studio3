/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.properties;

import org.eclipse.osgi.util.NLS;

public class EplMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.properties.eplmessages"; //$NON-NLS-1$

	public static String ProjectNaturesPage_CloseProjectJob_Title;
	public static String ProjectNaturesPage_Description;
	public static String ProjectNaturesPage_ERR_CloseProject;
	public static String ProjectNaturesPage_ERR_OpenProject;
	public static String ProjectNaturesPage_ERR_RetrieveNatures;
	public static String ProjectNaturesPage_ERR_SetNatures;
	public static String ProjectNaturesPage_LBL_MakePrimary;
	public static String ProjectNaturesPage_LBL_Primary;
	public static String ProjectNaturesPage_LBL_SetAsPrimary;
	public static String ProjectNaturesPage_ResetMessage;
	public static String ProjectNaturesPage_ResetTitle;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, EplMessages.class);
	}

	private EplMessages()
	{
	}
}
