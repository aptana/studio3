/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.browser.internal;

import junit.framework.TestCase;

import com.aptana.browser.BrowserPlugin;

public class BrowserConfigurationManagerTest extends TestCase
{

	private BrowserConfigurationManager manager;

	@Override
	protected void setUp() throws Exception
	{
		manager = BrowserPlugin.getDefault().getBrowserConfigurationManager();
	}

	@Override
	protected void tearDown() throws Exception
	{
		manager.clear();
		manager = null;
	}

	public void testGetSizeCategories()
	{
		BrowserSizeCategory[] categories = manager.getSizeCategories();
		assertEquals(5, categories.length);

		BrowserSizeCategory defaultCategory = categories[0];
		assertEquals("default", defaultCategory.getId());
		assertEquals("Default", defaultCategory.getName());
		assertEquals(Integer.MAX_VALUE, defaultCategory.getOrder());
		BrowserSize[] sizes = defaultCategory.getSizes();
		assertEquals(1, sizes.length);

		BrowserSize size = sizes[0];
		assertEquals(defaultCategory, size.getCategory());
		assertEquals("No Category and Image", size.getName());
		assertEquals(1024, size.getHeight());
		assertEquals(1280, size.getWidth());
		assertNull(size.getImage());

		BrowserSizeCategory testCategory = categories[1];
		assertEquals("com.aptana.browser.tests.sizeCategory", testCategory.getId());
		assertEquals("Test", testCategory.getName());
		assertEquals(Byte.MAX_VALUE, testCategory.getOrder());
		sizes = testCategory.getSizes();
		assertEquals(1, sizes.length);

		size = sizes[0];
		assertEquals(testCategory, size.getCategory());
		assertEquals("iPhone Vertical", size.getName());
		assertEquals(460, size.getHeight());
		assertEquals(320, size.getWidth());
		assertNotNull(size.getImage());

		BrowserSizeCategory orderedCategory = categories[3];
		assertEquals("com.aptana.browser.tests.sizeCategory.ordered", orderedCategory.getId());
		assertEquals("Ordered", orderedCategory.getName());
		assertEquals(200, orderedCategory.getOrder());
		sizes = orderedCategory.getSizes();
		assertEquals(0, sizes.length);
	}
}
