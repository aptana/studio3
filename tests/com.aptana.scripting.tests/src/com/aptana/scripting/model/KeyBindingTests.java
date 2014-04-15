/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
public class KeyBindingTests extends BundleTestBase
{
	/**
	 * testEclipseLinuxPlatformToScriptingPlatformMapping
	 */
	@Test
	public void testEclipseLinuxPlatformToScriptingPlatformMapping()
	{
		Platform[] platforms = Platform.getPlatformsForEclipsePlatform(org.eclipse.core.runtime.Platform.OS_LINUX);
		
		assertNotNull(platforms);
		assertEquals(2, platforms.length);
		
		assertEquals(Platform.LINUX, platforms[0]);
		assertEquals(Platform.UNIX, platforms[1]);
	}
}
