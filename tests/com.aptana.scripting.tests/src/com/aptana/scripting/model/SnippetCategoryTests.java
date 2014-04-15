/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.junit.Before;
import org.junit.Test;

import com.aptana.scripting.model.filters.IModelFilter;

public class SnippetCategoryTests extends BundleTestBase
{
	private List<SnippetCategoryElement> categories = null;

	@Before
	public void setUp() throws Exception
	{
		String bundleName = "bundleWithSnippetCategory";
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
		categories = entry.getSnippetCategories();
	}

	@Test
	public void testGetBundleSnippetCategoriesUsingFilter()
	{
		List<SnippetCategoryElement> snippetCategories = BundleManager.getInstance().getSnippetCategories(
				new IModelFilter()
				{

					public boolean include(AbstractElement element)
					{
						if (element instanceof SnippetCategoryElement)
						{
							return ((SnippetCategoryElement) element).getIconPath() != null;
						}
						return false;
					}
				});

		assertEquals("getSnippetCategories(IModelFilter) return the wrong elements", 5, snippetCategories.size());
	}

	@Test
	public void testGetBundleSnippetCategoriesUsingNullFilter()
	{
		List<SnippetCategoryElement> snippetCategories = BundleManager.getInstance().getSnippetCategories(null);
		assertEquals("getSnippetCategories(IModelFilter) return the wrong elements", 6, snippetCategories.size());
	}

	@Test
	public void testGetSnippetCategories()
	{
		assertEquals("getSnippetCategories() returned the wrong contents", 6, categories.size());
	}

	@Test
	public void testValidIconURL()
	{
		validateCategory("GoodCategory", "close.gif", true);
	}

	@Test
	public void testInValidIconURL()
	{
		validateCategory("InvalidIconCategory", "invalid.gif", false);
	}

	@Test
	public void testMalformedIconURL()
	{
		validateCategory("MalformedIconCategory", "\\ad/a\"\"invalid.gif", false);
	}

	@Test
	public void testNoIcon()
	{
		validateCategory("NoIconCategory", null, false);
	}

	@Test
	public void testWebIconURL()
	{
		validateCategory("UrlCategory", "http://preview.appcelerator.com/dashboard/img/icons/icon_to_do_list.png", true);
	}

	@Test
	public void testInvalidWebIconURL()
	{
		validateCategory("InvalidUrlCategory",
				"http://preview.appcelerator.com/dashboard/img/icons/icon_to_do_listxxx.png", false);
	}

	private SnippetCategoryElement validateCategory(String categoryName, String path, boolean isValidPath)
	{
		SnippetCategoryElement category = getCategoryByName(categoryName);
		assertNotNull(categoryName + " was not found", category);
		assertEquals(categoryName + " path does not match", path, category.getIconPath());

		if (isValidPath)
		{
			assertNotNull(category.getIconURL());
			try
			{
				ImageDescriptor descriptor = ImageDescriptor.createFromURL(category.getIconURL());
				assertNotNull(descriptor);
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		else
		{
			URL iconURL = category.getIconURL();

			if (category.getIconPath() == null)
			{
				assertNull(categoryName + " iconUrl should be null", iconURL);
			}

			if (iconURL != null)
			{
				try
				{
					ImageDescriptor descriptor = ImageDescriptor.createFromURL(category.getIconURL());
					Image image = descriptor.createImage(false);
					assertNull(image);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return category;
	}

	private SnippetCategoryElement getCategoryByName(String name)
	{
		for (SnippetCategoryElement element : categories)
		{
			if (element.getDisplayName().equals(name))
			{
				return element;
			}
		}

		return null;
	}
}
