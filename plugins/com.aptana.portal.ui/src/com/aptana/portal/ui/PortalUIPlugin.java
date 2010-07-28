package com.aptana.portal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PortalUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.portal.ui"; //$NON-NLS-1$

	// The browser Portal ID
	public static final String PORTAL_ID = "com.aptana.portal.main"; //$NON-NLS-1$

	/**
	 * Ruby image key
	 */
	public static final String RUBY = "/icons/wizban/ruby.png"; //$NON-NLS-1$
	
	// The shared instance
	private static PortalUIPlugin plugin;

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

	public static BundleContext getContext()
	{
		return getDefault().getBundle().getBundleContext();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PortalUIPlugin getDefault()
	{
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		reg.put(RUBY, imageDescriptorFromPlugin(PLUGIN_ID, RUBY));
	}
	
	public static void logInfo(String string, Throwable t)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, string, t));
	}

	public static void logError(Throwable t)
	{
		logError(t.getLocalizedMessage(), t);
	}

	public static void logError(String string, Throwable t)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, string, t));
	}

	public static void logWarning(String message)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message));
	}
}
