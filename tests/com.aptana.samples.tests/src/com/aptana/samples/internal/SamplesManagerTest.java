/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.internal;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.ResourceUtil;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SamplesReference;

public class SamplesManagerTest extends TestCase
{

	private ISamplesManager samplesManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		samplesManager = SamplesPlugin.getDefault().getSamplesManager();
	}

	@Override
	protected void tearDown() throws Exception
	{
		samplesManager = null;
		super.tearDown();
	}

	public void testGetCategories()
	{
		List<SampleCategory> categories = samplesManager.getCategories();

		assertEquals(2, categories.size());

		SampleCategory category = categories.get(0);
		assertEquals("com.aptana.projects.samples.web.category", category.getId());
		assertEquals("Web", category.getName());
		assertNotNull(category.getIconFile());

		category = categories.get(1);
		assertEquals("com.aptana.samples.tests.category", category.getId());
		assertEquals("Test Samples", category.getName());
		assertEquals(getFullPath("icons/category.png"), category.getIconFile());
	}

	public void testGetSamplesForCategory()
	{
		List<SamplesReference> samples = samplesManager.getSamplesForCategory("com.aptana.samples.tests.category");
		assertEquals(1, samples.size());

		SamplesReference remoteSample = samples.get(0);
		assertTrue(remoteSample.isRemote());
		assertEquals("com.aptana.samples.tests.remote", remoteSample.getId());
		assertEquals("Remote", remoteSample.getName());
		assertEquals("Testing remote sample", remoteSample.getDescription());
		assertEquals("git://github.com/aptana/remote_sample.git", remoteSample.getLocation());

		assertEquals(samplesManager.getCategories().get(1), remoteSample.getCategory());
		assertNull(remoteSample.getInfoFile());
		assertNull(remoteSample.getPreviewHandler());
		assertNull(remoteSample.getProjectHandler());

		String[] natures = remoteSample.getNatures();
		assertEquals(1, natures.length);
		assertEquals("com.aptana.projects.webnature", natures[0]);
		String[] includes = remoteSample.getIncludePaths();
		assertEquals(0, includes.length);
	}

	private static String getFullPath(String entryPath)
	{
		return ResourceUtil.resourcePathToString(Platform.getBundle("com.aptana.samples").getEntry(entryPath));
	}
}
