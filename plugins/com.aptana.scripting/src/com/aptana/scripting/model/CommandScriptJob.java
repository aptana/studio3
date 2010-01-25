package com.aptana.scripting.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public class CommandScriptJob extends AbstractScriptJob
{
	/**
	 * CommandScriptJob
	 */
	public CommandScriptJob()
	{
		this("");
	}
	
	/**
	 * CommandScriptJob
	 * 
	 * @param name
	 */
	public CommandScriptJob(String name)
	{
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor)
	{
		return null;
	}
}
