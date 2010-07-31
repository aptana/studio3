package com.aptana.portal.ui.dispatch.processorDelegates;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mortbay.util.ajax.JSON;

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
		IPreferenceStore preferenceStore = PortalUIPlugin.getDefault().getPreferenceStore();
		String versions = preferenceStore.getString(IPortalPreferences.CACHED_VERSIONS_PROPERTY_NAME);
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
					preferenceStore.setValue(IPortalPreferences.CACHED_VERSIONS_PROPERTY_NAME, JSON.toString(mapping));
					return null;
				}
				// We are good to return the cached version
				return version;
			}
		}
		return null;
	}

}
