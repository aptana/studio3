/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.terminal.Utils;

/**
 * Shows the terminal editor.
 * 
 * @author schitale
 */
public class ShowTerminalEditorAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void init(IWorkbenchWindow workbenchWindow) {
	}

	public void run(IAction action) {
		Utils.openTerminalEditor(TerminalEditor.ID, true);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
