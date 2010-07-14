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
package com.aptana.formatter.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.profile.BuiltInProfile;
import com.aptana.formatter.ui.profile.GeneralProfileVersioner;
import com.aptana.formatter.ui.profile.ProfileManager;
import com.aptana.formatter.ui.profile.ProfileStore;
import com.aptana.ui.ContributedExtension;
import com.aptana.ui.preferences.IPreferencesLookupDelegate;
import com.aptana.ui.preferences.IPreferencesSaveDelegate;
import com.aptana.ui.preferences.PreferenceKey;

/**
 * Abstract base class for the {@link IScriptFormatterFactory} implementations.
 */
public abstract class AbstractScriptFormatterFactory extends ContributedExtension implements IScriptFormatterFactory
{

	protected IProfileVersioner versioner;

	protected String getDefaultProfileID()
	{
		StringBuffer buffer = new StringBuffer();
		String lang = getLanguage();
		if (lang != null && lang.length() > 0)
		{
			buffer.append("org.eclipse.dltk."); //$NON-NLS-1$
			buffer.append(lang.toLowerCase());
		}
		else
		{
			buffer.append(getClass().getName());
		}

		buffer.append(".formatter.profiles.default"); //$NON-NLS-1$
		return buffer.toString();
	}

	protected String getDefaultProfileName()
	{
		return FormatterMessages.AbstractScriptFormatterFactory_defaultProfileName;
	}

	public List<IProfile> getBuiltInProfiles()
	{
		List<IProfile> profiles = new ArrayList<IProfile>();

		IProfileVersioner versioner = getProfileVersioner();
		BuiltInProfile profile = new BuiltInProfile(getDefaultProfileID(), getDefaultProfileName(),
				loadDefaultSettings(), 1, getId(), versioner.getCurrentVersion());

		profiles.add(profile);
		return profiles;
	}

	protected PreferenceKey getProfilesKey()
	{
		return null;
	}

	public List<IProfile> getCustomProfiles()
	{
		final PreferenceKey profilesKey = getProfilesKey();
		if (profilesKey != null)
		{
			final String profilesSource = profilesKey.getStoredValue(new InstanceScope());
			if (profilesSource != null && profilesSource.length() > 0)
			{
				final IProfileStore store = getProfileStore();
				try
				{
					return ((ProfileStore) store).readProfilesFromString(profilesSource);
				}
				catch (CoreException e)
				{
					FormatterPlugin.logError(e);
				}
			}
		}
		return Collections.emptyList();
	}

	public void saveCustomProfiles(List<IProfile> profiles)
	{
		final PreferenceKey profilesKey = getProfilesKey();
		if (profilesKey != null)
		{
			final IProfileStore store = getProfileStore();
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

	public Map<String, String> loadDefaultSettings()
	{
		Map<String, String> settings = new HashMap<String, String>();
		PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null)
		{
			DefaultScope scope = new DefaultScope();
			for (int i = 0; i < keys.length; i++)
			{
				PreferenceKey key = keys[i];
				String name = key.getName();
				IEclipsePreferences preferences = scope.getNode(key.getQualifier());
				String value = preferences.get(name, null);
				if (value != null)
					settings.put(name, value);
			}
		}
		return settings;
	}

	public Map<String, String> retrievePreferences(IPreferencesLookupDelegate delegate)
	{
		final PreferenceKey activeProfileKey = getActiveProfileKey();
		if (activeProfileKey != null)
		{
			final String profileId = delegate.getString(activeProfileKey.getQualifier(), activeProfileKey.getName());
			if (profileId != null && profileId.length() != 0)
			{
				for (IProfile profile : getBuiltInProfiles())
				{
					if (profileId.equals(profile.getID()))
					{
						return profile.getSettings();
					}
				}
				for (IProfile profile : getCustomProfiles())
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

		final PreferenceKey activeProfileKey = getActiveProfileKey();
		if (activeProfileKey != null && preferences.containsKey(activeProfileKey.getName()))
		{
			final String value = preferences.get(activeProfileKey.getName());
			delegate.setString(activeProfileKey.getQualifier(), activeProfileKey.getName(), value);
		}
	}

	public IProfileVersioner getProfileVersioner()
	{
		if (versioner == null)
			versioner = createProfileVersioner();
		return versioner;
	}

	public IProfileStore getProfileStore()
	{
		return new ProfileStore(getProfileVersioner(), loadDefaultSettings());
	}

	protected IProfileVersioner createProfileVersioner()
	{
		return new GeneralProfileVersioner(getId());
	}

	/*
	 * @see IScriptFormatterFactory#createProfileManager(java.util.List)
	 */
	public IProfileManager createProfileManager(List<IProfile> profiles)
	{
		return new ProfileManager(profiles);
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

	private String getLanguage()
	{
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager.getLanguageToolkit(getContentType());
		if (toolkit != null)
			return toolkit.getLanguageName();
		return null;
	}
}
