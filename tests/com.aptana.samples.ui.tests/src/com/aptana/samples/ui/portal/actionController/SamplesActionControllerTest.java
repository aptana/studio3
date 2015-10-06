/**
 * Aptana Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.portal.actionController;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.ui.portal.actionController.SamplesActionController.SAMPLE_INFO;

public class SamplesActionControllerTest
{
	SamplesActionController samplesController;
	ISamplesManager samplesManager;
	@SuppressWarnings("rawtypes")
	HashMap samplesData;
	public static final Object JSON_OK = MessageFormat.format("\"{0}\"", "ok"); //$NON-NLS-1$
	public static final Object JSON_ERROR = MessageFormat.format("\"{0}\"", "error"); //$NON-NLS-1$

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setUp() throws Exception
	{
		samplesController = new SamplesActionController();
		samplesData = new HashMap();
		// Set up samples data
		samplesData.put(SAMPLE_INFO.ID.toString(), "sampleId");
		samplesData.put(SAMPLE_INFO.NAME.toString(), "Test Sample");
		samplesData.put(SAMPLE_INFO.CATEGORY.toString(), "com.appcelerator.titanium.mobile.samples.category");
		samplesData.put(SAMPLE_INFO.PATH.toString(),
				"git://github.com/appcelerator-developer-relations/Sample.Mapping.git");
		samplesData.put(SAMPLE_INFO.DESCRIPTION.toString(), "This is a test sample");
		samplesData.put(SAMPLE_INFO.IMAGE.toString(),
				"http://preview.appcelerator.com/dashboard/img/icons/icon_geo.png");
		samplesData.put(SAMPLE_INFO.NATURES.toString(), new String[] { "com.aptana.projects.webnature",
				"com.appcelerator.titanium.mobile.nature" });
		samplesManager = SamplesPlugin.getDefault().getSamplesManager();

	}

	@Test
	public void testAddSample()
	{

		Object response = samplesController.addSample(new Object[] { samplesData });
		assertEquals(JSON_OK, response);
		IProjectSample sample = samplesManager.getSample("sampleId");
		assertNotNull(sample);
		assertEquals("sampleId", sample.getId());
		assertEquals("Test Sample", sample.getName());
		assertEquals("com.appcelerator.titanium.mobile.samples.category", sample.getCategory().getId());
		assertEquals("git://github.com/appcelerator-developer-relations/Sample.Mapping.git", sample.getLocation());
		assertEquals("This is a test sample", sample.getDescription());
		assertTrue(sample.isRemote());
		assertArrayEquals(new String[] { "com.aptana.projects.webnature", "com.appcelerator.titanium.mobile.nature" },
				sample.getNatures());
	}

	@Test
	public void testAddSampleMissingAttribute()
	{
		// Remove the required attribute id
		samplesData.remove(SAMPLE_INFO.ID.toString());
		Object response = samplesController.addSample(new Object[] { samplesData });
		assertEquals(JSON_ERROR, response.toString());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddSampleLocalLocation()
	{
		// Overwrite path and id
		samplesData.put(SAMPLE_INFO.ID.toString(), "sampleId123");

		// create a temp file and add it to path
		File temp = null;
		try
		{
			temp = File.createTempFile("temp-file-name", ".tmp");
			samplesData.put(SAMPLE_INFO.PATH.toString(), temp.getAbsolutePath());

			Object response = samplesController.addSample(new Object[] { samplesData });
			assertEquals(JSON_OK, response.toString());
			IProjectSample sample = samplesManager.getSample("sampleId123");
			assertNotNull(sample);
			assertEquals("sampleId123", sample.getId());
			assertEquals("Test Sample", sample.getName());
			assertEquals("com.appcelerator.titanium.mobile.samples.category", sample.getCategory().getId());
			assertEquals(temp.getAbsolutePath(), sample.getLocation());
			assertEquals("This is a test sample", sample.getDescription());
			assertFalse(sample.isRemote());
			// Check that default nature is added
			assertArrayEquals(
					new String[] { "com.aptana.projects.webnature", "com.appcelerator.titanium.mobile.nature" },
					sample.getNatures());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (temp != null && temp.exists())
			{
				temp.delete();
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddSampleMissingNature()
	{
		// Overwrite id
		samplesData.put(SAMPLE_INFO.ID.toString(), "sampleId345");
		// Remove the natures
		samplesData.remove(SAMPLE_INFO.NATURES.toString());
		Object response = samplesController.addSample(new Object[] { samplesData });
		assertEquals(JSON_OK, response.toString());
		IProjectSample sample = samplesManager.getSample("sampleId345");
		assertNotNull(sample);
		assertEquals("sampleId345", sample.getId());
		assertEquals("Test Sample", sample.getName());
		assertEquals("com.appcelerator.titanium.mobile.samples.category", sample.getCategory().getId());
		assertEquals("git://github.com/appcelerator-developer-relations/Sample.Mapping.git", sample.getLocation());
		assertEquals("This is a test sample", sample.getDescription());
		assertTrue(sample.isRemote());
		// Check that default nature is added
		assertArrayEquals(new String[] { SamplesActionController.WEB_NATURE, SamplesActionController.MOBILE_NATURE }, sample.getNatures());
	}

	@After
	public void tearDown()
	{
		samplesController = null;
		samplesManager = null;
		samplesData = null;
	}
}
