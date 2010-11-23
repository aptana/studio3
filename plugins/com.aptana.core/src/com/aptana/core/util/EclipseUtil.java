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
package com.aptana.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

public class EclipseUtil
{
	public static final String STANDALONE_PLUGIN_ID = "com.aptana.rcp"; //$NON-NLS-1$

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
	 * Retrieves the product version from the Platform aboutText property
	 * 
	 * @return
	 */
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

	/**
	 * Determines if the IDE is running as a standalone app versus as a plugin
	 * 
	 * @return
	 */
	public static boolean isStandalone()
	{
		return getPluginVersion(STANDALONE_PLUGIN_ID) != null;
	}

	/**
	 * Determines if the IDE is running in a unit test
	 * 
	 * @return
	 */
	public static boolean isTesting()
	{
		String application = System.getProperty("eclipse.application"); //$NON-NLS-1$
		if (application != null
				&& (application.equals("org.eclipse.pde.junit.runtime.uitestapplication")  //$NON-NLS-1$
						|| application.equals("org.eclipse.test.coretestapplication") //$NON-NLS-1$
						|| application.equals("org.eclipse.test.uitestapplication") //$NON-NLS-1$
						|| application.equals("org.eclipse.pde.junit.runtime.legacytestapplication") //$NON-NLS-1$
						|| application.equals("org.eclipse.pde.junit.runtime.coretestapplication") //$NON-NLS-1$
						|| application.equals("org.eclipse.pde.junit.runtime.coretestapplicationnonmain") //$NON-NLS-1$
						|| application.equals("org.eclipse.pde.junit.runtime.nonuithreadtestapplication"))) //$NON-NLS-1$
		{
			return true;
		}
		Object commands = System.getProperties().get("eclipse.commands"); //$NON-NLS-1$
		return (commands != null) ? commands.toString().contains("-testLoaderClass") : false; //$NON-NLS-1$
	}
}
