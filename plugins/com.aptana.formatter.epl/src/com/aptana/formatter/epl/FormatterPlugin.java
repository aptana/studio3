package com.aptana.formatter.epl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.formatter.ui.IProfileManager;
import com.aptana.formatter.ui.profile.ProfileManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class FormatterPlugin extends AbstractUIPlugin
{

	public static final int INTERNAL_ERROR = 10001;

	public static final boolean DEBUG = Boolean
			.valueOf(Platform.getDebugOption("com.aptana.formatter.epl/debug")).booleanValue(); //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.formatter.epl"; //$NON-NLS-1$

	// The shared instance
	private static FormatterPlugin plugin;
	
	/**
	 * The constructor
	 */
	public FormatterPlugin()
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
	public static FormatterPlugin getDefault()
	{
		return plugin;
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
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
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, message, throwable));
	}

	public static void logErrorStatus(String message, IStatus status)
	{
		if (status == null)
		{
			logError(message);
			return;
		}
		MultiStatus multi = new MultiStatus(PLUGIN_ID, INTERNAL_ERROR, message, null);
		multi.add(status);
		log(multi);
	}

	public static void logError(Throwable t)
	{
		logError(t.getMessage(), t);
	}
}
