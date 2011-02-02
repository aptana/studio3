/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.actions;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.actions.messages"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public static String AbstractToggleLinkingAction_Description;
	public static String AbstractToggleLinkingAction_LBL;
	public static String AbstractToggleLinkingAction_TTP;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static ResourceBundle getResourceBundle()
	{
		return RESOURCE_BUNDLE;
	}

	private Messages()
	{
	}
}
