package com.aptana.ui.epl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class UIEplPlugin extends AbstractUIPlugin
{

	public static final int INTERNAL_ERROR = 10001;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ui.epl"; //$NON-NLS-1$

	public static final boolean DEBUG = Boolean
			.valueOf(Platform.getDebugOption("com.aptana.ui.epl/debug")).booleanValue(); //$NON-NLS-1$

	// The shared instance
	private static UIEplPlugin plugin;

	/**
	 * The constructor
	 */
	public UIEplPlugin()
	{
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UIEplPlugin getDefault()
	{
		return plugin;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static Shell getActiveWorkbenchShell()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null)
		{
			return window.getShell();
		}
		return null;
	}

	public static void log(IStatus status)
	{
		UIEplPlugin.getDefault().getLog().log(status);
	}

	public static void logError(String message)
	{
		logError(message, null);
	}

	public static void warn(String message)
	{
		warn(message, null);
	}

	public static void warn(String message, Throwable throwable)
	{
		log(new Status(IStatus.WARNING, PLUGIN_ID, INTERNAL_ERROR, message, throwable));
	}

	public static void logError(String message, Throwable throwable)
	{
		UIEplPlugin.log(new Status(IStatus.ERROR, UIEplPlugin.PLUGIN_ID, INTERNAL_ERROR, message, throwable));
	}

	public static void logErrorStatus(String message, IStatus status)
	{
		if (status == null)
		{
			UIEplPlugin.logError(message);
			return;
		}
		MultiStatus multi = new MultiStatus(UIEplPlugin.PLUGIN_ID, INTERNAL_ERROR, message, null);
		multi.add(status);
		UIEplPlugin.log(multi);
	}

	public static void logError(Throwable t)
	{
		logError(t.getMessage(), t);
	}
}
