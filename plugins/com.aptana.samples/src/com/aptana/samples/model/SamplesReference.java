/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.samples.handlers.ISamplePreviewHandler;
import com.aptana.samples.handlers.ISampleProjectHandler;

public class SamplesReference
{

	private static final String ATTR_PROJECT_HANDLER = "projectHandler"; //$NON-NLS-1$
	private static final String ATTR_PREVIEW_HANDLER = "previewHandler"; //$NON-NLS-1$

	private final SampleCategory category;
	private final String path;
	private final IConfigurationElement configElement;

	private String name;
	private boolean isRemote;
	private String infoFile;
	private ISampleProjectHandler projectHandler;
	private ISamplePreviewHandler previewHandler;
	private String[] natures;
	private String[] includePaths;

	private List<SampleEntry> samples;

	public SamplesReference(SampleCategory category, String path, boolean isRemote, IConfigurationElement element)
	{
		this.category = category;
		this.path = path;
		this.isRemote = isRemote;
		configElement = element;
		natures = new String[0];
		includePaths = new String[0];
		samples = new ArrayList<SampleEntry>();

		if (!isRemote)
		{
			loadSamples();
		}
	}

	public SampleCategory getCategory()
	{
		return category;
	}

	public String getName()
	{
		return name;
	}

	public String getPath()
	{
		return path;
	}

	public String getInfoFile()
	{
		return infoFile;
	}

	public ISampleProjectHandler getProjectHandler()
	{
		if (projectHandler == null)
		{
			try
			{
				projectHandler = (ISampleProjectHandler) configElement.createExecutableExtension(ATTR_PROJECT_HANDLER);
			}
			catch (CoreException e)
			{
				// ignores the exception since it's optional
			}
		}
		return projectHandler;
	}

	public ISamplePreviewHandler getPreviewHandler()
	{
		if (previewHandler == null)
		{
			try
			{
				previewHandler = (ISamplePreviewHandler) configElement.createExecutableExtension(ATTR_PREVIEW_HANDLER);
			}
			catch (CoreException e)
			{
				// ignores the exception since it's optional
			}
		}
		return previewHandler;
	}

	public String[] getNatures()
	{
		return natures;
	}

	public String[] getIncludePaths()
	{
		return includePaths;
	}

	public List<SampleEntry> getSamples()
	{
		return Collections.unmodifiableList(samples);
	}

	public boolean isRemote()
	{
		return isRemote;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setInfoFile(String infoFile)
	{
		this.infoFile = infoFile;
	}

	public void setNatures(String[] natures)
	{
		this.natures = natures;
	}

	public void setIncludePaths(String[] paths)
	{
		includePaths = paths;
	}

	private void loadSamples()
	{
		samples.clear();

		File samplesDirectory = new File(path);
		File[] sampleFiles = samplesDirectory.listFiles();
		if (sampleFiles != null)
		{
			for (File file : sampleFiles)
			{
				if (file.isDirectory())
				{
					samples.add(new SampleEntry(file, this, true));
				}
			}
		}
	}
}
