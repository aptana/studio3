/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.util;

import java.net.URI;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.services.IEvaluationService;

import com.aptana.ui.UIPlugin;

/**
 * @author Max Stepanov
 */
public final class UIUtils
{

	/**
	 * 
	 */
	private UIUtils()
	{
	}

	/**
	 * Gets the display for the workbench
	 * 
	 * @return the display
	 */
	public static Display getDisplay()
	{
		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * Gets the active shell for the workbench
	 * 
	 * @return the active shell
	 */
	public static Shell getActiveShell()
	{
		Shell shell = getDisplay().getActiveShell();
		if (shell == null)
		{
			IWorkbenchWindow window = getActiveWorkbenchWindow();
			if (window != null)
			{
				shell = window.getShell();
			}
		}
		return shell;
	}

	/**
	 * Returns the editor part representing the current active editor.
	 * 
	 * @return the active editor
	 */
	public static IEditorPart getActiveEditor()
	{
		IWorkbenchPage workbenchPage = getActivePage();
		if (workbenchPage == null)
		{
			return null;
		}
		return workbenchPage.getActiveEditor();
	}

	/**
	 * Returns the active part in the current workbench window.
	 * 
	 * @return the active part
	 */
	public static IWorkbenchPart getActivePart()
	{
		IWorkbenchPage workbenchPage = getActivePage();
		if (workbenchPage == null)
		{
			return null;
		}
		return workbenchPage.getActivePart();
	}

	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow workbench = getActiveWorkbenchWindow();
		if (workbench == null)
		{
			return null;
		}
		return workbench.getActivePage();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Returns the URI for the specific editor input.
	 * 
	 * @param input
	 *            the editor input
	 * @return the URI, or null if none could be determined
	 */
	public static URI getURI(IEditorInput input)
	{
		if (input instanceof IFileEditorInput)
		{
			return ((IFileEditorInput) input).getFile().getLocationURI();
		}
		if (input instanceof IURIEditorInput)
		{
			return ((IURIEditorInput) input).getURI();
		}
		if (input instanceof IPathEditorInput)
		{
			return URIUtil.toURI(((IPathEditorInput) input).getPath());
		}
		return null;
	}

	public static IProject getSelectedProject()
	{
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench().getService(
				IEvaluationService.class);
		if (evaluationService != null)
		{
			IEvaluationContext currentState = evaluationService.getCurrentState();
			Object variable = currentState.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (variable instanceof IStructuredSelection)
			{
				Object selectedObject = ((IStructuredSelection) variable).getFirstElement();
				if (selectedObject instanceof IResource)
				{
					return ((IResource) selectedObject).getProject();
				}
			}
		}
		return null;
	}

	/**
	 * Finds a view with the given ID
	 * 
	 * @param viewID
	 *            the view ID
	 * @return the view part
	 * @throws PartInitException
	 */
	public static IViewPart findView(String viewID) throws PartInitException
	{
		IWorkbenchPage page = getActivePage();
		if (page != null)
		{
			return page.findView(viewID);
		}
		return null;
	}

	public static void showErrorMessage(String title, String message)
	{
		showErrorMessage(title != null ? title : Messages.UIUtils_Error, message, null);
	}

	public static void showErrorMessage(String message, Throwable exception)
	{
		showErrorMessage(Messages.UIUtils_Error, message, exception);
	}

	private static void showErrorMessage(final String title, final String message, final Throwable exception)
	{
		if (Display.getCurrent() == null || exception != null)
		{
			UIJob job = new UIJob(title)
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (exception == null)
					{
						showErrorDialog(title, message);
						return Status.OK_STATUS;
					}
					return new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, message, exception);
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.setUser(true);
			job.schedule();
		}
		else
		{
			showErrorDialog(title, message);
		}
	}

	private static void showErrorDialog(String title, String message)
	{
		MessageDialog.openError(getActiveWorkbenchWindow().getShell(), title, message);
	}

	public static boolean showPromptDialog(final String title, final String message)
	{
		if (Display.getCurrent() == null)
		{
			UIJob job = new UIJob(title)
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (showPromptDialogUI(title, message))
					{
						return Status.OK_STATUS;
					}
					return Status.CANCEL_STATUS;
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.setUser(true);
			job.schedule();
			try
			{
				job.join();
			}
			catch (InterruptedException e)
			{
			}
			return job.getResult() == Status.OK_STATUS;
		}
		else
		{
			return showPromptDialogUI(title, message);
		}
	}

	private static boolean showPromptDialogUI(String title, String message)
	{
		return MessageDialog.openQuestion(getActiveWorkbenchWindow().getShell(), title, message);
	}
}
