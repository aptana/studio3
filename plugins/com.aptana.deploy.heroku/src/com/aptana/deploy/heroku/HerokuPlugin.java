package com.aptana.deploy.heroku;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class HerokuPlugin extends Plugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy.heroku"; //$NON-NLS-1$

	private static HerokuPlugin instance;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		super.start(bundleContext);
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		instance = null;
		super.stop(bundleContext);
	}

	public static String getPluginIdentifier()
	{
		return PLUGIN_ID;
	}

	public static void logError(Throwable t)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginIdentifier(), t.getMessage(), t));
	}

	private static HerokuPlugin getDefault()
	{
		return instance;
	}

}
