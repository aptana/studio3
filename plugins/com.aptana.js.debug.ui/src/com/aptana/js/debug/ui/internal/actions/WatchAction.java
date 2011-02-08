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
package com.aptana.js.debug.ui.internal.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IExpressionManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
public class WatchAction implements IWorkbenchWindowActionDelegate, IEditorActionDelegate, IViewActionDelegate {

	private ISelection fSelection;

	/*
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}

	/*
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
	}

	/*
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (fSelection == null) {
			return;
		}
		if (fSelection instanceof IStructuredSelection) {
			for (Object object : ((IStructuredSelection) fSelection).toList()) {
				IVariable variable = (IVariable) object;
				try {
					createExpression(variable.getName());
				} catch (DebugException e) {
					DebugUiPlugin.errorDialog(Messages.WatchAction_CreateWatchExpressionFailed, e);
				}
			}
		} else if (fSelection instanceof ITextSelection) {
			String expressionText = ((ITextSelection) fSelection).getText();
			if (expressionText.length() > 0) {
				createExpression(expressionText);
			}
		}
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("rawtypes")
	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = null;
		if (!action.isEnabled()) {
			return;
		}
		int enabled = 0;
		int size = -1;
		if (selection instanceof IStructuredSelection) {
			fSelection = selection;
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			size = sSelection.size();
			IExpressionManager manager = DebugPlugin.getDefault().getExpressionManager();
			for (Iterator iterator = sSelection.iterator(); iterator.hasNext();) {
				IVariable variable = (IVariable) iterator.next();
				if (manager.hasWatchExpressionDelegate(variable.getModelIdentifier())) {
					enabled++;
				} else {
					break;
				}
			}
			if (enabled != size) {
				action.setEnabled(false);
			}
		} else if (selection instanceof ITextSelection) {
			fSelection = selection;
			ITextSelection tSelection = (ITextSelection) selection;
			if (tSelection.getLength() == 0) {
				action.setEnabled(false);
			}
		}
	}

	/*
	 * showExpressionsView
	 */
	protected void showExpressionsView() {
		IWorkbenchPage page = DebugUiPlugin.getActiveWorkbenchWindow().getActivePage();
		IViewPart part = page.findView(IDebugUIConstants.ID_EXPRESSION_VIEW);
		if (part == null) {
			try {
				page.showView(IDebugUIConstants.ID_EXPRESSION_VIEW);
			} catch (PartInitException e) {
			}
		} else {
			page.bringToTop(part);
		}

	}

	/*
	 * createExpression
	 * 
	 * @param expressionText
	 */
	protected void createExpression(String expressionText) {
		IWatchExpression expression;
		expression = DebugPlugin.getDefault().getExpressionManager().newWatchExpression(expressionText);
		DebugPlugin.getDefault().getExpressionManager().addExpression(expression);
		IAdaptable object = DebugUITools.getDebugContext();
		IDebugElement context = null;
		if (object instanceof IDebugElement) {
			context = (IDebugElement) object;
		} else if (object instanceof ILaunch) {
			context = ((ILaunch) object).getDebugTarget();
		}
		expression.setExpressionContext(context);
		showExpressionsView();
	}
}
