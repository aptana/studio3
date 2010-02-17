package com.aptana.scripting.ui.views;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.LoadCycleListener;

public class BundleView extends ViewPart
{
	TreeViewer _treeViewer;
	BundleViewContentProvider _contentProvider;
	BundleViewLabelProvider _labelProvider;
	
	/**
	 * BundleView
	 */
	public BundleView()
	{
		this._contentProvider = new BundleViewContentProvider();
		this._labelProvider = new BundleViewLabelProvider();
	}

	/**
	 * createPartControl
	 */
	public void createPartControl(Composite parent)
	{
		this._treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		this._treeViewer.setContentProvider(this._contentProvider);
		this._treeViewer.setLabelProvider(_labelProvider);
		this._treeViewer.setInput(BundleManager.getInstance());
		
		BundleManager.getInstance().addLoadCycleListener(new LoadCycleListener()
		{
			public void scriptLoaded(File script)
			{
				refresh();
			}

			public void scriptReloaded(File script)
			{
				refresh();
			}

			public void scriptUnloaded(File script)
			{
				refresh();
			}
		});
	}
	
	public void refresh()
	{
		UIJob job = new UIJob("Refresh Bundles View")
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				_treeViewer.refresh();
				
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	/**
	 * setFocus
	 */
	public void setFocus()
	{
	}
}
