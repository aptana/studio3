package com.aptana.scripting.model;


public class BundleTests extends BundleTestBase
{
	/**
	 * compareScopedBundles
	 * 
	 * @param bundleName
	 * @param scope1
	 * @param scope2
	 * @param command1
	 * @param command2
	 */
	private void compareScopedBundles(String bundleName, BundleScope scope1, BundleScope scope2, String command1, String command2)
	{
		// confirm first bundle loaded properly
		BundleEntry entry = this.getBundleEntry(bundleName, scope1);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals(command1, commands[0].getInvoke());
		
		// confirm second bundle overrides application
		entry = this.getBundleEntry(bundleName, scope2);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals(command2, commands[0].getInvoke());
	}
	
	/**
	 * compareScopedBundlesWithDelete
	 * 
	 * @param bundleName
	 * @param scope1
	 * @param scope2
	 * @param command1
	 * @param command2
	 */
	private void compareScopedBundlesWithDelete(String bundleName, BundleScope scope1, BundleScope scope2, String command1, String command2)
	{
		this.compareScopedBundles(bundleName, scope1, scope2, command1, command2);
		
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		BundleElement[] bundles = entry.getBundles();
		assertEquals(2, bundles.length);
		entry.removeBundle(bundles[1]);
		
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals(command1, commands[0].getInvoke());
	}
	
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
		BundleEntry entry = this.getBundleEntry(bundleName, BundleScope.APPLICATION);
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
		BundleEntry entry = this.getBundleEntry(bundleName, BundleScope.APPLICATION);
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
		BundleEntry entry = this.getBundleEntry(bundleName, BundleScope.APPLICATION);
		CommandElement[] snippets = entry.getCommands();
		
		assertNotNull(snippets);
		assertEquals(1, snippets.length);
		assertTrue(snippets[0] instanceof SnippetElement);
	}
	
	/**
	 * testUserOverridesApplication
	 */
	public void testUserOverridesApplication()
	{
		compareScopedBundles(
			"bundleWithCommand",
			BundleScope.APPLICATION,
			BundleScope.USER,
			"cd",
			"cd .."
		);
	}

	/**
	 * testUserOverridesApplication2
	 */
	public void testUserOverridesApplication2()
	{
		this.compareScopedBundles(
			"bundleWithCommand",
			BundleScope.USER,
			BundleScope.APPLICATION,
			"cd ..",
			"cd .."
		);
	}
	
	/**
	 * testUserOverridesApplication
	 */
	public void testProjectOverridesApplication()
	{
		this.compareScopedBundles(
			"bundleWithCommand",
			BundleScope.APPLICATION,
			BundleScope.PROJECT,
			"cd",
			"cd /"
		);
	}
	
	/**
	 * testUserOverridesApplication2
	 */
	public void testProjectOverridesApplication2()
	{
		this.compareScopedBundles(
			"bundleWithCommand",
			BundleScope.PROJECT,
			BundleScope.APPLICATION,
			"cd /",
			"cd /"
		);
	}
	
	/**
	 * testUserOverridesApplication
	 */
	public void testProjectOverridesUser()
	{
		this.compareScopedBundles(
			"bundleWithCommand",
			BundleScope.USER,
			BundleScope.PROJECT,
			"cd ..",
			"cd /"
		);
	}
	
	/**
	 * testUserOverridesApplication2
	 */
	public void testProjectOverridesUser2()
	{
		this.compareScopedBundles(
			"bundleWithCommand",
			BundleScope.PROJECT,
			BundleScope.USER,
			"cd /",
			"cd /"
		);
	}
	
	/**
	 * testApplicationOverrideAndDelete
	 */
	public void testApplicationOverrideAndDelete()
	{
		this.compareScopedBundlesWithDelete(
			"bundleWithCommand",
			BundleScope.APPLICATION,
			BundleScope.USER,
			"cd",
			"cd .."
		);
	}
	
	/**
	 * testApplicationOverrideAndDelete
	 */
	public void testApplicationOverrideAndDelete2()
	{
		this.compareScopedBundlesWithDelete(
			"bundleWithCommand",
			BundleScope.APPLICATION,
			BundleScope.PROJECT,
			"cd",
			"cd /"
		);
	}
	
	/**
	 * testUserOverridesApplication
	 */
	public void testUserOverrideAndDelete()
	{
		this.compareScopedBundlesWithDelete(
			"bundleWithCommand",
			BundleScope.USER,
			BundleScope.PROJECT,
			"cd ..",
			"cd /"
		);
	}
	
	/**
	 * testSamePrecedenceOverride
	 */
	public void testSamePrecedenceOverride()
	{
		// confirm first bundle loaded properly
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundleScope.USER);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd ..", commands[0].getInvoke());
		
		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithSameCommand", BundleScope.USER);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd", commands[0].getInvoke());
	}
	
	/**
	 * testSamePrecedenceOverride2
	 */
	public void testSamePrecedenceOverride2()
	{
		// confirm first bundle loaded properly
		this.loadBundleEntry("bundleWithSameCommand", BundleScope.USER);
		BundleEntry entry = BundleManager.getInstance().getBundleEntry("bundleWithCommand");
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd", commands[0].getInvoke());
		
		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithCommand", BundleScope.USER);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd", commands[0].getInvoke());
	}
	
	
	/**
	 * testSamePrecedenceAugmentation
	 */
	public void testSamePrecedenceAugmentation()
	{
		// confirm first bundle loaded properly
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundleScope.USER);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd ..", commands[0].getInvoke());
		
		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithDifferentCommand", BundleScope.USER);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(2, commands.length);
		
		CommandElement command1 = commands[0];
		CommandElement command2 = commands[1];
		
		if (command1.getDisplayName().equals("MyCommand"))
		{
			assertEquals("cd ..", command1.getInvoke());
			assertEquals("cd", command2.getInvoke());
		}
		else
		{
			assertEquals("cd", command1.getInvoke());
			assertEquals("cd ..", command2.getInvoke());
		}
	}
	
	/**
	 * testNameFromBundleDirectory
	 */
	public void testNameFromBundleDirectory()
	{
		// load bundle
		String bundleName = "bundleName";
		this.loadBundleEntry(bundleName, BundleScope.PROJECT);
		
		// get bundle entry
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		assertNotNull(entry);
	}
	
	/**
	 * testNameFromBundleDirectory2
	 */
	public void testNameFromBundleDirectory2()
	{
		// load bundle
		this.loadBundleEntry("bundleNameWithExtension.rrbundle", BundleScope.PROJECT);
		
		// get bundle entry
		BundleEntry entry = BundleManager.getInstance().getBundleEntry("bundleNameWithExtension");
		assertNotNull(entry);
	}
}
