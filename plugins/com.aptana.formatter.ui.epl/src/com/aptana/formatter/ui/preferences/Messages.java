/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.formatter.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.ui.preferences.messages"; //$NON-NLS-1$

	public static String PropertyAndPreferencePage_configureProjectSettings;
	public static String PropertyAndPreferencePage_configureWorkspaceSettings;
	public static String PropertyAndPreferencePage_enableProjectSpecific;
	public static String AddRemoveList_add;
	public static String AddRemoveList_remove;
	public static String AddRemoveList_inputMessageErrorInfo;
	public static String AddRemoveList_inputMessageTitle;
	public static String AddRemoveList_inputMessageText;
	// Positive number validator
	public static String PositiveNumberIsEmpty;
	public static String PositiveNumberIsInvalid;
	// Port validator
	public static String PortIsEmpty;
	public static String PortShouldBeInRange;
	public static String MinValueInvalid;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
