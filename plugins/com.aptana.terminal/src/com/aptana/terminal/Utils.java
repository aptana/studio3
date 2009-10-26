package com.aptana.terminal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.aptana.terminal.editor.TerminalEditorInput;

public class Utils
{
	private static final String SAMPLE_TEXT;

	/*
	 * static
	 */
	static
	{
		char[] chars = new char[200];
		
		for (int i = 0; i < chars.length; i++)
		{
			chars[i] = 'M';
		}
		
		SAMPLE_TEXT = new String(chars);
	}
	
	/*
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
				e.printStackTrace();
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
				buffer.append("\\x").append(Integer.toString(c, 16));
			}
			else
			{
				buffer.append(c);
			}
		}
		
		return buffer.toString();
	}
	
	/**
	 * getCharacterWidth
	 * 
	 * @return
	 */
	public static Size getCharacterWidth()
	{
		Display display = Display.getCurrent();
		Image canvas = new Image(display, 100,100);
		GC gc = new GC(canvas);
		Font font = new Font(display, "Courier", SWT.NORMAL, 9);
		
		gc.setFont(font);
		
		Point size = gc.textExtent(SAMPLE_TEXT);
		Size result = new Size(
			(double) size.x / SAMPLE_TEXT.length(),
			(double) size.y
		);
		
	    gc.dispose();
	    canvas.dispose();
	    
	    return result;
	}
}
