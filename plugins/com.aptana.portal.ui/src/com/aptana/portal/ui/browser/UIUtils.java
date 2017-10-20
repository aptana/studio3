package com.aptana.portal.ui.browser;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class UIUtils
{

	/**
	 * Gets the active shell for the workbench
	 * 
	 * @return the active shell
	 */
	public static Shell getActiveShell()
	{
		Shell shell = getDisplay().getActiveShell();
		if (shell == null)
		{
			IWorkbenchWindow window = getActiveWorkbenchWindow();
			if (window != null)
			{
				shell = window.getShell();
			}
		}
		return shell;
	}
	
	/**
	 * Gets the display for the workbench
	 * 
	 * @return the display
	 */
	public static Display getDisplay()
	{
		return PlatformUI.getWorkbench().getDisplay();
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		try
		{
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		}
		catch (IllegalStateException e)
		{
			// Workbench has not been created yet
			return null;
		}
	}
	
	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow workbench = getActiveWorkbenchWindow();
		if (workbench == null)
		{
			return null;
		}
		return workbench.getActivePage();
	}

}
