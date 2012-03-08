/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.preferences;

public interface IPreferenceConstants
{

	/**
	 * Preference key used to save the active theme.
	 */
	public static final String ACTIVE_THEME = "ACTIVE_THEME"; //$NON-NLS-1$

	/**
	 * Pref key for enabling "invasive" themes (extend to JDT and other views that are not explicitly ours).
	 */
	public static final String APPLY_TO_ALL_VIEWS = "enable_invasive_themes"; //$NON-NLS-1$

	/**
	 * Pref key for a boolean value. Should we apply the monospaced text font to views?
	 */
	public static final String INVASIVE_FONT = "enable_invasive_font"; //$NON-NLS-1$

	/**
	 * When we have invasive themes on, should we apply to everything we can, or just the views (and leave non-Aptana
	 * editors alone?)
	 */
	public static final String APPLY_TO_ALL_EDITORS = "apply_to_all_editors"; //$NON-NLS-1$
}
