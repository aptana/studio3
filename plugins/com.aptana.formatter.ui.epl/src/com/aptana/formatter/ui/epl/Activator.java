package com.aptana.formatter.ui.epl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.formatter.ui.epl"; //$NON-NLS-1$
	public static final int INTERNAL_ERROR = 10001;
	public static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption(PLUGIN_ID + "/debug")).booleanValue(); //$NON-NLS-1$
	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static Activator getDefault()
	{
		return plugin;
	}

	public static void log(IStatus status)
	{
		Activator.getDefault().getLog().log(status);
	}

	public static void logError(String message)
	{
		logError(message, null);
	}

	public static void warn(String message, Throwable throwable)
	{
		log(new Status(IStatus.WARNING, PLUGIN_ID, INTERNAL_ERROR, message, throwable));
	}

	public static void logError(String message, Throwable throwable)
	{
		Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, INTERNAL_ERROR, message, throwable));
	}

	public static void logError(Throwable t)
	{
		logError(t.getMessage(), t);
	}
}
