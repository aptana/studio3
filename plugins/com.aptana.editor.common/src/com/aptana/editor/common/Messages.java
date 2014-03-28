/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.messages"; //$NON-NLS-1$
	private static final String BUNDLE_FOR_CONSTRUCTED_KEYS = BUNDLE_NAME;
	private static ResourceBundle fgBundleForConstructedKeys = ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);

	public static String AbstractThemeableEditor_ConfirmOverwrite_Message;
	public static String AbstractThemeableEditor_ConfirmOverwrite_Title;
	public static String AbstractThemeableEditor_CursorPositionLabel;
	public static String AbstractThemeableEditor_Error_Message;
	public static String AbstractThemeableEditor_Error_Title;
	public static String AbstractThemeableEditor_SaveToggleDialog_LocalFilesystem;
	public static String AbstractThemeableEditor_SaveToggleDialog_Message;
	public static String AbstractThemeableEditor_SaveToggleDialog_Project;
	public static String AbstractThemeableEditor_SaveToggleDialog_Title;

	public static String CommonOccurrencesUpdater_Mark_Word_Occurrences;
	public static String CommonOccurrencesUpdater_Word_Occurrence_Description;
	public static String FileService_FailedToParse;
	public static String Folding_GroupName;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}

	static ResourceBundle getBundleForConstructedKeys()
	{
		return fgBundleForConstructedKeys;
	}
}
