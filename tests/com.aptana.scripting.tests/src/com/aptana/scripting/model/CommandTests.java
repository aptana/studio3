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
		CommandResult result = command.execute(null);
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
		
		assertEquals("hello\n", resultText);
	}

	/**
	 * invokeBlockCommandTest
	 */
	public void tsetInvokeBlockCommandTest()
	{
		String resultText = this.executeCommand("invokeBlock", "Test");
		
		assertEquals("hello", resultText);
	}
}
