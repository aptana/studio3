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
