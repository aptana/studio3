/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.js.debug.ui.internal.views;

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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.js.debug.core.model.IJSDebugTarget;
import com.aptana.js.debug.ui.internal.JSDebugModelPresentation;
import com.aptana.js.debug.ui.internal.actions.OpenScriptSourceAction;
import com.aptana.js.debug.ui.internal.scripts.ScriptsContentProvider;
import com.aptana.js.debug.ui.internal.scripts.ScriptsViewer;
import com.aptana.debug.core.util.DebugUtil;

/**
 * @author Max Stepanov
 */
public class ScriptsView extends AbstractDebugView implements IDebugEventSetListener {
	private static final String GOTO_FILE_ACTION = "GotoFile";
	private ISelectionListener selectionListener;
	private IDebugTarget currentTarget;
	private boolean currentTerminated;

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected Viewer createViewer(Composite parent) {
		ScriptsViewer treeViewer = new ScriptsViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL
				| SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new ScriptsContentProvider());
		treeViewer.setLabelProvider(new JSDebugModelPresentation());

		DebugPlugin.getDefault().addDebugEventListener(this);
		// listen to selection in debug view
		getSite().getPage().addPostSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, getSelectionListener());
		return treeViewer;
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
	 * @see org.eclipse.debug.ui.AbstractDebugView#createActions()
	 */
	protected void createActions() {
		IAction action = new OpenScriptSourceAction(getViewer());
		setAction(GOTO_FILE_ACTION, action);
		setAction(DOUBLE_CLICK_ACTION, action);
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#getHelpContextId()
	 */
	protected String getHelpContextId() {
		return null;
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillContextMenu(IMenuManager menu) {
		menu.add(getAction(GOTO_FILE_ACTION));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#configureToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void configureToolBar(IToolBarManager tbm) {
		tbm.add(getAction(GOTO_FILE_ACTION));
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#becomesHidden()
	 */
	protected void becomesHidden() {
		setViewerInput(null);
		super.becomesHidden();
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractDebugView#becomesVisible()
	 */
	protected void becomesVisible() {
		super.becomesVisible();
		setViewerInput(getCurrentDebugTarget());
	}

	private ISelectionListener getSelectionListener() {
		if (selectionListener == null) {
			selectionListener = new ISelectionListener() {
				public void selectionChanged(IWorkbenchPart part, ISelection selection) {
					if (!isAvailable() || !isVisible()) {
						return;
					}
					setViewerInput(getCurrentDebugTarget());
				}
			};
		}
		return selectionListener;
	}

	private void setViewerInput(IDebugTarget target) {
		if (!(target instanceof IJSDebugTarget) || ((IJSDebugTarget) target).isTerminated()) {
			target = null;
		}
		if (currentTarget == null && target == null) {
			return;
		}
		if (currentTarget != null && currentTarget.equals(target)) {
			return;
		}

		currentTarget = target;
		getViewer().setInput(target);
		showViewer();
	}

	/**
	 * getCurrentDebugTarget
	 * 
	 * @return AJAXMonitorView_items_0
	 */
	protected IDebugTarget getCurrentDebugTarget() {
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
		return (IDebugTarget) DebugUtil.findAdapter(adaptable, IDebugTarget.class);
	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();
			if (currentTarget == null || currentTerminated) {
				if ((event.getKind() == DebugEvent.CREATE) && source instanceof IDebugTarget) {
					getViewer().getControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (currentTarget == null || currentTerminated) {
								currentTerminated = false;
								setViewerInput(getCurrentDebugTarget());
							}
						}
					});
				}
			} else if ((event.getKind() == DebugEvent.TERMINATE) && source instanceof IDebugTarget) {
				currentTerminated = true;
			}

			if (currentTarget == null || source != currentTarget) {
				continue;
			}
			switch (event.getKind()) {
			case DebugEvent.CHANGE:
				if (event.getDetail() == DebugEvent.CONTENT) {
					getViewer().getControl().getDisplay().syncExec(new Runnable() {
						public void run() {
							getViewer().refresh();
						}
					});
				}
				break;
			default:
			}
		}
	}
}
