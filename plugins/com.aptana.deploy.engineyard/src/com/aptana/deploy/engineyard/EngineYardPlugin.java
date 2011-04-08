package com.aptana.deploy.engineyard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class EngineYardPlugin extends Plugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy.engineyard"; //$NON-NLS-1$

	private static EngineYardPlugin instance;

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

	public static EngineYardPlugin getDefault()
	{
		return instance;
	}

	public static void logError(Throwable t)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginIdentifier(), t.getMessage(), t));
	}

	public static String getPluginIdentifier()
	{
		return PLUGIN_ID;
	}

}
