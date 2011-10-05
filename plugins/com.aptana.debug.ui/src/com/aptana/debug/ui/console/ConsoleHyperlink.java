/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.ui.console;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.TextConsole;

import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.debug.ui.SourceDisplayUtil;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class ConsoleHyperlink implements IHyperlink {
	private TextConsole fConsole;
	private String fFilename;
	private int fLineNumber;

	/**
	 * ConsoleHyperlink
	 * 
	 * @param console
	 * @param fileName
	 * @param lineNumber
	 */
	public ConsoleHyperlink(TextConsole console, String fileName, int lineNumber) {
		fConsole = console;
		fFilename = fileName;
		fLineNumber = lineNumber;
	}

	/*
	 * @see org.eclipse.ui.console.IHyperlink#linkEntered()
	 */
	public void linkEntered() {
	}

	/*
	 * @see org.eclipse.ui.console.IHyperlink#linkExited()
	 */
	public void linkExited() {
	}

	/*
	 * @see org.eclipse.ui.console.IHyperlink#linkActivated()
	 */
	public void linkActivated() {
		try {
			Object sourceElement = DebugUITools.lookupSource(fFilename, getSourceLocator()).getSourceElement();
			IEditorInput editorInput = SourceDisplayUtil.getEditorInput(sourceElement);
			if (editorInput != null) {
				SourceDisplayUtil.openInEditor(editorInput, fLineNumber);
				return;
			}
			MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.ConsoleHyperlink_SourceNotFound_Title,
					MessageFormat.format(Messages.ConsoleHyperlink_SourceNotFound_Message, fFilename));
		} catch (CoreException e) {
			DebugUiPlugin.errorDialog("An exception occurred while following link.", e); //$NON-NLS-1$
		}
	}

	private ISourceLocator getSourceLocator() {
		ISourceLocator sourceLocator = null;
		IProcess process = (IProcess) getConsole().getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
		if (process != null) {
			ILaunch launch = process.getLaunch();
			if (launch != null) {
				sourceLocator = launch.getSourceLocator();
			}
		}
		return sourceLocator;
	}

	private TextConsole getConsole() {
		return fConsole;
	}
}
