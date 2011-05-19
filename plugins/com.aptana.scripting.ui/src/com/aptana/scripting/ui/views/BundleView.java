/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.LoadCycleListener;
import com.aptana.theme.ThemePlugin;

public class BundleView extends ViewPart
{
	TreeViewer _treeViewer;
	BundleViewContentProvider _contentProvider;
	BundleViewLabelProvider _labelProvider;
	LoadCycleListener _loadCycleListener;

	/**
	 * BundleView
	 */
	public BundleView()
	{
	}

	/**
	 * addListeners
	 */
	private void addListeners()
	{
		this.listenForScriptChanges();
	}

	/**
	 * applyTheme
	 */
	private void applyTheme()
	{
		ThemePlugin.getDefault().getControlThemerFactory().apply(this._treeViewer);
	}

	/**
	 * createPartControl
	 */
	public void createPartControl(Composite parent)
	{
		this._treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this._contentProvider = new BundleViewContentProvider();
		this._labelProvider = new BundleViewLabelProvider();

		this._treeViewer.setContentProvider(this._contentProvider);
		this._treeViewer.setLabelProvider(_labelProvider);
		this._treeViewer.setInput(BundleManager.getInstance());
		this._treeViewer.setSorter(new ViewerSorter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
			 * java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				String name1 = null;
				String name2 = null;

				if (viewer != null && viewer instanceof ContentViewer)
				{
					IBaseLabelProvider provider = ((ContentViewer) viewer).getLabelProvider();

					if (provider instanceof ILabelProvider)
					{
						ILabelProvider labelProvider = (ILabelProvider) provider;

						name1 = labelProvider.getText(e1);
						name2 = labelProvider.getText(e2);
					}
				}

				if (name1 == null)
				{
					name1 = e1.toString();
				}
				if (name2 == null)
				{
					name2 = e2.toString();
				}

				return name1.compareTo(name2);
			}
		});

		// add selection provider
		this.getSite().setSelectionProvider(this._treeViewer);

		// listen to theme changes
		this.hookContextMenu();
		this.addListeners();
		this.applyTheme();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		// remove selection provider
		this.getSite().setSelectionProvider(null);

		// remove load cycle listener
		BundleManager.getInstance().removeLoadCycleListener(this._loadCycleListener);

		ThemePlugin.getDefault().getControlThemerFactory().dispose(this._treeViewer);

		super.dispose();
	}

	/**
	 * fillContextMenu
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager)
	{
		ISelection selection = this._treeViewer.getSelection();

		if (selection instanceof TreeSelection)
		{
			TreeSelection treeSelection = (TreeSelection) selection;
			Object item = treeSelection.getFirstElement();

			if (item instanceof BaseNode)
			{
				BaseNode node = (BaseNode) item;
				Action[] actions = node.getActions();

				if (actions != null)
				{
					for (Action action : actions)
					{
						manager.add(action);
					}
				}
			}
		}
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$

		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				fillContextMenu(manager);
			}
		});

		// Create menu.
		Menu menu = menuMgr.createContextMenu(this._treeViewer.getControl());
		this._treeViewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, this._treeViewer);
	}

	/**
	 * listenForScriptChanges
	 */
	private void listenForScriptChanges()
	{
		this._loadCycleListener = new LoadCycleListener()
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
		};

		BundleManager.getInstance().addLoadCycleListener(this._loadCycleListener);
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		UIJob job = new UIJob("Refresh Bundles View") //$NON-NLS-1$
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
