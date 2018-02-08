/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.outline.messages"; //$NON-NLS-1$

	public static String CommonOutlinePage_InitialFilterText;
	public static String CommonOutlinePage_Sorting_Description;
	public static String CommonOutlinePage_Sorting_LBL;
	public static String CommonOutlinePage_Sorting_TTP;

	public static String CommonQuickOutlinePage_CollapseAll;

	public static String CommonQuickOutlinePage_ExpandAll;

	public static String CommonQuickOutlinePage_FilterLabel;

	public static String CommonQuickOutlinePage_SortAlphabetically;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
