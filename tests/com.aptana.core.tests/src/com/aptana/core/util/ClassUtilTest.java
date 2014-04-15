/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.junit.Test;
import org.osgi.framework.BundleActivator;

import com.aptana.core.CorePlugin;
import com.aptana.core.resources.IUniformResourceMarker;

public class ClassUtilTest
{
	@Test
	public void testGetClassesTree()
	{
		List<Class<?>> classes = ClassUtil.getClassesTree(CorePlugin.class);

		assertEquals(5, classes.size());
		assertTrue(classes.contains(CorePlugin.class));
		assertTrue(classes.contains(IPreferenceChangeListener.class));
		assertTrue(classes.contains(Plugin.class));
		assertTrue(classes.contains(BundleActivator.class));
		assertTrue(classes.contains(Object.class));
	}

	@Test
	public void testGetInterfaceTree()
	{
		List<Class<?>> classes = ClassUtil.getClassesTree(IUniformResourceMarker.class);

		assertEquals(3, classes.size());
		assertTrue(classes.contains(IUniformResourceMarker.class));
		assertTrue(classes.contains(IMarker.class));
		assertTrue(classes.contains(IAdaptable.class));
	}
}
