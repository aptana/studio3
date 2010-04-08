package com.aptana.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

public class EclipseUtils
{

	/**
	 * Are we in Eclipse 3.5 or higher?
	 */
	public static boolean inEclipse35orHigher = false;

	static
	{
		String version = System.getProperty("osgi.framework.version"); //$NON-NLS-1$

		if (version != null && version.startsWith("3.")) //$NON-NLS-1$
		{
			String[] parts = version.split("\\."); //$NON-NLS-1$
			if (parts.length > 1)
			{
				try
				{
					int minorVersion = Integer.parseInt(parts[1]);
					if (minorVersion > 4)
					{
						inEclipse35orHigher = true;
					}
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	/**
	 * Retrieves the bundle version of a plugin based on its id.
	 * 
	 * @param pluginId
	 *            the id of the plugin
	 * @return the bundle version, or null if not found.
	 */
	public static String getPluginVersion(String pluginId)
	{
		if (pluginId == null)
		{
			return null;
		}

		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle == null)
		{
			return null;
		}
		return bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION).toString();
	}

	/**
	 * Retrieves the bundle version of a plugin.
	 * 
	 * @param plugin
	 *            the plugin to retrieve from
	 * @return the bundle version, or null if not found.
	 */
	public static String getPluginVersion(Plugin plugin)
	{
		if (plugin == null)
		{
			return null;
		}

		Bundle bundle = plugin.getBundle();
		if (bundle == null)
		{
			return null;
		}
		return bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION).toString();
	}
}
