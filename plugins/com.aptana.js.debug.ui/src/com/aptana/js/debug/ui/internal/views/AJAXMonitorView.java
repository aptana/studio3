/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.views;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;

import com.aptana.debug.core.util.DebugUtil;
import com.aptana.js.debug.core.model.xhr.IXHRService;
import com.aptana.js.debug.ui.internal.xhr.AJAXMonitorPage;

/**
 * @author Max Stepanov
 */
public class AJAXMonitorView extends AbstractDebugView implements IDebugEventSetListener {

	private ISelectionListener selectionListener;
	private IXHRService current;
	private boolean currentTerminated;

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#configureToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void configureToolBar(IToolBarManager tbm) {
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#createActions()
	 */
	protected void createActions() {
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected Viewer createViewer(Composite parent) {
		throw new IllegalArgumentException("should not be called"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillContextMenu(IMenuManager menu) {
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#getHelpContextId()
	 */
	protected String getHelpContextId() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	protected IPage createDefaultPage(PageBook book) {
		AJAXMonitorPage page = new AJAXMonitorPage();
		page.createControl(book);
		setViewer(page.getViewer());
		initPage(page);
		updateTitle();

		DebugPlugin.getDefault().addDebugEventListener(this);
		// listen to selection in debug view
		getSite().getPage().addPostSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, getSelectionListener());
		return page;
	}

	private void updateTitle() {
		int count = 0;
		if (current != null) {
			count = current.getTransfersCount();
		}
		setContentDescription(MessageFormat.format(Messages.AJAXMonitorView_items_0, Integer.valueOf(count)));
		// count will be formatted as 1.234
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		getSite().getPage().removePostSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, getSelectionListener());
		DebugPlugin.getDefault().removeDebugEventListener(this);
		super.dispose();
	}

	/**
	 * getCurrentXHRService
	 * 
	 * @return IXHRService
	 */
	protected IXHRService getCurrentXHRService() {
		IAdaptable adaptable = DebugUITools.getDebugContext();
		if (adaptable == null) {
			IViewPart debugViewPart = getSite().getPage().findView(IDebugUIConstants.ID_DEBUG_VIEW);
			if (debugViewPart != null) {
				Object debugInput = ((AbstractDebugView) debugViewPart).getViewer().getInput();
				if (debugInput instanceof ILaunchManager) {
					IDebugTarget[] debugTargets = ((ILaunchManager) debugInput).getDebugTargets();
					if (debugTargets.length > 0) {
						adaptable = debugTargets[0];
					} else {
						IProcess[] debugProcesses = ((ILaunchManager) debugInput).getProcesses();
						if (debugProcesses.length > 0) {
							adaptable = debugProcesses[0];
						}
					}
				}
			}

		}
		IDebugTarget target = (IDebugTarget) DebugUtil.findAdapter(adaptable, IDebugTarget.class);
		if (target != null) {
			return (IXHRService) target.getAdapter(IXHRService.class);
		}
		return null;
	}

	private void setViewerInput(IXHRService input) {
		if (current == null && input == null) {
			return;
		}
		if (current != null && current.equals(input)) {
			return;
		}

		current = input;
		getViewer().setInput(input);
		updateTitle();
		showViewer();
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#becomesHidden()
	 */
	protected void becomesHidden() {
		getViewer().setInput(null);
		showViewer();
		super.becomesHidden();
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#becomesVisible()
	 */
	protected void becomesVisible() {
		super.becomesVisible();
		if (current != null) {
			getViewer().setInput(current);
			showViewer();
		} else {
			setViewerInput(getCurrentXHRService());
		}
		if (getViewer().getSelection().isEmpty() && (current != null) && (current.getTransfersCount() > 0)) {
			getViewer().setSelection(new StructuredSelection(current.getTransfers()[0]));
		}
	}

	private ISelectionListener getSelectionListener() {
		if (selectionListener == null) {
			selectionListener = new ISelectionListener() {
				public void selectionChanged(IWorkbenchPart part, ISelection selection) {
					if (!isAvailable() || !isVisible()) {
						return;
					}
					setViewerInput(getCurrentXHRService());
				}
			};
		}
		return selectionListener;
	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();
			final Object data = event.getData();
			if (current == null || currentTerminated) {
				if ((event.getKind() == DebugEvent.CREATE)
						&& (source instanceof IDebugTarget || source instanceof IProcess)) {
					getViewer().getControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (current == null || currentTerminated) {
								currentTerminated = false;
								setViewerInput(getCurrentXHRService());
							}
						}
					});
				}
				continue;
			} else if ((event.getKind() == DebugEvent.TERMINATE)
					&& (source instanceof IDebugTarget || source instanceof IProcess)) {
				currentTerminated = true;
			} else if (source != current) { // $codepro.audit.disable useEquals
				continue;
			}
			switch (event.getKind()) {
				case DebugEvent.CHANGE:
					getViewer().getControl().getDisplay().syncExec(new Runnable() {
						public void run() {
							if (data != null) {
								((StructuredViewer) getViewer()).update(current, null);
							} else {
								updateTitle();
								((StructuredViewer) getViewer()).refresh(current);
							}
						}
					});
					break;
				default:
					break;
			}
		}
	}
}
