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
package com.aptana.formatter;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.preferences.IPreferencesLookupDelegate;
import com.aptana.formatter.preferences.IPreferencesSaveDelegate;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.preferences.profile.IProfile;
import com.aptana.formatter.preferences.profile.IProfileManager;
import com.aptana.formatter.preferences.profile.IProfileStore;
import com.aptana.formatter.preferences.profile.ProfileManager;
import com.aptana.formatter.preferences.profile.ProfileStore;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

/**
 * Abstract base class for the {@link IScriptFormatterFactory} implementations.
 */
public abstract class AbstractScriptFormatterFactory extends ContributedExtension implements IScriptFormatterFactory
{

	private String mainContentType;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatterFactory#getLanguage()
	 */
	public String getMainContentType()
	{
		if (mainContentType == null)
		{
			// try to retrieve it from the extension contribution definition
			mainContentType = ScriptFormatterManager.getContentTypeByFactory(this);
		}
		return mainContentType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatterFactory#setLanguage(java.lang.String)
	 */
	public void setMainContentType(String mainContentType)
	{
		this.mainContentType = mainContentType;
	}

	protected PreferenceKey getProfilesKey()
	{
		return null;
	}

	public void saveCustomProfiles(List<IProfile> profiles)
	{
		final PreferenceKey profilesKey = getProfilesKey();
		if (profilesKey != null)
		{
			final IProfileStore store = ProfileManager.getInstance().getProfileStore();
			try
			{
				String value = ((ProfileStore) store).writeProfiles(profiles);
				profilesKey.setStoredValue(new InstanceScope(), value);
			}
			catch (CoreException e)
			{
				FormatterPlugin.logError(e);
			}
		}
	}

	public Map<String, String> retrievePreferences(IPreferencesLookupDelegate delegate)
	{
		ProfileManager profileManager = ProfileManager.getInstance();
		final PreferenceKey activeProfileKey = profileManager.getActiveProfileKey();
		if (activeProfileKey != null)
		{
			final String profileId = delegate.getString(activeProfileKey.getQualifier(), activeProfileKey.getName());
			if (profileId != null && profileId.length() != 0)
			{
				for (IProfile profile : profileManager.getBuiltInProfiles())
				{
					if (profileId.equals(profile.getID()))
					{
						return profile.getSettings();
					}
				}
				for (IProfile profile : profileManager.getCustomProfiles())
				{
					if (profileId.equals(profile.getID()))
					{
						return profile.getSettings();
					}
				}
			}
		}
		final Map<String, String> result = new HashMap<String, String>();
		final PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null)
		{
			for (int i = 0; i < keys.length; ++i)
			{
				final PreferenceKey prefKey = keys[i];
				final String key = prefKey.getName();
				result.put(key, delegate.getString(prefKey.getQualifier(), key));
			}
		}
		return result;
	}

	/**
	 * @since 2.0
	 */
	public Map<String, String> changeToIndentingOnly(Map<String, String> preferences)
	{
		return preferences;
	}

	public void savePreferences(Map<String, String> preferences, IPreferencesSaveDelegate delegate)
	{
		final PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null)
		{
			for (int i = 0; i < keys.length; ++i)
			{
				final PreferenceKey prefKey = keys[i];
				final String key = prefKey.getName();
				if (preferences.containsKey(key))
				{
					final String value = preferences.get(key);
					delegate.setString(prefKey.getQualifier(), key, value);
				}
			}
		}

		final PreferenceKey activeProfileKey = ProfileManager.getInstance().getActiveProfileKey();
		if (activeProfileKey != null && preferences.containsKey(activeProfileKey.getName()))
		{
			final String value = preferences.get(activeProfileKey.getName());
			delegate.setString(activeProfileKey.getQualifier(), activeProfileKey.getName(), value);
		}
	}

	public boolean isValid()
	{
		return true;
	}

	public URL getPreviewContent()
	{
		return null;
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner, IProfileManager manager)
	{
		return null;
	}
}
