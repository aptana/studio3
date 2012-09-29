/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class EplMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.preferences.eplmessages"; //$NON-NLS-1$

	public static String GenericRootPage_genericPerferencesPageMessage;
	public static String GenericRootPage_noAvailablePages;
	public static String GenericRootPage_preferences;

	public static String GenericRootPreferencePage_clearMessagesButtonLabel;
	public static String GenericRootPreferencePage_clearMessagesLabelText;
	public static String GenericRootPreferencePage_dialogsGroup;

	public static String PropertyAndPreferencePage_configureProjectSettings;
	public static String PropertyAndPreferencePage_configureWorkspaceSettings;
	public static String PropertyAndPreferencePage_enableProjectSpecific;
	public static String PropertyAndPreferencePage_projectSettingsLabel;
	public static String PropertyAndPreferencePage_workspaceSettingsLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, EplMessages.class);
	}

	private EplMessages()
	{
	}
}
