package com.aptana.scripting.model;

import java.io.File;
import java.io.IOException;

public class CommandBlockRunnerTests extends BundleTestBase
{

	// public void testCommandBlockRunner()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testAfterExecute()
	// {
	// fail("Not yet implemented");
	// }
	//
	public void testApplyEnvironment() throws IOException
	{
		BundleElement bundle = this.loadBundle("invokeString", BundlePrecedence.PROJECT);

		// get command
		CommandElement command = bundle.getCommandByName("Test");
		assertNotNull(command);

		File f = File.createTempFile("snippet", "rb");
		EnvironmentElement ee = createEnvironment(f.getAbsolutePath(), "environment", null);
		bundle.addChild(ee);

		CommandContext cc = new CommandContext(command);

		CommandBlockRunner cbr = new CommandBlockRunner(command, cc, BundleManager.getInstance().getBundleLoadPaths(
				bundle.getBundleDirectory()));
		cbr.applyEnvironment();
	}

	//
	// public void testApplyStreams()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testBeforeExecute()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testCloseStreams()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testExecuteBlock()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testGetRuntime()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testRunIProgressMonitor()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetConsoleIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetConsoleOutputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetErrorWriterIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetErrorWriterOutputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetReaderInputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetReaderIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetWriterIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetWriterOutputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testUnapplyEnvironment()
	// {
	// fail("Not yet implemented");
	// }
	//
	// public void testUnapplyStreams()
	// {
	// fail("Not yet implemented");
	// }
	//

	/**
	 * Create a environment block
	 * 
	 * @param path
	 * @param displayName
	 * @param scope
	 * @return
	 */
	protected EnvironmentElement createEnvironment(String path, String displayName, String scope)
	{
		EnvironmentElement se = new EnvironmentElement(path);
		se.setDisplayName(displayName);
		se.setScope(scope);

		return se;
	}

	/**
	 * Create a command block
	 * 
	 * @param path
	 * @param displayName
	 * @param scope
	 * @return
	 */
	protected CommandElement createCommand(String path, String displayName, String scope)
	{
		CommandElement ce = new CommandElement(path);
		ce.setDisplayName(displayName);
		ce.setScope(scope);

		return ce;
	}

}
