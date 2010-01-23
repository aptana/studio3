package com.aptana.scripting.model;

public class CommandTests extends BundleTestBase
{
	/**
	 * executeCommand
	 * 
	 * @param bundleName
	 * @param commandName
	 * @return
	 */
	protected String executeCommand(String bundleName, String commandName)
	{
		BundleElement bundle = this.loadBundle(bundleName, BundleScope.PROJECT);

		// get command
		CommandElement command = bundle.getCommandByName(commandName);
		assertNotNull(command);
		
		// run command and grab result
		CommandResult result = command.execute();
		assertNotNull(result);
		
		// return string result
		return result.getOutputString();
	}
	
	/**
	 * invokeStringCommandTest
	 */
	public void testInvokeStringCommandTest()
	{
		String resultText = this.executeCommand("invokeString", "Test");
		
		assertEquals("hello string", resultText);
	}

	/**
	 * invokeBlockCommandTest
	 */
	public void tsetInvokeBlockCommandTest()
	{
		String resultText = this.executeCommand("invokeBlock", "Test");
		
		assertEquals("hello", resultText);
	}
	
	/**
	 * testRequireInBlock
	 */
	public void testRequireInBlock()
	{
		String resultText = this.executeCommand("requireInCommand", "MyCommand");
		
		assertEquals("My Thing Name", resultText);
	}
}
