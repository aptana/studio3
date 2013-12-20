package com.aptana.scripting.model;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class CommandBlockRunnerTests extends BundleTestBase
{

	// @Test public void testCommandBlockRunner()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testAfterExecute()
	// {
	// fail("Not yet implemented");
	// }
	//
	@Test
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
	// @Test public void testApplyStreams()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testBeforeExecute()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testCloseStreams()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testExecuteBlock()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testGetRuntime()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testRunIProgressMonitor()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetConsoleIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetConsoleOutputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetErrorWriterIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetErrorWriterOutputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetReaderInputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetReaderIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetWriterIRubyObject()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testSetWriterOutputStream()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testUnapplyEnvironment()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test public void testUnapplyStreams()
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
