/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.views;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * IndexView
 */
public class IndexView extends ViewPart
{
	private TreeViewer treeViewer;
	private ITreeContentProvider contentProvider;
	private ILabelProvider labelProvider;
	private IActionProvider actionProvider;

	/**
	 * addListeners
	 */
	private void addListeners()
	{
		// this.listenForScriptChanges();
		ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();

		// @formatter:off
		selectionService.addPostSelectionListener(
			IPageLayout.ID_PROJECT_EXPLORER,
			new ISelectionListener() {
				public void selectionChanged(IWorkbenchPart part, ISelection selection)
				{
					if (part != IndexView.this && selection instanceof IStructuredSelection)
					{
						setInputFromSelection(selection);
					}
				}
			}
		);
		// @formatter:on
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		contentProvider = new IndexViewContentProvider();
		labelProvider = new IndexViewLabelProvider();
		actionProvider = new IndexViewActionProvider();

		// set content and label providers
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setComparator(new ViewerComparator()
		{
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

				return name1.compareToIgnoreCase(name2);
			}
		});

		// set input based on the current selection
		ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection(IPageLayout.ID_PROJECT_EXPLORER);
		setInputFromSelection(selection);

		// add selection provider
		this.getSite().setSelectionProvider(treeViewer);

		// listen to theme changes
		hookContextMenu();
		addListeners();
	}

	/**
	 * fillContextMenu
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager)
	{
		ISelection selection = treeViewer.getSelection();

		if (selection instanceof TreeSelection)
		{
			TreeSelection treeSelection = (TreeSelection) selection;
			Object item = treeSelection.getFirstElement();

			if (item != null)
			{
				IAction[] actions = actionProvider.getActions(this, item);

				if (actions != null)
				{
					for (IAction action : actions)
					{
						manager.add(action);
					}
				}
			}
		}
	}

	/**
	 * getTreeViewer
	 * 
	 * @return
	 */
	public TreeViewer getTreeViewer()
	{
		return treeViewer;
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
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	/**
	 * setInputFromSelection
	 * 
	 * @param selection
	 */
	private void setInputFromSelection(ISelection selection)
	{
		IStructuredSelection ss = (IStructuredSelection) selection;

		if (ss != null && treeViewer != null && !treeViewer.getTree().isDisposed())
		{
			Iterator<?> items = ss.iterator();

			while (items.hasNext())
			{
				Object item = items.next();

				if (item instanceof IResource)
				{
					IProject project = ((IResource) item).getProject();

					if (project.isOpen())
					{
						treeViewer.setInput(project);
					}
					else
					{
						treeViewer.setInput(null);
					}

					break;
				}
			}
		}
	}
}
