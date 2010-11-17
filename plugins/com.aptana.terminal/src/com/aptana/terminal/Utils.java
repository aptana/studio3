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
package com.aptana.terminal;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.aptana.terminal.editor.TerminalEditorInput;

public class Utils
{
	/**
	 * Utils
	 */
	private Utils()
	{
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
	public static IEditorPart openTerminalEditor(String editorId, boolean activate)
	{
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
	public static IEditorPart openTerminalEditor(IWorkbenchWindow window, String editorId, boolean activate)
	{
		
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if (page == null)
			{
				return null;
			}
			try
			{
				// TODO: changed MATCH pattern from MATCH_ID to MATCH_INPUT, so we'll probably need our own version
				// of this method
				return page.openEditor(new TerminalEditorInput(), editorId, activate, IWorkbenchPage.MATCH_INPUT);
			}
			catch (PartInitException e)
			{
				String message = MessageFormat.format(
					Messages.Utils_Unable_To_Open_Editor,
					new Object[] { editorId }
				);
				
				Activator.log(message, e);
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
	public static String encodeString(String text)
	{
		StringBuffer buffer = new StringBuffer();
		
		for (char c : text.toCharArray())
		{
			if (0 <= c && c < 32 || 128 <= c)
			{
				String hex = String.format("%1$02X", (int) c); //$NON-NLS-1$
				
				buffer.append("\\x").append(hex); //$NON-NLS-1$
			}
			else
			{
				buffer.append(c);
			}
		}
		
		return buffer.toString();
	}
	
	/**
	 * @param runnable run in display thread
	 */
	public static void runInDisplayThread(Runnable runnable) {
		if (Display.findDisplay(Thread.currentThread()) != null) {
			runnable.run();
		} else if( PlatformUI.isWorkbenchRunning() ) {
			PlatformUI.getWorkbench().getDisplay().syncExec(runnable);
		}
	}

}
