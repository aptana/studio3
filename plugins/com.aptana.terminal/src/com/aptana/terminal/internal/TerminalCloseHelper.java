/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.internal;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;

import com.aptana.terminal.connector.LocalTerminalConnector;

/**
 * @author Max Stepanov
 */
public final class TerminalCloseHelper {

	/**
	 * 
	 */
	private TerminalCloseHelper() {
	}

	public static boolean canCloseTerminal(IShellProvider shellProvider, LocalTerminalConnector terminalConnector) {
		List<String> processes = terminalConnector.getRunningProcesses();
		if (processes.size() < 2) {
			return true;
		}

		int closeId = 1;
		MessageDialog dialog = new MessageDialog(shellProvider.getShell(), Messages.TerminalCloseHelper_DialogTitle,
				null, Messages.TerminalCloseHelper_DialogMessage + processes.toString(), MessageDialog.QUESTION,
				new String[] { IDialogConstants.CANCEL_LABEL, IDialogConstants.CLOSE_LABEL }, closeId);
		return dialog.open() == closeId;
	}

}
