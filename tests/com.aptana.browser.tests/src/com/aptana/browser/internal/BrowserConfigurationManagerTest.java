/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.browser.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.browser.BrowserPlugin;

public class BrowserConfigurationManagerTest
{

	private BrowserConfigurationManager manager;

	@Before
	public void setUp() throws Exception
	{
		manager = BrowserPlugin.getDefault().getBrowserConfigurationManager();
	}

	@After
	public void tearDown() throws Exception
	{
		manager.clear();
		manager = null;
	}
	
	@Test
	public void testGetSizeCategories()
	{
		BrowserSizeCategory[] categories = manager.getSizeCategories();
		assertEquals(5, categories.length);
		// Should be: Computer, Mobile, Test, Ordered, Default

		// Default should be the last one
		BrowserSizeCategory defaultCategory = categories[4];
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

		// Category with no order defaults to order of 127, then sorted by name, so Test should be our third category overall
		BrowserSizeCategory testCategory = categories[2];
		assertEquals("com.aptana.browser.tests.sizeCategory", testCategory.getId());
		assertEquals("Test", testCategory.getName());
		assertEquals(Byte.MAX_VALUE, testCategory.getOrder());
		assertEquals("Test", testCategory.toString());
		sizes = testCategory.getSizes();
		assertEquals(1, sizes.length);

		size = sizes[0];
		assertEquals(testCategory, size.getCategory());
		assertEquals("iPhone Vertical", size.getName());
		assertEquals(460, size.getHeight());
		assertEquals(320, size.getWidth());
		BrowserBackgroundImage browserImage = size.getImage();
		assertEquals("com.aptana.browser.tests.backgroundImage.iphone.vertical", browserImage.getId());
		assertEquals(35, browserImage.getHorizontalIndent());
		assertEquals(150, browserImage.getVerticalIndent());
		assertEquals("iPhone Vertical", size.toString());
		assertTrue(browserImage.isBlackBackground());
		assertEquals(AbstractUIPlugin.imageDescriptorFromPlugin("com.aptana.browser", "images/iphone_ver.png"),
				browserImage.getImageDescriptor());

		// Ordered category. Value of 200 comes after unordered default values, before default which is defaults to MAX_INT order
		// So it should be the 4th (second last) category
		BrowserSizeCategory orderedCategory = categories[3];
		assertEquals("com.aptana.browser.tests.sizeCategory.ordered", orderedCategory.getId());
		assertEquals("Ordered", orderedCategory.getName());
		assertEquals(200, orderedCategory.getOrder());
		assertTrue(orderedCategory.compareTo(testCategory) > 0);
		sizes = orderedCategory.getSizes();
		assertEquals(0, sizes.length);
	}
}
