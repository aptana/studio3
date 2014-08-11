/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.formatter.IContributedExtension;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.FormatterMessages;

/**
 * The model for the set of profiles which are available in the workbench.<br>
 */
public class ProfileManager implements IProfileManager
{

	private static final String APTANA_CODE_FORMATTER_ID = "aptana.code.formatter"; //$NON-NLS-1$
	private static final String DEFAULT_PROFILE_ID = "com.aptana.formatter.profiles.default"; //$NON-NLS-1$
	private static final String ACTIVE_PROFILE_ID = "com.aptana.formatter.profiles.active"; //$NON-NLS-1$

	private static ProfileManager instance;

	/**
	 * A map containing the available profiles, using the IDs as keys.
	 */
	private final Map<String, IProfile> fProfiles;
	/**
	 * The available profiles, sorted by name.
	 */
	private final List<IProfile> fProfilesByName;
	private ListenerList listeners;
	/**
	 * The currently selected profile.
	 */
	private Map<IProject, IProfile> fSelected;
	private boolean fDirty = false;
	private IProfileVersioner versioner;
	private PreferenceKey[] preferenceKeys;

	/**
	 * Returns a single instance of the {@link ProfileManager}.<br>
	 * As long as the {@link #dispose()} method was not called, consequent calls to this method will return the same
	 * instance. In case the {@link #dispose()} was called, the next call for this method will return a new instance of
	 * the ProfileManager.
	 * 
	 * @return A single instance of a profile manager.
	 * @see #dispose()
	 */
	public static ProfileManager getInstance()
	{
		if (instance == null)
		{
			synchronized (ProfileManager.class)
			{
				if (instance == null)
				{
					instance = new ProfileManager();
				}
			}
		}
		return instance;
	}

	/**
	 * Dispose the current instance of the ProfileManager.
	 * 
	 * @see #getInstance()
	 */
	public static void dispose()
	{
		synchronized (ProfileManager.class)
		{
			instance = null;
		}
	}

	/**
	 * Create and initialize a new profile manager.
	 * 
	 * @param profiles
	 *            Initial custom profiles (List of type <code>CustomProfile</code>)
	 * @param profileVersioner
	 */
	private ProfileManager()
	{
		List<IProfile> profiles = new ArrayList<IProfile>();
		List<IProfile> builtInProfiles = getBuiltInProfiles();
		if (builtInProfiles != null && builtInProfiles.size() > 0)
		{
			profiles.addAll(builtInProfiles);
		}
		else
		{
			IdeLog.logError(FormatterPlugin.getDefault(), NLS.bind(
					FormatterMessages.AbstractFormatterSelectionBlock_noBuiltInProfiles, APTANA_CODE_FORMATTER_ID),
					IDebugScopes.DEBUG);
		}
		profiles.addAll(getCustomProfiles());

		fProfiles = new HashMap<String, IProfile>();
		fSelected = new HashMap<IProject, IProfile>();
		fProfilesByName = new ArrayList<IProfile>();
		for (final IProfile profile : profiles)
		{
			fProfiles.put(profile.getID(), profile);
			fProfilesByName.add(profile);
		}
		Collections.sort(fProfilesByName);
		if (!fProfilesByName.isEmpty())
		{
			String storedActiveProfile = getActiveProfileKey().getStoredValue(EclipseUtil.instanceScope());
			IProfile workspaceSelectedProfile = fProfiles.get(storedActiveProfile);
			if (workspaceSelectedProfile == null)
			{
				workspaceSelectedProfile = fProfilesByName.get(0);
			}
			fSelected.put(null, workspaceSelectedProfile);
		}
		listeners = new ListenerList();
	}

	private Map<String, String> loadDefaultSettings()
	{
		Map<String, String> settings = new HashMap<String, String>();
		PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null)
		{
			IScopeContext scope = EclipseUtil.defaultScope();
			for (PreferenceKey key : keys)
			{
				String name = key.getName();
				IEclipsePreferences preferences = scope.getNode(key.getQualifier());
				String value = preferences.get(name, null);
				if (value != null)
					settings.put(name, value);
			}
		}
		return settings;
	}

	public List<IProfile> getBuiltInProfiles()
	{
		List<IProfile> profiles = new ArrayList<IProfile>();
		IProfileVersioner versioner = getProfileVersioner();
		BuiltInProfile profile = new BuiltInProfile(getDefaultProfileID(), getDefaultProfileName(),
				loadDefaultSettings(), 1, APTANA_CODE_FORMATTER_ID, versioner.getCurrentVersion());

		profiles.add(profile);
		return profiles;
	}

	public List<IProfile> getCustomProfiles()
	{
		final PreferenceKey profilesKey = getProfilesKey();
		if (profilesKey != null)
		{
			final String profilesSource = profilesKey.getStoredValue(EclipseUtil.instanceScope());
			if (profilesSource != null && profilesSource.length() > 0)
			{
				final IProfileStore store = getProfileStore();
				try
				{
					return ((ProfileStore) store).readProfilesFromString(profilesSource);
				}
				catch (CoreException e)
				{
					IdeLog.logError(FormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
				}
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns the preferences key that will eventually hold the custom formatters settings.
	 * 
	 * @return The preferences key that will hold the custom formatter settings.
	 */
	public PreferenceKey getProfilesKey()
	{
		return new PreferenceKey(FormatterPlugin.PLUGIN_ID, APTANA_CODE_FORMATTER_ID);
	}

	private PreferenceKey[] getPreferenceKeys()
	{
		if (preferenceKeys == null)
		{
			preferenceKeys = collectPreferenceKeys(new ArrayList<IScriptFormatterFactory>(), false);
		}
		return preferenceKeys;
	}

	public IProfileStore getProfileStore()
	{
		return new ProfileStore(getProfileVersioner(), loadDefaultSettings());
	}

	/**
	 * Collects the formatter preferences keys. In case the forceBundleLoading is true, a bundle loading may occur while
	 * collecting the keys.
	 * 
	 * @param factories
	 * @param forceBundleLoading
	 * @return The preferences keys
	 */
	public static PreferenceKey[] collectPreferenceKeys(List<IScriptFormatterFactory> factories,
			boolean forceBundleLoading)
	{
		List<PreferenceKey> result = new ArrayList<PreferenceKey>();
		IContributedExtension[] extensions = ScriptFormatterManager.getInstance().getAllContributions(
				forceBundleLoading);
		Set<Class<? extends IScriptFormatterFactory>> factoriesClasses = new HashSet<Class<? extends IScriptFormatterFactory>>();
		for (IContributedExtension extension : extensions)
		{
			IScriptFormatterFactory factory = (IScriptFormatterFactory) extension;
			if (factory.isContributingToUI() && !factoriesClasses.contains(factory.getClass()))
			{
				factoriesClasses.add(factory.getClass());
				factories.add(factory);
				// result.add(factory.getFormatterPreferenceKey());
				final PreferenceKey[] keys = factory.getPreferenceKeys();
				if (keys != null)
				{
					for (int j = 0; j < keys.length; ++j)
					{
						final PreferenceKey prefKey = keys[j];
						result.add(prefKey);
					}
				}
			}
		}
		return result.toArray(new PreferenceKey[result.size()]);
	}

	/**
	 * Returns the active profile preference key.
	 */
	public PreferenceKey getActiveProfileKey()
	{
		return new PreferenceKey(FormatterPlugin.PLUGIN_ID, ACTIVE_PROFILE_ID);
	}

	/**
	 * Get an immutable list as view on all profiles, sorted alphabetically. Unless the set of profiles has been
	 * modified between the two calls, the sequence is guaranteed to correspond to the one returned by
	 * <code>getSortedNames</code>.
	 * 
	 * @return a list of elements of type <code>Profile</code>
	 * @see #getSortedDisplayNames()
	 */
	public List<IProfile> getSortedProfiles()
	{
		return Collections.unmodifiableList(fProfilesByName);
	}

	/**
	 * Get the names of all profiles stored in this profile manager, sorted alphabetically. Unless the set of profiles
	 * has been modified between the two calls, the sequence is guaranteed to correspond to the one returned by
	 * <code>getSortedProfiles</code>.
	 * 
	 * @return All names, sorted alphabetically
	 * @see #getSortedProfiles()
	 */
	public String[] getSortedDisplayNames()
	{
		final String[] sortedNames = new String[fProfilesByName.size()];
		int i = 0;
		for (IProfile curr : fProfilesByName)
		{
			sortedNames[i++] = curr.getName();
		}
		return sortedNames;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.profile.IProfileManager#getSelected(org.eclipse.core.resources.IProject)
	 */
	public IProfile getSelected(IProject project)
	{
		IProfile selected = fSelected.get(project);
		if (selected == null && project != null)
		{
			// try to resolve the selected profile
			PreferenceKey activeProfileKey = getActiveProfileKey();
			ProjectScope scope = new ProjectScope(project);
			IProfile profile = findProfile(activeProfileKey.getStoredValue(scope));
			if (profile != null)
			{
				fSelected.put(project, profile);
				selected = profile;
			}
			else
			{
				// Return the default workspace setting
				selected = fSelected.get(null);
			}
		}
		return selected;
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener)
	{
		listeners.add(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener)
	{
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.profile.IProfileManager#setSelected(org.eclipse.core.resources.IProject,
	 * com.aptana.formatter.preferences.profile.IProfile)
	 */
	public void setSelected(IProject project, IProfile profile)
	{
		final IProfile newSelected = fProfiles.get(profile.getID());
		IProfile prevSelected = getSelected(project);
		if (newSelected != null && !newSelected.equals(prevSelected))
		{
			PropertyChangeEvent event = new PropertyChangeEvent(this, PROFILE_SELECTED, prevSelected, newSelected);
			fSelected.put(project, newSelected);
			Object[] allListeners = listeners.getListeners();
			for (Object listener : allListeners)
			{
				((IPropertyChangeListener) listener).propertyChange(event);
			}
		}

	}

	public boolean containsName(String name)
	{
		for (IProfile curr : fProfilesByName)
		{
			if (name.equals(curr.getName()))
			{
				return true;
			}
		}
		return false;
	}

	/*
	 * @see IProfileManager#findProfile(java.lang.String)
	 */
	public IProfile findProfile(String profileId)
	{
		return fProfiles.get(profileId);
	}

	public void addProfile(IProject project, IProfile profile)
	{
		if (profile instanceof CustomProfile)
		{
			CustomProfile newProfile = (CustomProfile) profile;
			// newProfile.setManager(this);
			final CustomProfile oldProfile = (CustomProfile) fProfiles.get(profile.getID());
			if (oldProfile != null)
			{
				fProfiles.remove(oldProfile.getID());
				fProfilesByName.remove(oldProfile);
				// oldProfile.setManager(null);
			}
			fProfiles.put(profile.getID(), profile);
			fProfilesByName.add(profile);
			Collections.sort(fProfilesByName);
			fSelected.put(project, newProfile);
			fDirty = true;
		}
	}

	public boolean deleteProfile(IProfile profile)
	{
		if (profile instanceof CustomProfile)
		{
			CustomProfile oldProfile = (CustomProfile) profile;
			int index = fProfilesByName.indexOf(profile);

			fProfiles.remove(oldProfile.getID());
			fProfilesByName.remove(oldProfile);

			// oldProfile.setManager(null);

			if (index >= fProfilesByName.size())
			{
				index--;
			}
			IProject[] projects = fSelected.keySet().toArray(new IProject[fSelected.size()]);
			for (IProject key : projects)
			{
				if (profile.equals(fSelected.get(key)))
				{
					fSelected.remove(key);
					break;
				}
			}

			fDirty = true;

			return true;
		}
		return false;
	}

	public IProfile rename(IProject project, IProfile profile, String newName)
	{
		final String trimmed = newName.trim();
		if (trimmed.equals(profile.getName()))
			return profile;
		if (profile.isBuiltInProfile())
		{
			CustomProfile newProfile = new CustomProfile(trimmed, profile.getSettings(), profile.getVersion());
			addProfile(project, newProfile);
			fDirty = true;
			return newProfile;
		}
		else
		{
			CustomProfile cProfile = (CustomProfile) profile;

			String oldID = profile.getID();
			cProfile.fName = trimmed;

			fProfiles.remove(oldID);
			fProfiles.put(profile.getID(), profile);

			Collections.sort(fProfilesByName);
			fDirty = true;
			return cProfile;
		}
	}

	public IProfile create(IProject project, ProfileKind kind, String profileName, Map<String, String> settings,
			int version)
	{
		CustomProfile profile = new CustomProfile(profileName, settings, version);
		if (kind != ProfileKind.TEMPORARY)
		{
			addProfile(project, profile);
		}
		return profile;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.profile.IProfileManager#isDirty()
	 */
	public boolean isDirty()
	{
		return fDirty;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.profile.IProfileManager#markDirty()
	 */
	public void markDirty()
	{
		fDirty = true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.profile.IProfileManager#clearDirty()
	 */
	public void clearDirty()
	{
		fDirty = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IProfileManager#getProfileVersioner()
	 */
	public IProfileVersioner getProfileVersioner()
	{
		if (versioner == null)
			versioner = createProfileVersioner();
		return versioner;
	}

	/**
	 * Create a new profile versioner.
	 * 
	 * @return A new profile versioner
	 */
	private IProfileVersioner createProfileVersioner()
	{
		return new GeneralProfileVersioner(APTANA_CODE_FORMATTER_ID);
	}

	public String getDefaultProfileID()
	{
		return DEFAULT_PROFILE_ID;
	}

	public String getDefaultProfileName()
	{
		return FormatterMessages.AbstractScriptFormatterFactory_defaultProfileName;
	}
}
