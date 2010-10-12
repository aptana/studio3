/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
	public void testRequireInBlock()
	{
		String resultText = this.executeCommand("requireInCommand", "MyCommand");
		
		assertEquals("My Thing Name", resultText);
	}
}
