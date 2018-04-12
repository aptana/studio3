/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.preferences.messages"; //$NON-NLS-1$

	public static String JSPreferencePage_JS_Page_Title;
	public static String JSPreferencePage_initial_fold_options_label;
	public static String JSPreferencePage_fold_comments_label;
	public static String JSPreferencePage_fold_functions_label;
	public static String JSPreferencePage_fold_objects_label;
	public static String JSPreferencePage_fold_arrays_label;

	public static String NodePreferencePage_DetectedPathLabel;

	public static String NodePreferencePage_downloadButtonText;
	public static String NodePreferencePage_LocationLabel;

	public static String NodePreferencePage_nodejsDirSelectionMessage;
	public static String NodePreferencePage_NotDetected;

	public static String NodePreferencePage_SourceLocationLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
