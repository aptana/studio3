/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.findbar.FindBarPlugin;
import com.aptana.editor.findbar.preferences.IPreferencesConstants;

/**
 * Helper to set/get the configurations from the preferences.
 * 
 * @author fabioz
 */
public class FindBarConfiguration
{

	private final EclipseFindSettings eclipseFindSettings;

	public FindBarConfiguration(EclipseFindSettings eclipseFindSettings)
	{
		this.eclipseFindSettings = eclipseFindSettings;
	}

	private IPreferenceStore getPreferenceStore()
	{
		return FindBarPlugin.getDefault().getPreferenceStore();
	}

	// Getters ------------

	public boolean getSearchBackward()
	{
		return getPreferenceStore().getBoolean(IPreferencesConstants.SEARCH_BACKWARD_IN_FIND_BAR);
	}

	public boolean getMatchCount()
	{
		return getPreferenceStore().getBoolean(IPreferencesConstants.MATCH_COUNT_IN_FIND_BAR);
	}

	public boolean getRegularExpression()
	{
		return getPreferenceStore().getBoolean(IPreferencesConstants.REGULAR_EXPRESSION_IN_FIND_BAR);
	}

	public boolean getCaseSensitive()
	{
		return getPreferenceStore().getBoolean(IPreferencesConstants.CASE_SENSITIVE_IN_FIND_BAR);
	}

	public boolean getWholeWord()
	{
		return getPreferenceStore().getBoolean(IPreferencesConstants.WHOLE_WORD_IN_FIND_BAR);
	}

	// Setters -----------

	public void setCaseSensitive(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.CASE_SENSITIVE_IN_FIND_BAR, enable);
		eclipseFindSettings.fCase = enable;
		eclipseFindSettings.writeConfiguration();
	}

	public void setWholeWord(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.WHOLE_WORD_IN_FIND_BAR, enable);
		eclipseFindSettings.fWholeWord = enable;
		eclipseFindSettings.writeConfiguration();
	}

	public void setRegularExpression(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.REGULAR_EXPRESSION_IN_FIND_BAR, enable);
		eclipseFindSettings.fRegExSearch = enable;
		eclipseFindSettings.writeConfiguration();
	}

	public void setSearchBackward(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.SEARCH_BACKWARD_IN_FIND_BAR, enable);
		// Not saved in the Eclipse settings.
	}

	public void setMatchCount(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.MATCH_COUNT_IN_FIND_BAR, enable);
		// Not saved in the Eclipse settings.
	}

	/**
	 * Updates the settings in the preference store used by the find bar with the settings in the Eclipse find actions.
	 */
	public void updateFromEclipseFindSettings()
	{
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.setValue(IPreferencesConstants.CASE_SENSITIVE_IN_FIND_BAR, eclipseFindSettings.fCase);
		preferenceStore
				.setValue(IPreferencesConstants.REGULAR_EXPRESSION_IN_FIND_BAR, eclipseFindSettings.fRegExSearch);
		preferenceStore.setValue(IPreferencesConstants.WHOLE_WORD_IN_FIND_BAR, eclipseFindSettings.fWholeWord);
	}

	/**
	 * Toggles the setting for the given preferences key.
	 */
	public void toggle(String preferencesKey)
	{
		IPreferenceStore preferenceStore = getPreferenceStore();
		boolean b = !preferenceStore.getBoolean(preferencesKey);
		if (preferencesKey.equals(IPreferencesConstants.REGULAR_EXPRESSION_IN_FIND_BAR))
		{
			setRegularExpression(b);
		}
		else if (preferencesKey.equals(IPreferencesConstants.WHOLE_WORD_IN_FIND_BAR))
		{
			setWholeWord(b);
		}
		else if (preferencesKey.equals(IPreferencesConstants.CASE_SENSITIVE_IN_FIND_BAR))
		{
			setCaseSensitive(b);
		}
		else
		{
			preferenceStore.setValue(preferencesKey, b);
		}
	}

}
