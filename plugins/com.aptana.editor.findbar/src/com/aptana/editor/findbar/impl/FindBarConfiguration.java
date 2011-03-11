/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
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

	private IPreferenceStore getPreferenceStore()
	{
		return FindBarPlugin.getDefault().getPreferenceStore();
	}

	// Getters ------------

	public boolean getSearchBackward()
	{
		return getPreferenceStore().getBoolean(IPreferencesConstants.SEARCH_BACKWARD_IN_FIND_BAR);
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
	}

	public void setWholeWord(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.WHOLE_WORD_IN_FIND_BAR, enable);
	}

	public void setRegularExpression(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.REGULAR_EXPRESSION_IN_FIND_BAR, enable);
	}

	public void setSearchBackward(boolean enable)
	{
		getPreferenceStore().setValue(IPreferencesConstants.SEARCH_BACKWARD_IN_FIND_BAR, enable);
	}

}
