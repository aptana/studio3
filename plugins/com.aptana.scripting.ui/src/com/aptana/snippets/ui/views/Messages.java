/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.snippets.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.snippets.ui.views.messages"; //$NON-NLS-1$
	public static String SnippetPopupDialog_content_type_unknown;
	public static String SnippetPopupDialog_Desciption;
	public static String SnippetPopupDialog_Open_Snippet_Source_desc;
	public static String SnippetPopupDialog_Scope_None;
	public static String SnippetsView_Collapse_All_Action;
	public static String SnippetsView_Expand_All_Action;
	public static String SnippetsView_Initial_filter_text;
	public static String SnippetsView_Insert_Snippet_desc;
	public static String SnippetsView_partName;
	public static String SnippetsView_Show_Information_desc;
	public static String SnippetsView_Snippet_drawer_other;
	public static String SnippetsView_Snippet_drawer_title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
