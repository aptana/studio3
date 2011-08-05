/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.internal;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.ResourceUtil;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SampleEntry;
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

		assertEquals(1, categories.size());

		SampleCategory category = categories.get(0);
		assertEquals("com.aptana.samples.tests.category", category.getId());
		assertEquals("Test Samples", category.getName());
		assertEquals(getFullPath("icons/category.png"), category.getIconFile());
	}

	public void testGetSamplesForCategory()
	{
		List<SamplesReference> samples = samplesManager.getSamplesForCategory("com.aptana.samples.tests.category");
		assertEquals(2, samples.size());

		SamplesReference localSample = samples.get(0);
		assertFalse(localSample.isRemote());

		assertEquals(samplesManager.getCategories().get(0), localSample.getCategory());
		assertEquals("Local", localSample.getName());
		assertNull(localSample.getDescriptionText());
		assertEquals(getFullPath("samples/info.txt"), localSample.getInfoFile());
		assertEquals(getFullPath("samples/"), localSample.getPath());
		assertNull(localSample.getPreviewHandler());
		assertNull(localSample.getProjectHandler());

		String[] natures = localSample.getNatures();
		assertEquals(1, natures.length);
		assertEquals("com.aptana.projects.webnature", natures[0]);
		String[] includes = localSample.getIncludePaths();
		assertEquals(1, includes.length);
		assertEquals(getFullPath("lib/"), includes[0]);

		List<SampleEntry> entries = localSample.getSamples();
		assertEquals(1, entries.size());

		SampleEntry entry = entries.get(0);
		assertEquals(localSample, entry.getParent());
		assertEquals(new File(getFullPath("samples/test")), entry.getFile());
		assertEquals("Testing local sample", entry.getDescription());
		assertTrue(entry.isRoot());

		SampleEntry[] subentries = entry.getSubEntries();
		assertEquals(1, subentries.length);
		assertEquals(new File(getFullPath("samples/test/index.html")), subentries[0].getFile());
		assertFalse(subentries[0].isRoot());

		SamplesReference remoteSample = samples.get(1);
		assertTrue(remoteSample.isRemote());
		assertEquals("Remote", remoteSample.getName());
		assertEquals("Testing remote sample", remoteSample.getDescriptionText());
		assertEquals("git://github.com/aptana/remote_sample.git", remoteSample.getPath());
	}

	private static String getFullPath(String entryPath)
	{
		return ResourceUtil.resourcePathToString(Platform.getBundle("com.aptana.samples.tests").getEntry(entryPath));
	}
}
