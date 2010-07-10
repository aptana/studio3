/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.ui.preferences.formatter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.xml.sax.InputSource;

import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.preferences.PreferencesAccess;
import com.aptana.ui.preferences.formatter.ProfileManager.CustomProfile;
import com.aptana.ui.preferences.formatter.ProfileManager.Profile;



/**
 * 
 *
 */
public class FormatterProfileStore extends ProfileStore {

	/**
	 * Preference key where all profiles are stored
	 */
	private static final String PREF_FORMATTER_PROFILES= "org.eclipse.jdt.ui.formatterprofiles"; //$NON-NLS-1$
	
	

	private String pluginID;
		
	/**
	 * @param pluginId 
	 * 
	 */
	public FormatterProfileStore(String pluginId) {
		super(PREF_FORMATTER_PROFILES,pluginId);
		this.pluginID=pluginId;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	public List<Profile> readProfiles(IScopeContext scope) throws CoreException {
	    List<Profile> profiles= super.readProfiles(scope);
	    if (profiles == null) {
			profiles= readOldForCompatibility(scope);
		}
	    return profiles;
	}

	/**
	 * Read the available profiles from the internal XML file and return them
	 * as collection.
	 * @return returns a list of <code>CustomProfile</code> or <code>null</code>
	 */
	private List<Profile> readOldForCompatibility(IScopeContext instanceScope) {
		
		// in 3.0 M9 and less the profiles were stored in a file in the plugin's meta data
		final String STORE_FILE= "code_formatter_profiles.xml"; //$NON-NLS-1$

		File file= UIEplPlugin.getDefault().getStateLocation().append(STORE_FILE).toFile();
		if (!file.exists())
			return null;
		
		try {
			// note that it's wrong to use a file reader when XML declares UTF-8: Kept for compatibility
			final FileReader reader= new FileReader(file);
			try {
				List<Profile> res= readProfilesFromStream(new InputSource(reader));
				if (res != null) {
					
					writeProfiles(res, instanceScope);
				}
				file.delete(); // remove after successful write
				return res;
			} finally {
				reader.close();
			}
		} catch (CoreException e) {
			UIEplPlugin.getDefault().getLog().log(e.getStatus());
		} catch (IOException e) {
			UIEplPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, UIEplPlugin.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}
	
	
	/**
	 * 
	 */
	public void checkCurrentOptionsVersion() {
		PreferencesAccess access= PreferencesAccess.getOriginalPreferences();
		
		IScopeContext instanceScope= access.getInstanceScope();
		IEclipsePreferences uiPreferences= instanceScope.getNode(pluginID);
		
		
		try {
			List<Profile> profiles= (new FormatterProfileStore(this.pluginID)).readProfiles(instanceScope);
			if (profiles == null) {
				profiles= new ArrayList<Profile>();
			}
			ProfileManager manager= new FormatterProfileManager(profiles, instanceScope, access,pluginID);
			if (manager.getSelected() instanceof CustomProfile) {
				manager.commitChanges(instanceScope); // updates JavaCore options
			}
			uiPreferences.putInt(PREF_FORMATTER_PROFILES + VERSION_KEY_SUFFIX, 1);
			savePreferences(instanceScope);
						
			IProject[] projects= ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i= 0; i < projects.length; i++) {
				IScopeContext scope= access.getProjectScope(projects[i]);
				if (manager.hasProjectSpecificSettings(scope)) {
					manager= new FormatterProfileManager(profiles, scope, access,pluginID);
					manager.commitChanges(scope); // updates JavaCore project options
					savePreferences(scope);
				}
			}
		} catch (CoreException e) {
			UIEplPlugin.getDefault().getLog().log(e.getStatus());
		} catch (BackingStoreException e) {
			UIEplPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, UIEplPlugin.PLUGIN_ID, e.getMessage(), e));
		}
	}
	
	private  void savePreferences(final IScopeContext context) throws BackingStoreException {
		try {
			context.getNode(pluginID).flush();
		} finally {
			context.getNode(pluginID).flush();
		}
	}
}
