/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
				job.setSystem(true);
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
