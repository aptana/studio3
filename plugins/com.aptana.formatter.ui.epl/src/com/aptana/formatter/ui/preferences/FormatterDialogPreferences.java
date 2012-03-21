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
package com.aptana.formatter.ui.preferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.preferences.IPreferenceDelegate;

public class FormatterDialogPreferences implements IPreferenceDelegate
{

	private final Map<String, String> preferences = new HashMap<String, String>();

	public String getString(Object key)
	{
		final String value = preferences.get(key);
		return value != null ? value : StringUtil.EMPTY;
	}

	public boolean getBoolean(Object key)
	{
		return Boolean.valueOf(getString(key)).booleanValue();
	}

	public void setString(Object key, String value)
	{
		preferences.put((String) key, value);
	}

	public void setBoolean(Object key, boolean value)
	{
		setString(key, String.valueOf(value));
	}

	/**
	 * @return
	 */
	public Map<String, String> get()
	{
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * @param prefs
	 */
	public void set(Map<String, String> prefs)
	{
		preferences.clear();
		if (prefs != null)
		{
			preferences.putAll(prefs);
		}
	}

}
