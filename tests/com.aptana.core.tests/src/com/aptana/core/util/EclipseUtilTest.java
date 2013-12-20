/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.service.datalocation.Location;
import org.junit.Test;
import org.osgi.framework.Version;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.util.EclipseUtil.LauncherFilter;

public class EclipseUtilTest
{

	@Test
	public void testGetApplicationLauncher()
	{
		IPath path = EclipseUtil.getApplicationLauncher();
		assertNotNull(path);

		boolean match = false;
		String name = path.removeFileExtension().lastSegment();
		for (String launcherName : EclipseUtil.LAUNCHER_NAMES)
		{
			if (launcherName.equalsIgnoreCase(name))
			{
				match = true;
				break;
			}
		}
		assertTrue(match);
	}

	@Test
	public void testGetProductVersion()
	{
		String productVersion = EclipseUtil.getProductVersion();
		Version version = Platform.getProduct().getDefiningBundle().getVersion();

		assertEquals(version.getMajor() + "." + version.getMinor() + "." + version.getMicro(), productVersion);
	}

	@Test
	public void testGetTraceableItems()
	{
		Map<String, String> items = EclipseUtil.getTraceableItems();

		assertTrue(items.containsKey("com.aptana.core/debug"));
		assertTrue(items.containsKey("com.aptana.core/debug/builder"));
		assertTrue(items.containsKey("com.aptana.core/debug/logger"));
		assertTrue(items.containsKey("com.aptana.core/debug/shell"));
	}

	@Test
	public void testGetCurrentDebuggableComponents()
	{
		String[] components = EclipseUtil.getCurrentDebuggableComponents();
		assertEquals(0, components.length);

		String[] testComponents = new String[] { "com.aptana.core/debug", "com.aptana.rcp/debug" };
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID);
		prefs.put(ICorePreferenceConstants.PREF_DEBUG_COMPONENT_LIST, StringUtil.join(",", testComponents));

		components = EclipseUtil.getCurrentDebuggableComponents();
		assertEquals(testComponents.length, components.length);
		for (int i = 0; i < testComponents.length; ++i)
		{
			assertEquals(testComponents[i], components[i]);
		}
	}

	@Test
	public void testGetSystemProperty()
	{
		assertEquals(PlatformUtil.isWindows() ? "\\" : "/", EclipseUtil.getSystemProperty("file.separator"));
		assertNull(EclipseUtil.getSystemProperty("random_property"));
		assertNull(EclipseUtil.getSystemProperty(null));
	}

	@Test
	public void testIsSystemPropertyEnabled()
	{
		assertTrue(EclipseUtil.isSystemPropertyEnabled("java.specification.version"));
		assertFalse(EclipseUtil.isSystemPropertyEnabled("random_property"));
		assertFalse(EclipseUtil.isSystemPropertyEnabled(null));
	}

	@Test
	public void testIsPluginLoaded()
	{
		assertTrue(EclipseUtil.isPluginLoaded(CorePlugin.getDefault()));
		assertFalse(EclipseUtil.isPluginLoaded(null));
	}

	@Test
	public void testGetPluginVersion()
	{
		assertNotNull(EclipseUtil.getPluginVersion(CorePlugin.getDefault()));
		assertNotNull(EclipseUtil.getPluginVersion(CorePlugin.PLUGIN_ID));
		assertNull(EclipseUtil.getPluginVersion((Plugin) null));
		assertNull(EclipseUtil.getPluginVersion((String) null));
	}

	@Test
	public void testLauncherFilter()
	{
		Location location = Platform.getInstallLocation();
		assertNotNull(location);

		IPath launcher = new Path(location.getURL().getFile());
		File file = launcher.toFile();
		assertTrue(file.isDirectory());

		String[] executableFiles = file.list(new LauncherFilter());
		assertTrue(executableFiles.length > 0);
	}
}
