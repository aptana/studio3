/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.js.debug.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class LaunchDebuggerPromptStatusHandler implements IStatusHandler {
	/**
	 * @see org.eclipse.debug.core.IStatusHandler#handleStatus(org.eclipse.core.runtime.IStatus, java.lang.Object)
	 */
	public Object handleStatus(IStatus status, Object source) throws CoreException {
		Shell shell = UIUtils.getActiveShell();
		String title = Messages.LaunchDebuggerPromptStatusHandler_Title;
		String message = Messages.LaunchDebuggerPromptStatusHandler_DebuggerSessionIsActive;

		MessageDialog dlg = new MessageDialog(shell, title, null, message, MessageDialog.INFORMATION, new String[] {
				Messages.LaunchDebuggerPromptStatusHandler_CloseActiveSession, IDialogConstants.CANCEL_LABEL }, 1);
		return Boolean.valueOf(dlg.open() == Window.OK);
	}
}
