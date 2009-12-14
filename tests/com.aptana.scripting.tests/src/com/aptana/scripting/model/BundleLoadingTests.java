package com.aptana.scripting.model;


public class BundleLoadingTests extends BundleTestBase
{
	/**
	 * setUp
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		BundleManager.getInstance().reset();
	}

	/**
	 * testLoadLoneBundle
	 */
	public void testLoadLoneBundle()
	{
		String bundleName = "loneBundle";
		BundleElement bundle = this.loadBundle(bundleName, BundleScope.APPLICATION);
		
		assertNotNull(bundle);
		assertEquals(bundleName, bundle.getDisplayName());
	}

	/**
	 * testLoadBundleWithCommand
	 */
	public void testLoadBundleWithCommand()
	{
		String bundleName = "bundleWithCommand";
		BundleEntry entry = this.loadBundleEntry(bundleName, BundleScope.APPLICATION);
		CommandElement[] commands = entry.getCommands();
		
		assertNotNull(commands);
		assertEquals(1, commands.length);
	}

	/**
	 * testLoadBundleWithMenu
	 */
	public void testLoadBundleWithMenu()
	{
		String bundleName = "bundleWithMenu";
		BundleEntry entry = this.loadBundleEntry(bundleName, BundleScope.APPLICATION);
		MenuElement[] menus = entry.getMenus();
		
		assertNotNull(menus);
		assertEquals(1, menus.length);
	}

	/**
	 * testLoadBundleWithSnippet
	 */
	public void testLoadBundleWithSnippet()
	{
		String bundleName = "bundleWithSnippet";
		BundleEntry entry = this.loadBundleEntry(bundleName, BundleScope.APPLICATION);
		SnippetElement[] snippets = entry.getSnippets();
		
		assertNotNull(snippets);
		assertEquals(1, snippets.length);
	}
	
	/**
	 * testUserOverridesApplication
	 */
	public void testUserOverridesApplication()
	{
		String bundleName = "bundleWithCommand";
		
		// confirm app bundle loaded properly
		BundleEntry entry = this.loadBundleEntry(bundleName, BundleScope.APPLICATION);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd", commands[0].getInvoke());
		
		// confirm user bundle overrides application
		entry = this.loadBundleEntry(bundleName, BundleScope.USER);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd ..", commands[0].getInvoke());
	}
	
	/**
	 * testUserOverridesApplication
	 */
	public void testProjectOverridesApplication()
	{
		String bundleName = "bundleWithCommand";
		
		// confirm app bundle loaded properly
		BundleEntry entry = this.loadBundleEntry(bundleName, BundleScope.APPLICATION);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd", commands[0].getInvoke());
		
		// confirm project bundle overrides application
		entry = this.loadBundleEntry(bundleName, BundleScope.PROJECT);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd /", commands[0].getInvoke());
	}
	
	/**
	 * testUserOverridesApplication
	 */
	public void testProjectOverridesUser()
	{
		String bundleName = "bundleWithCommand";
		
		// confirm app bundle loaded properly
		BundleEntry entry = this.loadBundleEntry(bundleName, BundleScope.USER);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd ..", commands[0].getInvoke());
		
		// confirm project bundle overrides application
		entry = this.loadBundleEntry(bundleName, BundleScope.PROJECT);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd /", commands[0].getInvoke());
	}
}
