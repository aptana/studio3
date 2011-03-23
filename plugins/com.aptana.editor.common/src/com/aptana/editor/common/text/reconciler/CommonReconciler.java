/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.util.EclipseUtil;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.LoadCycleListener;

public class CommonReconciler extends MonoReconciler
{

	private LoadCycleListener listener;

	/**
	 * Used for performance testing purposes so we can see if we've finished our first reconcile!
	 */
	@SuppressWarnings("unused")
	private boolean fIninitalProcessDone = false;

	public CommonReconciler(ITextEditor editor, IReconcilingStrategy strategy, boolean isIncremental)
	{
		super(strategy, isIncremental);
	}

	@Override
	public void install(ITextViewer textViewer)
	{
		super.install(textViewer);

		listener = new LoadCycleListener()
		{

			private Job job;

			public void scriptUnloaded(File script)
			{
				bundleFileChanged(script);
			}

			private void bundleFileChanged(File script)
			{
				if (script == null || !script.getName().equals("bundle.rb")) //$NON-NLS-1$
					return;
				// Run in a job on a delay and cancel/reschedule if it already exists and is scheduled... This should
				// basically only make us run once if we get hit multiple times in a row. We'll still probably run a few
				// times, but this should cut it down a lot.
				if (job != null)
				{
					job.cancel();
				}
				job = new Job("Force reconcile on bundle change") //$NON-NLS-1$
				{
					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						IProgressMonitor oldMonitor = getProgressMonitor();
						setProgressMonitor(monitor);
						process(null);
						setProgressMonitor(oldMonitor);
						return Status.OK_STATUS;
					}
				};
				job.setSystem(!EclipseUtil.showSystemJobs());
				job.schedule(750);
			}

			public void scriptReloaded(File script)
			{
				bundleFileChanged(script);
			}

			public void scriptLoaded(File script)
			{
				bundleFileChanged(script);
			}
		};
		BundleManager.getInstance().addLoadCycleListener(listener);
	}

	@Override
	public void uninstall()
	{
		try
		{
			BundleManager.getInstance().removeLoadCycleListener(listener);
		}
		finally
		{
			super.uninstall();
		}
	}

	@Override
	protected void initialProcess()
	{
		super.initialProcess();
		fIninitalProcessDone = true;
	}
}
