package com.aptana.scripting.model;


public class BundleTests extends BundleTestBase
{
	/**
	 * compareScopedBundles
	 * 
	 * @param bundleName
	 * @param prec1
	 * @param prec2
	 * @param command1
	 * @param command2
	 */
	private void compareScopedBundles(String bundleName, BundlePrecedence prec1, BundlePrecedence prec2, String command1, String command2)
	{
		// confirm first bundle loaded properly
		BundleEntry entry = this.getBundleEntry(bundleName, prec1);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals(command1, commands[0].getInvoke());
		
		// confirm second bundle overrides application
		entry = this.getBundleEntry(bundleName, prec2);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals(command2, commands[0].getInvoke());
	}
	
	/**
	 * compareScopedBundlesWithDelete
	 * 
	 * @param bundleName
	 * @param prec1
	 * @param prec2
	 * @param command1
	 * @param command2
	 */
	private void compareScopedBundlesWithDelete(String bundleName, BundlePrecedence prec1, BundlePrecedence prec2, String command1, String command2)
	{
		this.compareScopedBundles(bundleName, prec1, prec2, command1, command2);
		
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
		BundleElement bundle = this.loadBundle(bundleName, BundlePrecedence.APPLICATION);
		
		assertNotNull(bundle);
		assertEquals(bundleName, bundle.getDisplayName());
	}

	/**
	 * testLoadBundleWithCommand
	 */
	public void testLoadBundleWithCommand()
	{
		String bundleName = "bundleWithCommand";
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
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
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
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
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
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
			BundlePrecedence.APPLICATION,
			BundlePrecedence.USER,
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
			BundlePrecedence.USER,
			BundlePrecedence.APPLICATION,
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
			BundlePrecedence.APPLICATION,
			BundlePrecedence.PROJECT,
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
			BundlePrecedence.PROJECT,
			BundlePrecedence.APPLICATION,
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
			BundlePrecedence.USER,
			BundlePrecedence.PROJECT,
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
			BundlePrecedence.PROJECT,
			BundlePrecedence.USER,
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
			BundlePrecedence.APPLICATION,
			BundlePrecedence.USER,
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
			BundlePrecedence.APPLICATION,
			BundlePrecedence.PROJECT,
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
			BundlePrecedence.USER,
			BundlePrecedence.PROJECT,
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
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd ..", commands[0].getInvoke());
		
		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithSameCommand", BundlePrecedence.USER);
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
		this.loadBundleEntry("bundleWithSameCommand", BundlePrecedence.USER);
		BundleEntry entry = BundleManager.getInstance().getBundleEntry("bundleWithCommand");
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd", commands[0].getInvoke());
		
		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.USER);
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
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.length);
		assertEquals("cd ..", commands[0].getInvoke());
		
		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithDifferentCommand", BundlePrecedence.USER);
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
		this.loadBundleEntry(bundleName, BundlePrecedence.PROJECT);
		
		// get bundle entry
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		assertNotNull(entry);
	}
	
	/**
	 * testNameFromBundleDirectoryWithExtension
	 */
	public void testNameFromBundleDirectoryWithExtension()
	{
		// load bundle
		this.loadBundleEntry("bundleNameWithExtension.ruble", BundlePrecedence.PROJECT);
		
		// get bundle entry
		BundleEntry entry = BundleManager.getInstance().getBundleEntry("bundleNameWithExtension");
		assertNotNull(entry);
	}
	
	/**
	 * testBundleIsBundleDeclaration
	 */
	public void testBundleIsBundleDeclaration()
	{
		String bundleName = "bundleDefinition";
		this.loadBundleEntry(bundleName, BundlePrecedence.PROJECT);
		
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		assertNotNull(entry);
		
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.length);
		assertFalse(bundles[0].isReference());
	}
	
	/**
	 * testBundleIsBundleDeclaration2
	 */
	public void testBundleIsBundleDeclaration2()
	{
		String bundleName = "bundleDefinition2";
		this.loadBundleEntry(bundleName, BundlePrecedence.PROJECT);
		
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		assertNotNull(entry);
		
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.length);
		assertFalse(bundles[0].isReference());
	}
	
	/**
	 * testBundleIsBundleReference
	 */
	public void testBundleIsBundleReference()
	{
		this.loadBundleEntry("bundleReference", BundlePrecedence.PROJECT);
		
		BundleEntry entry = BundleManager.getInstance().getBundleEntry("MyBundle");
		assertNotNull(entry);
		
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.length);
		assertTrue(bundles[0].isReference());
	}
	
	public void testReferenceLoadingAcrossPrecendenceBounds()
	{
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommandReference", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommandReference", BundlePrecedence.PROJECT);
		
		BundleEntry entry = BundleManager.getInstance().getBundleEntry("bundleWithCommand");
		assertNotNull(entry);
		
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(3, bundles.length);
		
		CommandElement[] commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(3, commands.length);
	}
}
