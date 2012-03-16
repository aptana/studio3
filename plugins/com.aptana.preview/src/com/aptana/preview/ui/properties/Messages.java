/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.preview.ui.properties;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.preview.ui.properties.messages"; //$NON-NLS-1$

	public static String ProjectPreviewPropertyPage_ChooseServerType;
	public static String ProjectPreviewPropertyPage_ERR_FailToCreateServer;
	public static String ProjectPreviewPropertyPage_ERR_FailToOpenServerDialog;
	public static String ProjectPreviewPropertyPage_preview_server_label;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
