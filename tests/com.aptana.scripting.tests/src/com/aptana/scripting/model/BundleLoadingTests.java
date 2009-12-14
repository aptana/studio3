package com.aptana.scripting.model;


public class BundleLoadingTests extends BundleTestBase
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
		// confirm app bundle loaded properly
		BundleEntry entry = this.loadBundleEntry(bundleName, scope1);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals(command1, commands[0].getInvoke());
		
		// confirm user bundle overrides application
		entry = this.loadBundleEntry(bundleName, scope2);
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
}
