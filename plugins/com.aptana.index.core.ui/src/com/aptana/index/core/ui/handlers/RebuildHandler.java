/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;

public class RebuildHandler extends AbstractHandler
{
	private List<IProject> _projects;

	/**
	 * RebuildHandler
	 */
	public RebuildHandler()
	{
		this._projects = new ArrayList<IProject>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		for (final IProject p : this._projects)
		{
			Job job = new Job(MessageFormat.format("Rebuilding {0}", p.getName())) //$NON-NLS-1$
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						p.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
					}
					catch (CoreException e)
					{
						return e.getStatus();
					}
					return Status.OK_STATUS;
				}

			};
			job.schedule();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		return this._projects.isEmpty() == false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#setEnabled(java.lang.Object)
	 */
	@Override
	public void setEnabled(Object evaluationContext)
	{
		// clear cached selection
		this._projects.clear();

		if (evaluationContext instanceof EvaluationContext)
		{
			EvaluationContext context = (EvaluationContext) evaluationContext;
			Object value = context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);

			if (value instanceof ISelection)
			{
				ISelection selection = (ISelection) value;

				if (selection instanceof IStructuredSelection && selection.isEmpty() == false)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;

					for (Object object : structuredSelection.toArray())
					{
						if (object instanceof IProject)
						{
							this._projects.add((IProject) object);
						}
					}
				}
			}
		}
	}
}
