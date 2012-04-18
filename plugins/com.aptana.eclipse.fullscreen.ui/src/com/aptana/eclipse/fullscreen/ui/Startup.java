package com.aptana.eclipse.fullscreen.ui;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Startup implements Runnable, IStartup
{

	private IWorkbenchWindow[] windows;

	public void earlyStartup()
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		windows = workbench.getWorkbenchWindows();
		workbench.getDisplay().asyncExec(this);
	}

	public void run()
	{
		for (IWorkbenchWindow window : windows)
		{
			Activator.setWindowFullscreen(window.getShell());
		}
	}
}
