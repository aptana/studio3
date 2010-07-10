/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.ui.preferences.PreferencesAccess;

/**
 * The model for the set of profiles which are available in the workbench.
 */
public abstract class ProfileManager extends Observable
{
	public static final class KeySet
	{

		private final List<String> fKeys;
		private final String fNodeName;

		/**
		 * @param nodeName
		 * @param keys
		 */
		public KeySet(String nodeName, List<String> keys)
		{
			fNodeName = nodeName;
			fKeys = keys;
		}

		/**
		 * @return String
		 */
		public String getNodeName()
		{
			return fNodeName;
		}

		/**
		 * @return List
		 */
		public List<String> getKeys()
		{
			return fKeys;
		}
	}

	/**
	 * A prefix which is prepended to every ID of a user-defined profile, in order to differentiate it from a built-in
	 * profile.
	 */
	public final static String ID_PREFIX = "_"; //$NON-NLS-1$

	/**
	 * Represents a profile with a unique ID, a name and a map containing the code formatter settings.
	 */
	public static abstract class Profile implements Comparable<Profile>
	{

		/**
		 * @return String
		 */
		public abstract String getName();

		/**
		 * @param name
		 * @param manager
		 * @return Profile
		 */
		public abstract Profile rename(String name, ProfileManager manager);

		/**
		 * @return Map
		 */
		public abstract Map<String, String> getSettings();

		/**
		 * @param settings
		 */
		public abstract void setSettings(Map<String, String> settings);

		/**
		 * @return version
		 */
		public abstract int getVersion();

		/**
		 * @param otherMap
		 * @param allKeys
		 * @return boolean
		 */
		public boolean hasEqualSettings(Map<String, String> otherMap, List<String> allKeys)
		{
			Map<String, String> settings = getSettings();
			for (Iterator<String> iter = allKeys.iterator(); iter.hasNext();)
			{
				String key = iter.next();
				Object other = otherMap.get(key);
				Object curr = settings.get(key);
				if (other == null)
				{
					if (curr != null)
					{
						return false;
					}
				}
				else if (!other.equals(curr))
				{
					if (curr != null)
						return false;
				}
			}
			return true;
		}

		/**
		 * @return boolean
		 */
		public abstract boolean isProfileToSave();

		/**
		 * @return id
		 */
		public abstract String getID();

		/**
		 * @return is shared
		 */
		public boolean isSharedProfile()
		{
			return false;
		}

		/**
		 * @return is built in
		 */
		public boolean isBuiltInProfile()
		{
			return false;
		}
	}

	/**
	 * Represents a built-in profile. The state of a built-in profile cannot be changed after instantiation.
	 */
	public static final class BuiltInProfile extends Profile
	{
		private final String fName;
		private final String fID;
		private final Map<String, String> fSettings;
		private final int fOrder;
		private final int fCurrentVersion;
		private final String fProfileKind;

		/**
		 * @param ID
		 * @param name
		 * @param settings
		 * @param order
		 * @param currentVersion
		 * @param profileKind
		 */
		public BuiltInProfile(String ID, String name, Map<String, String> settings, int order, int currentVersion,
				String profileKind)
		{
			fName = name;
			fID = ID;
			fSettings = settings;
			fOrder = order;
			fCurrentVersion = currentVersion;
			fProfileKind = profileKind;
		}

		public String getName()
		{
			return fName;
		}

		public Profile rename(String name, ProfileManager manager)
		{
			final String trimmed = name.trim();
			CustomProfile newProfile = new CustomProfile(trimmed, fSettings, fCurrentVersion, fProfileKind);
			manager.addProfile(newProfile);
			return newProfile;
		}

		public Map<String, String> getSettings()
		{
			return fSettings;
		}

		public void setSettings(Map<String, String> settings)
		{
		}

		public String getID()
		{
			return fID;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public final int compareTo(Profile o)
		{
			if (o instanceof BuiltInProfile)
			{
				return fOrder - ((BuiltInProfile) o).fOrder;
			}
			return -1;
		}

		public boolean isProfileToSave()
		{
			return false;
		}

		public boolean isBuiltInProfile()
		{
			return true;
		}

		public int getVersion()
		{
			return fCurrentVersion;
		}

	}

	/**
	 * Represents a user-defined profile. A custom profile can be modified after instantiation.
	 */
	public static class CustomProfile extends Profile
	{
		private String fName;
		private Map<String, String> fSettings;
		/**
		 * 
		 */
		protected ProfileManager fManager;
		private int fVersion;
		private final String fKind;

		/**
		 * @param name
		 * @param settings
		 * @param version
		 * @param kind
		 */
		public CustomProfile(String name, Map<String, String> settings, int version, String kind)
		{
			fName = name;
			fSettings = settings;
			fVersion = version;
			fKind = kind;
		}

		public String getName()
		{
			return fName;
		}

		public Profile rename(String name, ProfileManager manager)
		{
			final String trimmed = name.trim();
			if (trimmed.equals(getName()))
				return this;

			String oldID = getID(); // remember old id before changing name
			fName = trimmed;

			manager.profileRenamed(this, oldID);
			return this;
		}

		public Map<String, String> getSettings()
		{
			return fSettings;
		}

		public void setSettings(Map<String, String> settings)
		{
			if (settings == null)
				throw new IllegalArgumentException();
			fSettings = settings;
			if (fManager != null)
			{
				fManager.profileChanged(this);
			}
		}

		public String getID()
		{
			return ID_PREFIX + fName;
		}

		/**
		 * @param profileManager
		 */
		public void setManager(ProfileManager profileManager)
		{
			fManager = profileManager;
		}

		/**
		 * @return ProfileManager
		 */
		public ProfileManager getManager()
		{
			return fManager;
		}

		public int getVersion()
		{
			return fVersion;
		}

		/**
		 * @param version
		 */
		public void setVersion(int version)
		{
			fVersion = version;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Profile o)
		{
			if (o instanceof SharedProfile)
			{
				return -1;
			}
			if (o instanceof CustomProfile)
			{
				return getName().compareToIgnoreCase(((Profile) o).getName());
			}
			return 1;
		}

		public boolean isProfileToSave()
		{
			return true;
		}

		/**
		 * @return String
		 */
		public String getKind()
		{
			return fKind;
		}

	}

	/**
	 * 
	 *
	 */
	public final class SharedProfile extends CustomProfile
	{

		/**
		 * @param oldName
		 * @param options
		 * @param version
		 * @param profileKind
		 */
		public SharedProfile(String oldName, Map<String, String> options, int version, String profileKind)
		{
			super(oldName, options, version, profileKind);
		}

		public Profile rename(String name, ProfileManager manager)
		{
			CustomProfile profile = new CustomProfile(name.trim(), getSettings(), getVersion(), getKind());

			manager.profileReplaced(this, profile);
			return profile;
		}

		public String getID()
		{
			return SHARED_PROFILE;
		}

		public final int compareTo(Profile o)
		{
			return 1;
		}

		public boolean isProfileToSave()
		{
			return false;
		}

		public boolean isSharedProfile()
		{
			return true;
		}
	}

	/**
	 * The possible events for observers listening to this class.
	 */
	public final static int SELECTION_CHANGED_EVENT = 1;
	/**
	 * 
	 */
	public final static int PROFILE_DELETED_EVENT = 2;
	/**
	 * 
	 */
	public final static int PROFILE_RENAMED_EVENT = 3;
	/**
	 * 
	 */
	public final static int PROFILE_CREATED_EVENT = 4;
	/**
	 * 
	 */
	public final static int SETTINGS_CHANGED_EVENT = 5;

	/**
	 * The key of the preference where the selected profile is stored.
	 */
	private final String fProfileKey;

	/**
	 * The key of the preference where the version of the current settings is stored
	 */
	private final String fProfileVersionKey;

	private final static String SHARED_PROFILE = "org.eclipse.jdt.ui.default.shared"; //$NON-NLS-1$

	/**
	 * A map containing the available profiles, using the IDs as keys.
	 */
	private final Map<String, Profile> fProfiles;

	/**
	 * The available profiles, sorted by name.
	 */
	private final List<Profile> fProfilesByName;

	/**
	 * The currently selected profile.
	 */
	private Profile fSelected;

	/**
	 * The keys of the options to be saved with each profile
	 */
	private final KeySet[] fKeySets;
	/**
	 * 
	 */
	protected String pluginId;
	private final PreferencesAccess fPreferencesAccess;

	/**
	 * Create and initialize a new profile manager.
	 * 
	 * @param profiles
	 *            Initial custom profiles (List of type <code>CustomProfile</code>)
	 * @param context
	 * @param preferencesAccess
	 * @param keySets
	 * @param profileKey
	 * @param profileVersionKey
	 * @param pluginId
	 */
	public ProfileManager(List<Profile> profiles, IScopeContext context, PreferencesAccess preferencesAccess,
			KeySet[] keySets, String profileKey, String profileVersionKey, String pluginId)
	{
		this.pluginId = pluginId;
		fPreferencesAccess = preferencesAccess;
		fKeySets = keySets;
		fProfileKey = profileKey;
		fProfileVersionKey = profileVersionKey;

		fProfiles = new HashMap<String, Profile>();
		fProfilesByName = new ArrayList<Profile>();

		for (final Iterator<Profile> iter = profiles.iterator(); iter.hasNext();)
		{
			final Profile profile = iter.next();
			if (profile instanceof CustomProfile)
			{
				((CustomProfile) profile).setManager(this);
			}
			fProfiles.put(profile.getID(), profile);
			fProfilesByName.add(profile);
		}

		Collections.sort(fProfilesByName);

		String profileId = getSelectedProfileId(fPreferencesAccess.getInstanceScope());

		Profile profile = (Profile) fProfiles.get(profileId);
		if (profile == null)
		{
			profile = getDefaultProfile();
		}
		fSelected = profile;

		if (context.getName() == ProjectScope.SCOPE && hasProjectSpecificSettings(context))
		{
			Map<String, String> map = readFromPreferenceStore(context, profile);
			if (map != null)
			{

				List<String> allKeys = new ArrayList<String>();
				for (int i = 0; i < fKeySets.length; i++)
				{
					allKeys.addAll(fKeySets[i].getKeys());
				}
				Collections.sort(allKeys);

				Profile matching = null;

				String projProfileId = context.getNode(pluginId).get(fProfileKey, null);
				if (projProfileId != null)
				{
					Profile curr = (Profile) fProfiles.get(projProfileId);
					if (curr != null && (curr.isBuiltInProfile() || curr.hasEqualSettings(map, allKeys)))
					{
						matching = curr;
					}
				}
				else
				{
					// old version: look for similar
					for (final Iterator<Profile> iter = fProfilesByName.iterator(); iter.hasNext();)
					{
						Profile curr = iter.next();
						if (curr.hasEqualSettings(map, allKeys))
						{
							matching = curr;
							break;
						}
					}
				}
				if (matching == null)
				{
					String name;
					if (projProfileId != null && !fProfiles.containsKey(projProfileId))
					{
						name = MessageFormat.format(FormatterMessages.ProfileManager_unmanaged_profile_with_name,
								projProfileId.substring(ID_PREFIX.length()));
					}
					else
					{
						name = FormatterMessages.ProfileManager_unmanaged_profile;
					}
					// current settings do not correspond to any profile -> create a 'team' profile
					SharedProfile shared = new SharedProfile(name, map, 1, ""); //$NON-NLS-1$
					shared.setManager(this);
					fProfiles.put(shared.getID(), shared);
					fProfilesByName.add(shared); // add last
					matching = shared;
				}
				fSelected = matching;
			}
		}
	}

	/**
	 * @param instanceScope
	 * @return String
	 */
	protected String getSelectedProfileId(IScopeContext instanceScope)
	{
		String profileId = instanceScope.getNode(pluginId).get(fProfileKey, null);
		if (profileId == null)
		{
			// request from bug 129427
			profileId = new DefaultScope().getNode(pluginId).get(fProfileKey, null);
		}
		return profileId;
	}

	/**
	 * Notify observers with a message. The message must be one of the following:
	 * 
	 * @param message
	 *            Message to send out
	 * @see #SELECTION_CHANGED_EVENT
	 * @see #PROFILE_DELETED_EVENT
	 * @see #PROFILE_RENAMED_EVENT
	 * @see #PROFILE_CREATED_EVENT
	 * @see #SETTINGS_CHANGED_EVENT
	 */
	protected void notifyObservers(int message)
	{
		setChanged();
		notifyObservers(new Integer(message));
	}

	/**
	 * @param context
	 * @param keySets
	 * @return value
	 */
	public boolean hasProjectSpecificSettings(IScopeContext context, KeySet[] keySets)
	{
		for (int i = 0; i < keySets.length; i++)
		{
			KeySet keySet = keySets[i];
			IEclipsePreferences preferences = context.getNode(pluginId);
			for (final Iterator<String> keyIter = keySet.getKeys().iterator(); keyIter.hasNext();)
			{
				final String key = keyIter.next();
				Object val = preferences.get(key, null);
				if (val != null)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param context
	 * @return value
	 */
	public boolean hasProjectSpecificSettings(IScopeContext context)
	{
		return hasProjectSpecificSettings(context, fKeySets);
	}

	/**
	 * Only to read project specific settings to find out to what profile it matches.
	 * 
	 * @param context
	 *            The project context
	 */
	private Map<String, String> readFromPreferenceStore(IScopeContext context, Profile workspaceProfile)
	{
		final Map<String, String> profileOptions = new HashMap<String, String>();
		boolean hasValues = false;
		for (int i = 0; i < fKeySets.length; i++)
		{
			KeySet keySet = fKeySets[i];
			IEclipsePreferences preferences = context.getNode(pluginId);
			for (final Iterator<String> keyIter = keySet.getKeys().iterator(); keyIter.hasNext();)
			{
				final String key = (String) keyIter.next();
				String val = preferences.get(key, null);
				if (val != null)
				{
					hasValues = true;
				}
				else
				{
					val = workspaceProfile.getSettings().get(key);
				}
				profileOptions.put(key, val);
			}
		}

		if (!hasValues)
		{
			return null;
		}
		setLatestCompliance(profileOptions);
		return profileOptions;
	}

	private boolean updatePreferences(IEclipsePreferences prefs, List<String> keys, Map<String, String> profileOptions)
	{
		boolean hasChanges = false;
		for (final Iterator<String> keyIter = keys.iterator(); keyIter.hasNext();)
		{
			final String key = keyIter.next();
			final String oldVal = prefs.get(key, null);
			final String val = (String) profileOptions.get(key);
			if (val == null)
			{
				if (oldVal != null)
				{
					prefs.remove(key);
					hasChanges = true;
				}
			}
			else if (!val.equals(oldVal))
			{
				prefs.put(key, val);
				hasChanges = true;
			}
		}
		return hasChanges;
	}

	/**
	 * Update all formatter settings with the settings of the specified profile.
	 * 
	 * @param profile
	 *            The profile to write to the preference store
	 */
	private void writeToPreferenceStore(Profile profile, IScopeContext context)
	{
		final Map<String, String> profileOptions = profile.getSettings();
		IEclipsePreferences node = context.getNode(pluginId);
		final IEclipsePreferences uiPrefs = node;
		for (int i = 0; i < fKeySets.length; i++)
		{
			updatePreferences(node, fKeySets[i].getKeys(), profileOptions);
		}

		Iterator<String> iterator = profileOptions.keySet().iterator();
		while (iterator.hasNext())
		{
			String key = iterator.next();
			Object object = profileOptions.get(key);
			if (object != null)
			{
				String string = object.toString();
				uiPrefs.put(key, string);
			}
		}
		if (uiPrefs.getInt(fProfileVersionKey, 0) != 1)
		{
			uiPrefs.putInt(fProfileVersionKey, 1);
		}

		if (context.getName() == InstanceScope.SCOPE)
		{
			uiPrefs.put(fProfileKey, profile.getID());
		}
		else if (context.getName() == ProjectScope.SCOPE && !profile.isSharedProfile())
		{
			uiPrefs.put(fProfileKey, profile.getID());
		}
	}

	/**
	 * Get an immutable list as view on all profiles, sorted alphabetically. Unless the set of profiles has been
	 * modified between the two calls, the sequence is guaranteed to correspond to the one returned by
	 * <code>getSortedNames</code>.
	 * 
	 * @return a list of elements of type <code>Profile</code>
	 * @see #getSortedDisplayNames()
	 */
	public List<Profile> getSortedProfiles()
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
		for (final Iterator<Profile> iter = fProfilesByName.iterator(); iter.hasNext();)
		{
			Profile curr = iter.next();
			sortedNames[i++] = curr.getName();
		}
		return sortedNames;
	}

	/**
	 * Get the profile for this profile id.
	 * 
	 * @param ID
	 *            The profile ID
	 * @return The profile with the given ID or <code>null</code>
	 */
	public Profile getProfile(String ID)
	{
		return (Profile) fProfiles.get(ID);
	}

	/**
	 * Activate the selected profile, update all necessary options in preferences and save profiles to disk.
	 * 
	 * @param scopeContext
	 */
	public void commitChanges(IScopeContext scopeContext)
	{
		if (fSelected != null)
		{
			writeToPreferenceStore(fSelected, scopeContext);
		}
	}

	/**
	 * @param context
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
	public void clearAllSettings(IScopeContext context)
	{
		for (int i = 0; i < fKeySets.length; i++)
		{
			updatePreferences(context.getNode(pluginId), fKeySets[i].getKeys(), Collections.EMPTY_MAP);
		}

		final IEclipsePreferences uiPrefs = context.getNode(this.pluginId);
		uiPrefs.remove(fProfileKey);
	}

	/**
	 * Get the currently selected profile.
	 * 
	 * @return The currently selected profile.
	 */
	public Profile getSelected()
	{
		return fSelected;
	}

	/**
	 * Set the selected profile. The profile must already be contained in this profile manager.
	 * 
	 * @param profile
	 *            The profile to select
	 */
	public void setSelected(Profile profile)
	{
		final Profile newSelected = (Profile) fProfiles.get(profile.getID());
		if (newSelected != null && !newSelected.equals(fSelected))
		{
			fSelected = newSelected;
			notifyObservers(SELECTION_CHANGED_EVENT);
		}
	}

	/**
	 * Check whether a user-defined profile in this profile manager already has this name.
	 * 
	 * @param name
	 *            The name to test for
	 * @return Returns <code>true</code> if a profile with the given name exists
	 */
	public boolean containsName(String name)
	{
		for (final Iterator<Profile> iter = fProfilesByName.iterator(); iter.hasNext();)
		{
			Profile curr = iter.next();
			if (name.equals(curr.getName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a new custom profile to this profile manager.
	 * 
	 * @param profile
	 *            The profile to add
	 */
	public void addProfile(CustomProfile profile)
	{
		profile.setManager(this);
		final CustomProfile oldProfile = (CustomProfile) fProfiles.get(profile.getID());
		if (oldProfile != null)
		{
			fProfiles.remove(oldProfile.getID());
			fProfilesByName.remove(oldProfile);
			oldProfile.setManager(null);
		}
		fProfiles.put(profile.getID(), profile);
		fProfilesByName.add(profile);
		Collections.sort(fProfilesByName);
		fSelected = profile;
		notifyObservers(PROFILE_CREATED_EVENT);
	}

	/**
	 * Delete the currently selected profile from this profile manager. The next profile in the list is selected.
	 * 
	 * @return true if the profile has been successfully removed, false otherwise.
	 */
	public boolean deleteSelected()
	{
		if (!(fSelected instanceof CustomProfile))
			return false;

		return deleteProfile((CustomProfile) fSelected);
	}

	/**
	 * @param profile
	 * @return value
	 */
	public boolean deleteProfile(CustomProfile profile)
	{
		int index = fProfilesByName.indexOf(profile);

		fProfiles.remove(profile.getID());
		fProfilesByName.remove(profile);

		profile.setManager(null);

		if (index >= fProfilesByName.size())
			index--;
		fSelected = (Profile) fProfilesByName.get(index);

		if (!profile.isSharedProfile())
		{
			updateProfilesWithName(profile.getID(), null, false);
		}

		notifyObservers(PROFILE_DELETED_EVENT);
		return true;
	}

	/**
	 * @param profile
	 * @param oldID
	 */
	public void profileRenamed(CustomProfile profile, String oldID)
	{
		fProfiles.remove(oldID);
		fProfiles.put(profile.getID(), profile);

		if (!profile.isSharedProfile())
		{
			updateProfilesWithName(oldID, profile, false);
		}

		Collections.sort(fProfilesByName);
		notifyObservers(PROFILE_RENAMED_EVENT);
	}

	/**
	 * @param oldProfile
	 * @param newProfile
	 */
	public void profileReplaced(CustomProfile oldProfile, CustomProfile newProfile)
	{
		fProfiles.remove(oldProfile.getID());
		fProfiles.put(newProfile.getID(), newProfile);
		fProfilesByName.remove(oldProfile);
		fProfilesByName.add(newProfile);
		Collections.sort(fProfilesByName);

		if (!oldProfile.isSharedProfile())
		{
			updateProfilesWithName(oldProfile.getID(), null, false);
		}

		setSelected(newProfile);
		notifyObservers(PROFILE_CREATED_EVENT);
		notifyObservers(SELECTION_CHANGED_EVENT);
	}

	/**
	 * @param profile
	 */
	public void profileChanged(CustomProfile profile)
	{
		if (!profile.isSharedProfile())
		{
			updateProfilesWithName(profile.getID(), profile, true);
		}

		notifyObservers(SETTINGS_CHANGED_EVENT);
	}

	/**
	 * @param oldName
	 * @param newProfile
	 * @param applySettings
	 */
	protected void updateProfilesWithName(String oldName, Profile newProfile, boolean applySettings)
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++)
		{
			IScopeContext projectScope = fPreferencesAccess.getProjectScope(projects[i]);
			IEclipsePreferences node = projectScope.getNode(pluginId);
			String profileId = node.get(fProfileKey, null);
			if (oldName.equals(profileId))
			{
				if (newProfile == null)
				{
					node.remove(fProfileKey);
				}
				else
				{
					if (applySettings)
					{
						writeToPreferenceStore(newProfile, projectScope);
					}
					else
					{
						node.put(fProfileKey, newProfile.getID());
					}
				}
			}
		}

		IScopeContext instanceScope = fPreferencesAccess.getInstanceScope();
		final IEclipsePreferences uiPrefs = instanceScope.getNode(pluginId);
		if (newProfile != null && oldName.equals(uiPrefs.get(fProfileKey, null)))
		{
			writeToPreferenceStore(newProfile, instanceScope);
		}
	}

	private static void setLatestCompliance(Map<String, String> map)
	{
		// JavaModelUtil.set50CompilanceOptions(map);
	}

	/**
	 * @return default Profile
	 */
	public abstract Profile getDefaultProfile();

}
