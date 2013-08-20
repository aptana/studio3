/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.views;

import java.util.Collections;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import com.aptana.core.logging.IdeLog;
import com.aptana.samples.ISampleListener;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.model.SamplesReference;
import com.aptana.samples.ui.SamplesUIPlugin;
import com.aptana.samples.ui.handlers.ImportSampleHandler;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author Kevin Lindsey
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia
 */
public class SamplesView extends ViewPart
{

	// the view id
	public static final String ID = "com.aptana.samples.ui.SamplesView"; //$NON-NLS-1$

	private TreeViewer treeViewer;

	private ISampleListener sampleListener = new ISampleListener()
	{

		public void sampleAdded(IProjectSample sample)
		{
			refresh();
		}

		public void sampleRemoved(IProjectSample sample)
		{
			refresh();
		}

		private void refresh()
		{
			UIUtils.getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{
					if (treeViewer != null && !treeViewer.getControl().isDisposed())
					{
						treeViewer.refresh();
					}
				}
			});
		}
	};

	@Override
	public void createPartControl(Composite parent)
	{
		treeViewer = createTreeViewer(parent);

		getSite().setSelectionProvider(treeViewer);
		hookContextMenu();
		applyTheme();

		getSamplesManager().addSampleListener(sampleListener);
	}

	@Override
	public void setFocus()
	{
	}

	@Override
	public void dispose()
	{
		getSamplesManager().removeSampleListener(sampleListener);
		super.dispose();
		ThemePlugin.getDefault().getControlThemerFactory().dispose(treeViewer);
	}

	public void collapseAll()
	{
		treeViewer.collapseAll();
	}

	protected TreeViewer createTreeViewer(Composite parent)
	{
		final TreeViewer treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(new SamplesViewContentProvider());
		treeViewer.setLabelProvider(new SamplesViewLabelProvider());
		treeViewer.setInput(getSamplesManager());
		treeViewer.setComparator(new ViewerComparator());
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
				Object element = thisSelection.getFirstElement();
				treeViewer.setExpandedState(element, !treeViewer.getExpandedState(element));
				if (element instanceof SamplesReference)
				{
					// Run the import command.
					ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(
							ICommandService.class);
					Command command = commandService.getCommand(ImportSampleHandler.COMMAND_ID);
					if (command != null)
					{
						try
						{
							IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
									IHandlerService.class);
							ExecutionEvent ee = new ExecutionEvent(command, Collections.emptyMap(), null,
									handlerService.getCurrentState());

							command.executeWithChecks(ee);
						}
						catch (Exception e)
						{
							IdeLog.logError(SamplesUIPlugin.getDefault(), e);
						}
					}
				}
			}
		});
		ColumnViewerToolTipSupport.enableFor(treeViewer);

		return treeViewer;
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{

			public void menuAboutToShow(IMenuManager manager)
			{
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});

		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	private void applyTheme()
	{
		ThemePlugin.getDefault().getControlThemerFactory().apply(treeViewer);
	}

	private static ISamplesManager getSamplesManager()
	{
		return SamplesPlugin.getDefault().getSamplesManager();
	}
}
