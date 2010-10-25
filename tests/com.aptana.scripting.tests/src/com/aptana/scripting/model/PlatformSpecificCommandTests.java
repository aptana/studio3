package com.aptana.scripting.model;

import org.eclipse.core.runtime.Platform;

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
		CommandElement[] commands = BundleTestBase.getBundleManagerInstance().getCommands(new IModelFilter()
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
		assertTrue(commands.length == 1);
		
		return commands[0];
	}

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
	public void testLinuxAndExplicitAllPlatformBlock()
	{
		this.testCommand("linuxAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("linuxAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("linuxAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testLinuxAndExplicitAllPlatformString
	 */
	public void testLinuxAndExplicitAllPlatformString()
	{
		this.testCommand("linuxAndExplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("linuxAndExplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("linuxAndExplicitAllPlatformString", Platform.OS_WIN32);
	}
	
	/**
	 * testLinuxAndImplicitAllPlatformBlock
	 */
	public void testLinuxAndImplicitAllPlatformBlock()
	{
		this.testCommand("linuxAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("linuxAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("linuxAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testLinuxAndImplicitAllPlatformString
	 */
	public void testLinuxAndImplicitAllPlatformString()
	{
		this.testCommand("linuxAndImplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("linuxAndImplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("linuxAndImplicitAllPlatformString", Platform.OS_WIN32);
	}
	
	/**
	 * testLinuxOnlyPlatformBlock
	 */
	public void testLinuxOnlyPlatformBlock()
	{
		this.testCommand("linuxOnlyPlatformBlock", Platform.OS_LINUX);
	}
	
	/**
	 * testLinuxOnlyPlatformString
	 */
	public void testLinuxOnlyPlatformString()
	{
		this.testCommand("linuxOnlyPlatformString", Platform.OS_LINUX);
	}
	
	/**
	 * testMacAndExplicitAllPlatformBlock
	 */
	public void testMacAndExplicitAllPlatformBlock()
	{
		this.testCommand("macAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("macAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("macAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testMacAndExplicitAllPlatformString
	 */
	public void testMacAndExplicitAllPlatformString()
	{
		this.testCommand("macAndExplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("macAndExplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("macAndExplicitAllPlatformString", Platform.OS_WIN32);
	}
	
	/**
	 * testMacAndImplicitAllPlatformBlock
	 */
	public void testMacAndImplicitAllPlatformBlock()
	{
		this.testCommand("macAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("macAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("macAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testMacAndImplicitAllPlatformString
	 */
	public void testMacAndImplicitAllPlatformString()
	{
		this.testCommand("macAndImplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("macAndImplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("macAndImplicitAllPlatformString", Platform.OS_WIN32);
	}
	
	/**
	 * testMacOnlyPlatformBlock
	 */
	public void testMacOnlyPlatformBlock()
	{
		this.testCommand("macOnlyPlatformBlock", Platform.OS_MACOSX);
	}
	
	/**
	 * testMacOnlyPlatformString
	 */
	public void testMacOnlyPlatformString()
	{
		this.testCommand("macOnlyPlatformString", Platform.OS_MACOSX);
	}
	
	/**
	 * testUnixAndExplicitAllPlatformBlock
	 */
	public void testUnixAndExplicitAllPlatformBlock()
	{
		this.testCommand("unixAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("unixAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("unixAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testUnixAndExplicitAllPlatformString
	 */
	public void testUnixAndExplicitAllPlatformString()
	{
		this.testCommand("unixAndExplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("unixAndExplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("unixAndExplicitAllPlatformString", Platform.OS_WIN32);
	}
	
	/**
	 * testUnixAndImplicitAllPlatformBlock
	 */
	public void testUnixAndImplicitAllPlatformBlock()
	{
		this.testCommand("unixAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("unixAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("unixAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testUnixAndImplicitAllPlatformString
	 */
	public void testUnixAndImplicitAllPlatformString()
	{
		this.testCommand("unixAndImplicitAllPlatformString", Platform.OS_LINUX);
		this.testCommand("unixAndImplicitAllPlatformString", Platform.OS_MACOSX);
		this.testCommand("unixAndImplicitAllPlatformString", Platform.OS_WIN32);
	}
	
	/**
	 * testUnixOnlyPlatformBlock
	 */
	public void testUnixOnlyPlatformBlock()
	{
		this.testCommand("unixOnlyPlatformBlock", Platform.OS_LINUX);
	}
	
	/**
	 * testUnixOnlyPlatformString
	 */
	public void testUnixOnlyPlatformString()
	{
		this.testCommand("unixOnlyPlatformString", Platform.OS_LINUX);
	}
	
	/**
	 * testWindowsAndExplicitAllPlatformBlock
	 */
	public void testWindowsAndExplicitAllPlatformBlock()
	{
		this.testCommand("windowsAndExplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("windowsAndExplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("windowsAndExplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testWindowsAndImplicitAllPlatformBlock
	 */
	public void testWindowsAndImplicitAllPlatformBlock()
	{
		this.testCommand("windowsAndImplicitAllPlatformBlock", Platform.OS_LINUX);
		this.testCommand("windowsAndImplicitAllPlatformBlock", Platform.OS_MACOSX);
		this.testCommand("windowsAndImplicitAllPlatformBlock", Platform.OS_WIN32);
	}
	
	/**
	 * testWindowsOnlyPlatformString
	 */
	public void testWindowsOnlyPlatformString()
	{
		this.testCommand("windowsOnlyPlatformString", Platform.OS_WIN32);
	}
}
