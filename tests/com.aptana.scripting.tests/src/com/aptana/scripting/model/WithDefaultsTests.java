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

import java.util.List;

import org.junit.Test;

public class WithDefaultsTests extends BundleTestBase
{
	/**
	 * testWithDefaults
	 */
	@Test
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
	@Test
	public void testWithDefaultInsideBundle()
	{
		this.loadBundleEntry("withDefaultsInsideBundle", BundlePrecedence.PROJECT);
		BundleEntry entry = BundleTestBase.getBundleManagerInstance().getBundleEntry("Bundle Reference");
		assertNotNull(entry);

		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.size());

		BundleElement bundle = bundles.get(0);
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
	@Test
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
