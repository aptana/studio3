/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.aptana.terminal.editor.TerminalEditorInput;

public class Utils {
	/**
	 * Utils
	 */
	private Utils() {
	}

	/**
	 * Opens a specific editor.
	 * 
	 * @param editorId
	 *            the editor ID
	 * @param activate
	 *            true if the editor should be activated, false otherwise
	 * @return
	 */
	public static IEditorPart openTerminalEditor(String editorId, boolean activate) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		return openTerminalEditor(window, editorId, activate);
	}

	/**
	 * openEditor
	 * 
	 * @param window
	 * @param editorId
	 * @param activate
	 * @return
	 */
	public static IEditorPart openTerminalEditor(IWorkbenchWindow window, String editorId, boolean activate) {

		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page == null) {
				return null;
			}
			try {
				// TODO: changed MATCH pattern from MATCH_ID to MATCH_INPUT, so we'll probably need our own version
				// of this method
				return page.openEditor(new TerminalEditorInput(), editorId, activate, IWorkbenchPage.MATCH_INPUT);
			} catch (PartInitException e) {
				String message = MessageFormat.format(Messages.Utils_Unable_To_Open_Editor, new Object[] { editorId });

				TerminalPlugin.log(message, e);
			}
		}

		return null;
	}

	/**
	 * encodeString
	 * 
	 * @param text
	 * @return
	 */
	public static String encodeString(String text) {
		StringBuffer buffer = new StringBuffer();

		for (char c : text.toCharArray()) {
			if (0 <= c && c < 32 || 128 <= c) {
				String hex = String.format("%1$02X", (int) c); //$NON-NLS-1$

				buffer.append("\\x").append(hex); //$NON-NLS-1$
			} else {
				buffer.append(c);
			}
		}

		return buffer.toString();
	}

	/**
	 * @param runnable
	 *            run in display thread
	 */
	public static void runInDisplayThread(Runnable runnable) {
		if (Display.findDisplay(Thread.currentThread()) != null) {
			runnable.run();
		} else if (PlatformUI.isWorkbenchRunning()) {
			PlatformUI.getWorkbench().getDisplay().syncExec(runnable);
		}
	}

}
