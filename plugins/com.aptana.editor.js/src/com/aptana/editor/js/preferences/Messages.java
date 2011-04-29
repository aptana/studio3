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

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
