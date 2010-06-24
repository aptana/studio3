package com.aptana.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

public class EclipseUtil
{

	private static final String STANDALONE_PLUGIN_ID = "com.aptana.radrails.rcp"; //$NON-NLS-1$

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

	public static String getProductVersion()
	{
		String version = null;

		try
		{
			// this approach fails in "Rational Application Developer 6.0.1"
			IProduct product = Platform.getProduct();
			String aboutText = product.getProperty("aboutText"); //$NON-NLS-1$

			String pattern = "Version: (.*)\n"; //$NON-NLS-1$
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(aboutText);
			boolean found = m.find();
			if (!found)
			{
				p = Pattern.compile("build: (.*)\n"); //$NON-NLS-1$
				m = p.matcher(aboutText);
				found = m.find();
			}

			if (found)
			{
				version = m.group(1);
			}
		}
		catch (Exception e)
		{

		}

		return version;
	}

	public static boolean isStandalone()
	{
		return getPluginVersion(STANDALONE_PLUGIN_ID) != null;
	}
}
