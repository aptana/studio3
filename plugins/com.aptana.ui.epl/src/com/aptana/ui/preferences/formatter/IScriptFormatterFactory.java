/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.aptana.ui.preferences.IPreferencesLookupDelegate;
import com.aptana.ui.preferences.IPreferencesSaveDelegate;
import com.aptana.ui.preferences.PreferenceKey;

/**
 * Script source code formatter factory interface.
 */
public interface IScriptFormatterFactory
{

	/**
	 * Retrieves the formatting options from the specified <code>delegate</code>
	 * 
	 * @param delegate
	 * @return
	 */
	Map<String, String> retrievePreferences(IPreferencesLookupDelegate delegate);

	/**
	 * Change the preferences to perform only indenting. Preferences affecting the number of lines will be disabled.
	 * 
	 * @param preferences
	 * @return
	 * @since 2.0
	 */
	Map<String, String> changeToIndentingOnly(Map<String, String> preferences);

	PreferenceKey[] getPreferenceKeys();

	PreferenceKey getActiveProfileKey();

	void savePreferences(Map<String, String> preferences, IPreferencesSaveDelegate delegate);

	/**
	 * Creates the {@link IScriptFormatter} with the specified preferences.
	 * 
	 * @param lineDelimiter
	 *            the line delimiter to use
	 * @param preferences
	 *            the formatting options
	 */
	IScriptFormatter createFormatter(String lineDelimiter, Map<String, String> preferences);

	List<IProfile> getBuiltInProfiles();

	List<IProfile> getCustomProfiles();

	void saveCustomProfiles(List<IProfile> profiles);

	IProfileVersioner getProfileVersioner();

	IProfileStore getProfileStore();

	/**
	 * Returns the contribution id
	 */
	String getId();

	/**
	 * Validates that this formatter factory is correctly installed.
	 * 
	 * @return
	 */
	boolean isValid();

	/**
	 * Return the preview content to use with this formatter or <code>null</code> if no preview is available.
	 * 
	 * @return
	 */
	URL getPreviewContent();

	/**
	 * @return
	 */
	IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner);

	/**
	 * @param allProfiles
	 * @return
	 */
	IProfileManager createProfileManager(List<IProfile> profiles);

}
