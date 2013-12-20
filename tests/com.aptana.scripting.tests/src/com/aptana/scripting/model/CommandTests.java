/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		BundleElement bundle = this.loadBundle(bundleName, BundlePrecedence.PROJECT);

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
	@Test
	public void testInvokeStringCommandTest()
	{
		String resultText = this.executeCommand("invokeString", "Test");

		// NOTE: we have to use endsWith here because msysgit prints out /etc/motd
		// when using 'bash -l'. Most likely users will turn this off, but we need
		// to perform the test this way to pass in a default install
		assertTrue(resultText.endsWith("hello string"));
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
	@Test
	public void testRequireInBlock()
	{
		String resultText = this.executeCommand("requireInCommand", "MyCommand");

		assertEquals("My Thing Name", resultText);
	}
}
