package com.aptana.parsing;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ParsingPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.parsing"; //$NON-NLS-1$

	// The shared instance
	private static ParsingPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ParsingPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		try
		{
			ParserPoolFactory.getInstance().dispose();
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}		
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ParsingPlugin getDefault() {
		return plugin;
	}

	public static void logError(Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}
}
