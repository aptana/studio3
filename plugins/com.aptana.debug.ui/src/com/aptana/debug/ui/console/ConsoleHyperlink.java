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
			MessageDialog
					.openInformation(
							DebugUiPlugin.getActiveWorkbenchShell(),
							"Information", MessageFormat.format("Source not found for {0}", fFilename));
		} catch (CoreException e) {
			DebugUiPlugin.errorDialog("An exception occurred while following link.", e);
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
