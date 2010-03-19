package com.aptana.scripting.model;

import org.eclipse.core.runtime.Platform;

import com.aptana.scripting.model.filters.ScopeFilter;

public class PlatformSpecificCommandTests extends BundleTestBase
{
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		this.loadBundleEntry("bundleWithPlatformSpecifierCommands", BundlePrecedence.PROJECT);
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
	 * testCommandsForCurrentPlatform
	 */
	public void testCommandsForCurrentPlatform()
	{
		ScopeFilter filter = new ScopeFilter("foo");
		CommandElement[] commands = BundleTestBase.getBundleManagerInstance().getCommands(filter);

		assertNotNull(commands);
		assertTrue(commands.length > 0);

		assertEquals("implicitAllPlatformString", executeCommand(commands[0]));
		assertEquals("explicitAllPlatformString", executeCommand(commands[1]));

		if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			assertEquals(22, commands.length);
			assertEquals("macAndImplicitAllPlatformString", executeCommand(commands[2]));
			assertEquals("macAndExplicitAllPlatformString", executeCommand(commands[3]));
			assertEquals("macOnlyPlatformString", executeCommand(commands[4]));
			assertEquals("implicitAllPlatformBlock", executeCommand(commands[11]));
			assertEquals("explicitAllPlatformBlock", executeCommand(commands[12]));
			assertEquals("macAndImplicitAllPlatformBlock", executeCommand(commands[13]));
			assertEquals("macAndExplicitAllPlatformBlock", executeCommand(commands[14]));
			assertEquals("macAndImplicitAllPlatformBlock", executeCommand(commands[13]));
			assertEquals("macAndExplicitAllPlatformBlock", executeCommand(commands[14]));
			assertEquals("macOnlyPlatformBlock", executeCommand(commands[15]));
			assertEquals("windowsAndImplicitAllPlatformBlock", executeCommand(commands[16]));
			assertEquals("windowsAndExplicitAllPlatformBlock", executeCommand(commands[17]));
			assertEquals("linuxAndImplicitAllPlatformBlock", executeCommand(commands[18]));
			assertEquals("linuxAndExplicitAllPlatformBlock", executeCommand(commands[19]));
			assertEquals("unixAndImplicitAllPlatformBlock", executeCommand(commands[20]));
			assertEquals("unixAndExplicitAllPlatformBlock", executeCommand(commands[21]));
		}

		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			assertEquals(22, commands.length);
			assertEquals("windowsAndImplicitAllPlatformString", executeCommand(commands[5]));
			assertEquals("windowsAndExplicitAllPlatformString", executeCommand(commands[6]));
			assertEquals("windowsOnlyPlatformString", executeCommand(commands[7]));
			// TODO more
		}

		if (Platform.OS_LINUX.equals(Platform.getOS()))
		{
			// The ones with .unix also apply to linux
			assertEquals(24, commands.length);
			assertEquals("linuxAndImplicitAllPlatformString", executeCommand(commands[6]));
			assertEquals("linuxAndExplicitAllPlatformString", executeCommand(commands[7]));
			assertEquals("linuxOnlyPlatformString", executeCommand(commands[8]));
			assertEquals("unixAndImplicitAllPlatformString", executeCommand(commands[9]));
			assertEquals("unixAndExplicitAllPlatformString", executeCommand(commands[10]));
			assertEquals("unixOnlyPlatformString", executeCommand(commands[11]));
			assertEquals("implicitAllPlatformBlock", executeCommand(commands[12]));
			assertEquals("explicitAllPlatformBlock", executeCommand(commands[13]));
			assertEquals("macAndImplicitAllPlatformBlock", executeCommand(commands[14]));
			assertEquals("macAndExplicitAllPlatformBlock", executeCommand(commands[15]));
			assertEquals("windowsAndImplicitAllPlatformBlock", executeCommand(commands[16]));
			assertEquals("windowsAndExplicitAllPlatformBlock", executeCommand(commands[17]));
			assertEquals("linuxAndImplicitAllPlatformBlock", executeCommand(commands[18]));
			assertEquals("linuxAndExplicitAllPlatformBlock", executeCommand(commands[19]));
			assertEquals("linuxOnlyPlatformBlock", executeCommand(commands[20]));
			assertEquals("unixAndImplicitAllPlatformBlock", executeCommand(commands[21]));
			assertEquals("unixAndExplicitAllPlatformBlock", executeCommand(commands[22]));
			assertEquals("unixOnlyPlatformBlock", executeCommand(commands[23]));
		}
	}

}
