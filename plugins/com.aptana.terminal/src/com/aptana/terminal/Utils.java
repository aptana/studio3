package com.aptana.terminal;

import java.text.MessageFormat;

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
	public static IEditorPart openEditor(String editorId, boolean activate)
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			
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
				
				Activator.logError(message, e);
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
				String hex = String.format("%1$02X", (int) c);
				
				buffer.append("\\x").append(hex); //$NON-NLS-1$
			}
			else
			{
				buffer.append(c);
			}
		}
		
		return buffer.toString();
	}
}
