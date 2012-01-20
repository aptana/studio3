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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.core.logging.IdeLog;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.preferences.IPreferencesSaveDelegate;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.preferences.PreferencesLookupDelegate;
import com.aptana.formatter.preferences.profile.IProfile;
import com.aptana.formatter.preferences.profile.ProfileManager;
import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * Abstract base class for the {@link IScriptFormatterFactory} implementations.
 */
public abstract class AbstractScriptFormatterFactory extends ContributedExtension implements IScriptFormatterFactory
{
	private static final String USE_GLOBAL_DEFAULTS_KEY = "com.aptana.editor.common.useGlobalDefaults"; //$NON-NLS-1$
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

	public Map<String, String> retrievePreferences(PreferencesLookupDelegate delegate)
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
			else
			{
				List<IProfile> builtInProfiles = profileManager.getBuiltInProfiles();
				if (builtInProfiles != null && !builtInProfiles.isEmpty())
				{
					return builtInProfiles.get(0).getSettings();
				}
			}
		}
		// Last resort - Collect only this formatters' preferences.
		final Map<String, String> result = new HashMap<String, String>();
		final PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null)
		{
			for (PreferenceKey prefKey : keys)
			{
				final String key = prefKey.getName();
				result.put(key, delegate.getString(prefKey.getQualifier(), key));
			}
		}
		return result;
	}

	public void savePreferences(Map<String, String> preferences, IPreferencesSaveDelegate delegate)
	{
		savePreferences(preferences, delegate, false);
	}

	/**
	 * Save the preferences.<br>
	 * Node that the given preferences Map may be modified during that save in order to adjust the settings to other
	 * variables, such as the editor's tab-size.
	 */
	public void savePreferences(Map<String, String> preferences, IPreferencesSaveDelegate delegate,
			boolean isInitializing)
	{
		// Handle Tab-Size setting here. This settings will immediately effect all the editors of the kind we are
		// formatting. Since this method is also called when we load the preferences, we make sure we don't set anything
		// ahead of time.
		// The update may fix the tab-size in the preferences to fit the editor's one, in case isInitializing is on.
		updateEditorTabSize(preferences, isInitializing);

		// Save the preferences.
		final PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null)
		{
			for (PreferenceKey prefKey : keys)
			{
				final String key = prefKey.getName();
				if (preferences.containsKey(key))
				{
					final String value = preferences.get(key);
					delegate.setString(prefKey.getQualifier(), key, value);
				}
			}
		}
	}

	/**
	 * Handle Tab-Size setting here. This settings will immediately effect all the editors of the kind we are
	 * formatting. Since this method is also called when we load the preferences, we make sure we don't set anything
	 * ahead of time.
	 * <ul>
	 * <li>In case the formatter settings indicate to use tabs - set the editor's prefs to tabs.</li>
	 * <li>In case the settings indicate spaces, or mixed - set the editor's prefs to spaces.</li>
	 * <li>When the workspace defaults settings match the formatter's settings, the editor preferences will indicate it.
	 * </li>
	 * </ul>
	 * 
	 * @param preferences
	 * @param isInitializing
	 */
	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#updateEditorTabSize(java.util.Map, boolean)
	 */
	protected void updateEditorTabSize(Map<String, String> preferences, boolean isInitializing)
	{
		IEclipsePreferences prefs = getEclipsePreferences();
		int editorTabSize = getEditorTabSize();
		if (CodeFormatterConstants.EDITOR.equals(getFormatterTabPolicy(preferences)))
		{
			// In case the formatter defines to follow the Editor's Tab-Policy, just update the preferences Map with the
			// editor-tab-size.
			preferences.put(getFormatterTabSizeKey(), String.valueOf(editorTabSize));
		}
		else
		{
			String prefTabSize = preferences.get(getFormatterTabSizeKey());
			int selectedTabValue = (prefTabSize != null) ? Integer.parseInt(prefTabSize) : 0;
			if (selectedTabValue == editorTabSize)
			{
				if (selectedTabValue == getDefaultEditorTabSize())
				{
					// Set the editor's settings to the default one.
					prefs.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
					prefs.putBoolean(USE_GLOBAL_DEFAULTS_KEY, true);
				}
			}
			else
			{
				if (isInitializing)
				{
					// fix the preferences value
					selectedTabValue = editorTabSize;
					preferences.put(getFormatterTabSizeKey(), String.valueOf(selectedTabValue));
				}
				prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, selectedTabValue);
				prefs.putBoolean(USE_GLOBAL_DEFAULTS_KEY, selectedTabValue == getDefaultEditorTabSize());
			}
			try
			{
				prefs.flush();
			}
			catch (Exception e)
			{
				IdeLog.logError(FormatterPlugin.getDefault(), e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#isValid()
	 */
	public boolean isValid()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPreviewContent()
	 */
	public URL getPreviewContent()
	{
		return null;
	}

	/**
	 * By default, return true.<br>
	 * Subclasses may override.
	 */
	public boolean isContributingToUI()
	{
		return true;
	}

	/**
	 * By default, return true.<br>
	 * Subclasses may override.
	 * 
	 * @see com.aptana.formatter.IScriptFormatterFactory#canConsumePreviousIndent()
	 */
	public boolean canConsumePreviousIndent()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.IScriptFormatterFactory#updateProfile(com.aptana.formatter.preferences.profile.IProfile)
	 */
	public void updateProfile(IProfile profile)
	{
		int tabSize = getEditorTabSize();
		Map<String, String> settings = profile.getSettings();
		settings.put(getFormatterTabSizeKey(), Integer.toString(tabSize));
		profile.setSettings(settings);
	}

	/**
	 * Returns the formatter key for the tab size.
	 * 
	 * @return A string key for the tab-size defined in the formatter.
	 */
	protected abstract String getFormatterTabSizeKey();

	/**
	 * Returns the editor's tab-size. The editor is the main one used for this formatter's content type.<br>
	 * Subclasses of this class should implement this method to avoid any plugins dependency issues.
	 * 
	 * @return The editor's tab-size.
	 */
	protected abstract int getEditorTabSize();

	/**
	 * Returns the editor's tab default size. The editor is the main one used for this formatter's content type.<br>
	 * Subclasses of this class should implement this method to avoid any plugins dependency issues.
	 * 
	 * @return The editor's default tab-size.
	 */
	protected abstract int getDefaultEditorTabSize();

	/**
	 * Returns an {@link IEclipsePreferences}
	 */
	protected abstract IEclipsePreferences getEclipsePreferences();

	/**
	 * Returns the formatter's tab policy (as defined at the {@link CodeFormatterConstants}).
	 * 
	 * @param preferences
	 * @return
	 */
	protected abstract String getFormatterTabPolicy(Map<String, String> preferences);
}
