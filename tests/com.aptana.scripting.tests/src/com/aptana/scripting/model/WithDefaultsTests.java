package com.aptana.scripting.model;

public class WithDefaultsTests extends BundleTestBase
{
	/**
	 * testWithDefaults
	 */
	public void testWithDefaults()
	{
		BundleElement bundle = this.loadBundle("withDefaults", BundlePrecedence.PROJECT);
		assertNotNull(bundle);
		
		// get command
		CommandElement command = bundle.getCommandByName("Test");
		assertNotNull(command);
		
		InputType[] inputTypes = command.getInputTypes();
		assertNotNull(inputTypes);
		assertEquals(1, inputTypes.length);
		
		InputType inputType = inputTypes[0];
		assertEquals(InputType.NONE, inputType);
		
		assertEquals("discard", command.getOutputType());
	}
	
	/**
	 * testWithDefaultsInsideBundle
	 */
	public void testWithDefaultInsideBundle()
	{
		this.loadBundleEntry("withDefaultsInsideBundle", BundlePrecedence.PROJECT);
		BundleEntry entry = BundleTestBase.getBundleManagerInstance().getBundleEntry("Bundle Reference");
		assertNotNull(entry);
		
		BundleElement[] bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.length);
		
		BundleElement bundle = bundles[0];
		assertNotNull(bundle);
		
		// get command
		CommandElement command = bundle.getCommandByName("Test");
		assertNotNull(command);
		
		assertEquals("source.ruby", command.getScope());
		
		InputType[] inputTypes = command.getInputTypes();
		assertNotNull(inputTypes);
		assertEquals(1, inputTypes.length);
		
		InputType inputType = inputTypes[0];
		assertEquals(InputType.NONE, inputType);
		
		assertEquals("discard", command.getOutputType());
	}
	
	/**
	 * testNestedWithDefaults
	 */
	public void testNestedWithDefaults()
	{
		BundleElement bundle = this.loadBundle("nestedWithDefaults", BundlePrecedence.PROJECT);
		assertNotNull(bundle);
		
		// get first command
		CommandElement command = bundle.getCommandByName("Test 1");
		assertNotNull(command);
		
		InputType[] inputTypes = command.getInputTypes();
		assertNotNull(inputTypes);
		assertEquals(1, inputTypes.length);
		
		assertEquals("source.ruby", command.getScope());
		
		InputType inputType = inputTypes[0];
		assertEquals(InputType.NONE, inputType);
		
		assertEquals("discard", command.getOutputType());
		
		// get second command
		command = bundle.getCommandByName("Test 2");
		assertNotNull(command);
		
		inputTypes = command.getInputTypes();
		assertNotNull(inputTypes);
		assertEquals(1, inputTypes.length);
		
		assertEquals("text.html", command.getScope());
		
		inputType = inputTypes[0];
		assertEquals(InputType.NONE, inputType);
		
		assertEquals("discard", command.getOutputType());
		
		// get third command
		command = bundle.getCommandByName("Test 3");
		assertNotNull(command);
		
		assertEquals("source.ruby", command.getScope());
		
		inputTypes = command.getInputTypes();
		assertNotNull(inputTypes);
		assertEquals(1, inputTypes.length);
		
		inputType = inputTypes[0];
		assertEquals(InputType.NONE, inputType);
		
		assertEquals("discard", command.getOutputType());
	}
}
