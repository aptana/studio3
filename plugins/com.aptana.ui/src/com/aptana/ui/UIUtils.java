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

package com.aptana.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Max Stepanov
 *
 */
public final class UIUtils {

	/**
	 * 
	 */
	private UIUtils() {
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
		if (shell == null) {
		    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		    if (window != null) {
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
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbench == null)
		{
			return null;
		}
		IWorkbenchPage workbenchPage = workbench.getActivePage();
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
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbench == null)
		{
			return null;
		}
		IWorkbenchPage workbenchPage = workbench.getActivePage();
		if (workbenchPage == null)
		{
			return null;
		}
		return workbenchPage.getActivePart();
	}

	public static void showErrorMessage(String title, String message) {
		showErrorMessage(title != null ? title : Messages.UIUtils_Error, message, null);
	}

	public static void showErrorMessage(String message, Throwable exception) {
		showErrorMessage(Messages.UIUtils_Error, message, exception);
	}

	private static void showErrorMessage(final String title, final String message, final Throwable exception) {
		if (Display.getCurrent() == null || exception != null) {
			UIJob job = new UIJob(title) {
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					if (exception == null) {
						showErrorDialog(title, message);
						return Status.OK_STATUS;
					}
					return new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, message, exception);
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.setUser(true);
			job.schedule();
		} else {
			showErrorDialog(title, message);
		}
	}
	
	private static void showErrorDialog(String title, String message) {
		MessageDialog.openError(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				title, message);
	}
}
