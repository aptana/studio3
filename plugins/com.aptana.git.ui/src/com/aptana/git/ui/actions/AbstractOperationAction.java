/*******************************************************************************
 * Copyright (C) 2008, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2007, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.actions.Messages;

/**
 * Common functionality for EGit operations.
 */
@SuppressWarnings("rawtypes")
abstract class AbstractOperationAction implements IObjectActionDelegate {
	/**
	 * The active workbench part
	 */
	protected IWorkbenchPart wp;

	private IWorkspaceRunnable op;

	public void selectionChanged(final IAction act, final ISelection sel) {
		// work performed in setActivePart
	}

	public void setActivePart(final IAction act, final IWorkbenchPart part) {
		wp = part;
		ISelection sel = part.getSite().getPage().getSelection();
		final List selection;
		if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
			selection = ((IStructuredSelection) sel).toList();
		} else {
			selection = Collections.EMPTY_LIST;
		}
		op = createOperation(act, selection);
		act.setEnabled(op != null && wp != null);
	}

	/**
	 * Instantiate an operation on an action on provided objects.
	 *
	 * @param act
	 * @param selection
	 * @return a {@link IWorkspaceRunnable} for invoking this operation later on
	 */
	protected abstract IWorkspaceRunnable createOperation(final IAction act,
			final List selection);

	/**
	 * A method to invoke when the operation is finished.
	 */
	protected void postOperation() {
		// Empty
	}

	public void run(final IAction act) {
		if (op != null) {
			try {
				try {
					wp.getSite().getWorkbenchWindow().run(true, false,
							new IRunnableWithProgress() {
								public void run(final IProgressMonitor monitor)
										throws InvocationTargetException {
									try {
										op.run(monitor);
									} catch (CoreException ce) {
										throw new InvocationTargetException(ce);
									}
								}
							});
				} finally {
					postOperation();
				}
			} catch (Throwable e) {
				final String msg = NLS.bind(Messages.AbstractOperationAction_GenericFailed_Message, act
						.getText());
				final IStatus status;

				if (e instanceof InvocationTargetException) {
					e = e.getCause();
				}

				if (e instanceof CoreException) {
					status = ((CoreException) e).getStatus();
					e = status.getException();
				} else {
					status = new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
							1, msg, e);
				}

				GitUIPlugin.logError(msg, e);
				ErrorDialog.openError(wp.getSite().getShell(), act.getText(),
						msg, status, status.getSeverity());
			}
		}
	}
}
