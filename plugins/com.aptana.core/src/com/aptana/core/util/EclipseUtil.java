/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.service.datalocation.Location;
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
	
	/**
	 * Returns path to application launcher executable
	 * 
	 * @return
	 */
	public static IPath getApplicationLauncher() {
		return getApplicationLauncher(false);
	}

	/**
	 * Returns path to application launcher executable
	 * 
	 * @param asSplashLauncher
	 * @return
	 */
	public static IPath getApplicationLauncher(boolean asSplashLauncher) {
		IPath launcher = null;
		String cmdline = System.getProperty("eclipse.commands"); //$NON-NLS-1$
		if ( cmdline != null && cmdline.length() > 0 ) {
			String[] args = cmdline.split("\n"); //$NON-NLS-1$
			for( int i = 0; i < args.length; ++i ) {
				if ( "-launcher".equals(args[i]) && (i+1) < args.length ) { //$NON-NLS-1$
					launcher = Path.fromOSString(args[i+1]);
					break;
				}
			}
		}
		if ( launcher == null ) {
			Location location = Platform.getInstallLocation();
			if ( location != null ) {
				launcher = new Path(location.getURL().getFile());
				if ( launcher.toFile().isDirectory() ) {
					String[] executableFiles = launcher.toFile().list(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							IPath path = Path.fromOSString(dir.getAbsolutePath()).append(name);
							name = path.removeFileExtension().lastSegment();
							String ext = path.getFileExtension();
							if (Platform.OS_MACOSX.equals(Platform.getOS())) {
								if (!"app".equals(ext)) {
									return false;
								}
							}
							if ("Eclipse".equalsIgnoreCase(name) || "AptanaStudio3".equalsIgnoreCase(name)) { //$NON-NLS-1$ //$NON-NLS-2$
								return true;
							}
							return false;
						}
					});
					if (executableFiles.length > 0) {
						launcher = launcher.append(executableFiles[0]);
					}
				}
			}
		}
		if (launcher == null || !launcher.toFile().exists() ) {
			return null;
		}
		if (Platform.OS_MACOSX.equals(Platform.getOS()) && asSplashLauncher) {
			launcher = new Path(PlatformUtil.getApplicationExecutable(launcher.toOSString()).getAbsolutePath());
		}
		return launcher;
	}

}
