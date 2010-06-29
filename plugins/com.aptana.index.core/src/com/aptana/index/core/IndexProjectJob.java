package com.aptana.index.core;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;

class IndexProjectJob extends IndexContainerJob
{

	public IndexProjectJob(IProject project)
	{
		super(MessageFormat.format("Indexing project {0}", project.getName()), project.getLocationURI());
	}
}