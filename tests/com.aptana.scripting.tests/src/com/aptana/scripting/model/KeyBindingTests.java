package com.aptana.scripting.model;

public class KeyBindingTests extends BundleTestBase
{
	/**
	 * testEclipseLinuxPlatformToScriptingPlatformMapping
	 */
	public void testEclipseLinuxPlatformToScriptingPlatformMapping()
	{
		Platform[] platforms = Platform.getPlatformsForEclipsePlatform(org.eclipse.core.runtime.Platform.OS_LINUX);
		
		assertNotNull(platforms);
		assertEquals(2, platforms.length);
		
		assertEquals(Platform.LINUX, platforms[0]);
		assertEquals(Platform.UNIX, platforms[1]);
	}
}
