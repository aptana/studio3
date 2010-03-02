package com.aptana.util;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


public class ClipboardUtil
{
	private ClipboardUtil()
	{
	}
	
	/**
	 * Determine if the clipboard has text content
	 * 
	 * @return
	 */
	public static boolean hasTextContent()
	{
		Display display = PlatformUI.getWorkbench().getDisplay();
		Clipboard clipboard = new Clipboard(display);
		TextTransfer transfer = TextTransfer.getInstance();
		TransferData[] available = clipboard.getAvailableTypes();
		boolean result = false;
		
		for (int i = 0; i < available.length; i++)
		{
			if (transfer.isSupportedType(available[i]))
			{
				result = true;
				break;
			}
		}
		
		return result;
	}
}
