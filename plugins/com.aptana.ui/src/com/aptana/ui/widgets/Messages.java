/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.widgets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.widgets.messages"; //$NON-NLS-1$

	public static String SingleProjectView_CaseSensitive;
	public static String SingleProjectView_InitialFileFilterText;
	public static String SingleProjectView_RegularExpression;
	public static String SingleProjectView_Wildcard;

	public static String SelectedTemplateGroup_Label;

	public static String SelectedTemplateDesc_More_Label;
	public static String SelectedTemplateDesc_Less_Label;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
