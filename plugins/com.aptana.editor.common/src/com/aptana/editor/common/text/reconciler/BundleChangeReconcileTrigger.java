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

import com.aptana.core.util.EclipseUtil;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.LoadCycleListener;

/**
 * @author Chris Williams
 * @author Max Stepanov
 */
public class BundleChangeReconcileTrigger implements LoadCycleListener
{

	private static final String BUNDLE_RB = "bundle.rb"; //$NON-NLS-1$

	private final CommonReconciler reconciler;

	private final Job job = new Job("Force reconcile on bundle change") //$NON-NLS-1$
	{
		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			reconciler.forceReconciling();
			return Status.OK_STATUS;
		}
	};

	/**
	 *
	 */
	public BundleChangeReconcileTrigger(CommonReconciler reconciler)
	{
		this.reconciler = reconciler;
		BundleManager.getInstance().addLoadCycleListener(this);
	}

	public void dispose()
	{
		BundleManager.getInstance().removeLoadCycleListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptLoaded(java.io.File)
	 */
	public void scriptLoaded(File script)
	{
		bundleFileChanged(script);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptReloaded(java.io.File)
	 */
	public void scriptReloaded(File script)
	{
		bundleFileChanged(script);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptUnloaded(java.io.File)
	 */
	public void scriptUnloaded(File script)
	{
		bundleFileChanged(script);
	}

	private void bundleFileChanged(File script)
	{
		if (script == null || !script.getName().equals(BUNDLE_RB))
		{
			return;
		}
		// Run in a job on a delay and cancel/reschedule if it already exists and is scheduled... This should
		// basically only make us run once if we get hit multiple times in a row. We'll still probably run a few
		// times, but this should cut it down a lot.
		job.cancel();
		EclipseUtil.setSystemForJob(job);
		job.schedule(750);
	}

}
