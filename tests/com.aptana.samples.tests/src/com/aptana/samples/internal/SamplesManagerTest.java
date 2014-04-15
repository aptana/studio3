/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.internal;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.List;

import junit.framework.TestCase;

import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SamplesReference;

public class SamplesManagerTest
{

	private ISamplesManager samplesManager;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		samplesManager = SamplesPlugin.getDefault().getSamplesManager();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		samplesManager = null;
//		super.tearDown();
	}

	@Test
	public void testGetCategories()
	{
		List<SampleCategory> categories = samplesManager.getCategories();

		assertTrue(categories.size() >= 2);

		SampleCategory category = findCategoryById(categories, "com.aptana.projects.samples.web.category");
		assertNotNull(category);
		assertEquals("com.aptana.projects.samples.web.category", category.getId());
		assertEquals("Web", category.getName());
		assertNotNull(category.getIconFile());

		category = findCategoryById(categories, "com.aptana.samples.tests.category");
		assertNotNull(category);
		assertEquals("com.aptana.samples.tests.category", category.getId());
		assertEquals("Test Samples", category.getName());
		assertEquals("platform:/plugin/com.aptana.samples/icons/category.png", category.getIconFile().toString());
	}

	protected SampleCategory findCategoryById(List<SampleCategory> categories, String categoryId)
	{
		for (SampleCategory category : categories)
		{
			if (category.getId().equals(categoryId))
			{
				return category;
			}
		}
		return null;
	}

	@Test
	public void testGetSamplesForCategory()
	{
		List<IProjectSample> samples = samplesManager.getSamplesForCategory("com.aptana.samples.tests.category");
		assertEquals(1, samples.size());

		IProjectSample remoteSample = samples.get(0);
		assertTrue(remoteSample.isRemote());
		assertEquals("com.aptana.samples.tests.remote", remoteSample.getId());
		assertEquals("Remote", remoteSample.getName());
		assertEquals("Testing remote sample", remoteSample.getDescription());
		assertEquals("git://github.com/aptana/remote_sample.git", remoteSample.getLocation());

		assertEquals(samplesManager.getCategories().get(1), remoteSample.getCategory());
		assertNull(remoteSample.getProjectHandler());

		String[] natures = remoteSample.getNatures();
		assertEquals(1, natures.length);
		assertEquals("com.aptana.projects.webnature", natures[0]);

		if (remoteSample instanceof SamplesReference)
		{
			SamplesReference sample = (SamplesReference) remoteSample;
			assertNull(sample.getInfoFile());
			assertNull(sample.getPreviewHandler());
			String[] includes = sample.getIncludePaths();
			assertEquals(0, includes.length);
		}

	}
}
