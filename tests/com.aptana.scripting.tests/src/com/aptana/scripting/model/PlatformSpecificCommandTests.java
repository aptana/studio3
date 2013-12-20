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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;

import com.aptana.scripting.model.filters.IModelFilter;

public class PlatformSpecificCommandTests extends BundleTestBase
{
	/**
	 * assertEndsWith
	 * 
	 * @param expected
	 * @param actual
	 */
	protected void assertEndsWith(String expected, String actual)
	{
		assertNotNull(actual);
		assertTrue(actual.endsWith(expected));
	}

	/**
	 * executeCommand
	 * 
	 * @param command
	 * @return
	 */
	protected String executeCommand(CommandElement command)
	{
		assertNotNull(command);

		// run command and grab result
		CommandResult result = command.execute();
		assertNotNull(result);

		// return string result
		return result.getOutputString();
	}

	/**
	 * getCommand
	 * 
	 * @param name
	 * @return
	 */
	protected CommandElement getCommand(final String name)
	{
		List<CommandElement> commands = BundleTestBase.getBundleManagerInstance().getExecutableCommands(
				new IModelFilter()
				{
					public boolean include(AbstractElement element)
					{
						boolean result = false;

						if (element instanceof CommandElement)
						{
							CommandElement command = (CommandElement) element;

							result = ("foo".equals(command.getScope()) && name.equals(command.getDisplayName()));
						}

						return result;
					}
				});

		assertNotNull(commands);
		assertTrue(commands.size() == 1);

		return commands.get(0);
	}

	@Before
	public void setUp() throws Exception
	{
		this.loadBundleEntry("bundleWithPlatformSpecifierCommands", BundlePrecedence.PROJECT);
	}

	/**
	 * testCommand
	 * 
	 * @param name
	 */
	protected void testCommand(String name)
	{
		this.testCommand(name, null);
	}

	/**
	 * testCommand
	 * 
	 * @param name
	 * @param platform
	 */
	protected void testCommand(String name, String platform)
	{
		String OS = Platform.getOS();

		if (platform == null || OS.equals(platform))
		{
			CommandElement command = getCommand(name);
			String result = executeCommand(command);

			if (OS.equals(Platform.OS_WIN32))
			{
				assertEndsWith(name, result);
			}
			else
			{
				assertEquals(name, result);
			}
		}
	}

	/**
	 * testExplicitAllPlatformBlock
	 */
	@Test
	public void testExplicitAllPlatformBlock()
	{
		this.testCommand("explicitAllPlatformBlock");
		this.testCommand("explicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("explicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("explicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testExplicitAllPlatformString
	 */
	@Test
	public void testExplicitAllPlatformString()
	{
		this.testCommand("explicitAllPlatformString");
		this.testCommand("explicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("explicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("explicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testImplicitAllPlatformBlock
	 */
	@Test
	public void testImplicitAllPlatformBlock()
	{
		this.testCommand("implicitAllPlatformBlock");
		this.testCommand("implicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("implicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("implicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testImplicitAllPlatformString
	 */
	@Test
	public void testImplicitAllPlatformString()
	{
		this.testCommand("implicitAllPlatformString");
		this.testCommand("implicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("implicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("implicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testLinuxAndExplicitAllPlatformBlock
	 */
	@Test
	public void testLinuxAndExplicitAllPlatformBlock()
	{
		this.testCommand("linuxAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("linuxAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("linuxAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testLinuxAndExplicitAllPlatformString
	 */
	@Test
	public void testLinuxAndExplicitAllPlatformString()
	{
		this.testCommand("linuxAndExplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("linuxAndExplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("linuxAndExplicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testLinuxAndImplicitAllPlatformBlock
	 */
	@Test
	public void testLinuxAndImplicitAllPlatformBlock()
	{
		this.testCommand("linuxAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("linuxAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("linuxAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testLinuxAndImplicitAllPlatformString
	 */
	@Test
	public void testLinuxAndImplicitAllPlatformString()
	{
		this.testCommand("linuxAndImplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("linuxAndImplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("linuxAndImplicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testLinuxOnlyPlatformBlock
	 */
	@Test
	public void testLinuxOnlyPlatformBlock()
	{
		this.testCommand("linuxOnlyPlatformBlock", Platform.OS_LINUX);
	}

	/**
	 * testLinuxOnlyPlatformString
	 */
	@Test
	public void testLinuxOnlyPlatformString()
	{
		this.testCommand("linuxOnlyPlatformString", Platform.OS_LINUX);
	}

	/**
	 * testMacAndExplicitAllPlatformBlock
	 */
	@Test
	public void testMacAndExplicitAllPlatformBlock()
	{
		this.testCommand("macAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("macAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("macAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testMacAndExplicitAllPlatformString
	 */
	@Test
	public void testMacAndExplicitAllPlatformString()
	{
		this.testCommand("macAndExplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("macAndExplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("macAndExplicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testMacAndImplicitAllPlatformBlock
	 */
	@Test
	public void testMacAndImplicitAllPlatformBlock()
	{
		this.testCommand("macAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("macAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("macAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testMacAndImplicitAllPlatformString
	 */
	@Test
	public void testMacAndImplicitAllPlatformString()
	{
		this.testCommand("macAndImplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("macAndImplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("macAndImplicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testMacOnlyPlatformBlock
	 */
	@Test
	public void testMacOnlyPlatformBlock()
	{
		this.testCommand("macOnlyPlatformBlock", Platform.OS_MACOSX);
	}

	/**
	 * testMacOnlyPlatformString
	 */
	@Test
	public void testMacOnlyPlatformString()
	{
		this.testCommand("macOnlyPlatformString", Platform.OS_MACOSX);
	}

	/**
	 * testUnixAndExplicitAllPlatformBlock
	 */
	@Test
	public void testUnixAndExplicitAllPlatformBlock()
	{
		this.testCommand("unixAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("unixAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("unixAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testUnixAndExplicitAllPlatformString
	 */
	@Test
	public void testUnixAndExplicitAllPlatformString()
	{
		this.testCommand("unixAndExplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("unixAndExplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("unixAndExplicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testUnixAndImplicitAllPlatformBlock
	 */
	@Test
	public void testUnixAndImplicitAllPlatformBlock()
	{
		this.testCommand("unixAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("unixAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("unixAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testUnixAndImplicitAllPlatformString
	 */
	@Test
	public void testUnixAndImplicitAllPlatformString()
	{
		this.testCommand("unixAndImplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("unixAndImplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("unixAndImplicitAllPlatformString", Platform.OS_WIN32);
	}

	/**
	 * testUnixOnlyPlatformBlock
	 */
	@Test
	public void testUnixOnlyPlatformBlock()
	{
		this.testCommand("unixOnlyPlatformBlock", Platform.OS_LINUX);
	}

	/**
	 * testUnixOnlyPlatformString
	 */
	@Test
	public void testUnixOnlyPlatformString()
	{
		this.testCommand("unixOnlyPlatformString", Platform.OS_LINUX);
	}

	/**
	 * testWindowsAndExplicitAllPlatformBlock
	 */
	@Test
	public void testWindowsAndExplicitAllPlatformBlock()
	{
		this.testCommand("windowsAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("windowsAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("windowsAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testWindowsAndImplicitAllPlatformBlock
	 */
	@Test
	public void testWindowsAndImplicitAllPlatformBlock()
	{
		this.testCommand("windowsAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("windowsAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("windowsAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}

	/**
	 * testWindowsOnlyPlatformString
	 */
	@Test
	public void testWindowsOnlyPlatformString()
	{
		this.testCommand("windowsOnlyPlatformString", Platform.OS_WIN32);
	}
}
