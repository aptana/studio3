/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.build;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.logging.IdeLog;
import com.aptana.index.core.build.BuildContext;

/**
 * Intended to wrap an {@link IConfigurationElement} and lazily instantiate an {@link IBuildParticipant} on-demand only
 * when necessary. This can help avoid loading instances (and their containing plugins) if they're never used.
 * 
 * @author cwilliams
 */
public class LazyBuildParticipant extends AbstractBuildParticipant
{

	private IConfigurationElement ice;
	private IBuildParticipant participant;

	LazyBuildParticipant(IConfigurationElement ice) throws CoreException
	{
		this.ice = ice;
		setInitializationData(ice, null, null);
	}

	public void clean(IProject project, IProgressMonitor monitor)
	{
		getParticipant().clean(project, monitor);
	}

	public synchronized IBuildParticipant getParticipant()
	{
		if (participant == null)
		{
			try
			{
				participant = (IBuildParticipant) ice.createExecutableExtension("class"); //$NON-NLS-1$
			}
			catch (CoreException e)
			{
				IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
			}
		}
		return participant;
	}

	public void buildStarting(IProject project, int kind, IProgressMonitor monitor)
	{
		getParticipant().buildStarting(project, kind, monitor);
	}

	public void buildEnding(IProgressMonitor monitor)
	{
		getParticipant().buildEnding(monitor);
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		getParticipant().buildFile(context, monitor);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		getParticipant().deleteFile(context, monitor);
	}

	@Override
	public boolean isEnabled(BuildType type)
	{
		return getParticipant().isEnabled(type);
	}

	@Override
	public boolean isRequired()
	{
		// FIXME is there any way to avoid instantiating here?
		return getParticipant().isRequired();
	}

	@Override
	public void restoreDefaults()
	{
		getParticipant().restoreDefaults();
	}
}
