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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.samples.handlers.ISamplePreviewHandler;
import com.aptana.samples.handlers.ISampleProjectHandler;

public class SamplesReference
{

	private static final String ATTR_PROJECT_HANDLER = "projectHandler"; //$NON-NLS-1$
	private static final String ATTR_PREVIEW_HANDLER = "previewHandler"; //$NON-NLS-1$

	private final String name;
	private final String directory;
	private final IConfigurationElement configElement;

	private String infoFile;
	private String iconFile;
	private ISampleProjectHandler projectHandler;
	private ISamplePreviewHandler previewHandler;
	private String[] natures;
	private String[] includePaths;

	private List<SampleEntry> samples;

	public SamplesReference(String name, String directory, IConfigurationElement element)
	{
		this.name = name;
		this.directory = directory;
		configElement = element;
		natures = new String[0];
		includePaths = new String[0];
		samples = new ArrayList<SampleEntry>();

		loadSamples();
	}

	public String getName()
	{
		return name;
	}

	public String getDirectory()
	{
		return directory;
	}

	public String getInfoFile()
	{
		return infoFile;
	}

	public String getIconFile()
	{
		return iconFile;
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

	public SampleEntry[] getSamples()
	{
		return samples.toArray(new SampleEntry[samples.size()]);
	}

	public void setInfoFile(String infoFile)
	{
		this.infoFile = infoFile;
	}

	public void setIconFile(String iconFile)
	{
		this.iconFile = iconFile;
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

		File samplesDirectory = new File(directory);
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
