/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.processorDelegates;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.mortbay.util.ajax.JSON;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.StringUtil;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * A configuration processor delegate that use the preferences as a cache mechanism for the installed apps versions.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class CachedVersionProcessorDelegate extends BaseVersionProcessor
{

	private String appName;

	/**
	 * Constructs a new CachedVersionProcessorDelegate.<br>
	 * Unlike other processor delegates, this one is not instantiated through an extension point, as it's being
	 * allocated per application that does not have a version delegate. So, this instance should be created with the
	 * application name. This application name will be used to track the cached version preference for that app.
	 * 
	 * @param appName
	 */
	public CachedVersionProcessorDelegate(String appName)
	{
		this.appName = appName;

	}

	@Override
	public String getSupportedApplication()
	{
		return appName;
	}

	/**
	 * The run command for this special delegate looks for a version value cached in the workspace preferences.<br>
	 * The version identifier was cached in the preferences when the app was installed through the dev-toolbox. This
	 * method also tries to verify that the application is still in the cached location. If not, which means it was
	 * probably removed externally, we remove the cache from the preferences.
	 * 
	 * @param commandType
	 *            Expecting for the 'version' command.
	 * @param workingDir
	 *            - Not used in this delegate, and may be null.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object runCommand(String commandType, IPath workingDir)
	{
		// We cache the version and the location in the preferences as a JSON string. So, it's pretty easy to maintain.

		IPreferencesService service = Platform.getPreferencesService();
		IScopeContext[] contexts;
		IProject project = PortalUIPlugin.getActiveProject();
		if (project != null)
		{
			contexts = new IScopeContext[] { new ProjectScope(project), new DefaultScope() };
		}
		else
		{
			contexts = new IScopeContext[] { new InstanceScope(), new DefaultScope() };
		}
		String versions = service.getString(PortalUIPlugin.PLUGIN_ID, IPortalPreferences.CACHED_VERSIONS_PROPERTY_NAME,
				null, contexts);
		if (versions == null || versions.equals(StringUtil.EMPTY))
		{
			return null;
		}
		Map<String, Map<String, String>> mapping = (Map<String, Map<String, String>>) JSON.parse(versions);
		Map<String, String> appVersion = mapping.get(getSupportedApplication().toLowerCase());
		if (appVersion != null)
		{
			String location = appVersion.get(IPortalPreferences.CACHED_LOCATION_PROPERTY);
			String version = appVersion.get(IPortalPreferences.CACHED_VERSION_PROPERTY);
			if (location != null)
			{
				// Verify that this location still exists on that machine. If not, remove it and store it back to the
				// preferences.
				File localFile = new File(location);
				if (!localFile.exists())
				{
					mapping.remove(getSupportedApplication().toLowerCase());
					IEclipsePreferences node = null;
					if (project != null)
					{
						node = contexts[0].getNode(PortalUIPlugin.PLUGIN_ID);
					}
					else
					{
						node = contexts[1].getNode(PortalUIPlugin.PLUGIN_ID);
					}
					node.put(IPortalPreferences.CACHED_VERSIONS_PROPERTY_NAME, JSON.toString(mapping));
					try
					{
						node.flush();
					}
					catch (BackingStoreException e)
					{
						PortalUIPlugin.logError(e);
						return null;
					}
					return version;
				}
				// We are good to return the cached version
				return version;
			}
		}
		return null;
	}

}
