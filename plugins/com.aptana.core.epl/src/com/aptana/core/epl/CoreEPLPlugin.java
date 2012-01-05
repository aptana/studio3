/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor

package com.aptana.core.epl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
public class CoreEPLPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.core.epl"; //$NON-NLS-1$

	// The shared instance
	private static CoreEPLPlugin plugin;
	private ServiceTracker retrievalFactoryTracker;
	private static BundleContext context;

	/**
	 * The constructor
	 */
	public CoreEPLPlugin()
	{
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		super.start(bundleContext);
		context = bundleContext;
		plugin = this;
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		plugin = null;
		context = null;
		super.stop(bundleContext);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CoreEPLPlugin getDefault()
	{
		return plugin;
	}

	public static BundleContext getContext()
	{
		return context;
	}

	/**
	 * Returns a {@link IRetrieveFileTransferFactory} using a {@link ServiceTracker} after having attempted to start the
	 * bundle "org.eclipse.ecf.provider.filetransfer". If something is wrong with the configuration this method returns
	 * null.
	 * 
	 * @return a factory, or null, if configuration is incorrect
	 */
	public IRetrieveFileTransferFactory getRetrieveFileTransferFactory()
	{
		return (IRetrieveFileTransferFactory) getFileTransferServiceTracker().getService();
	}

	/**
	 * Returns the service described by the given arguments. Note that this is a helper class that <b>immediately</b>
	 * ungets the service reference. This results in a window where the system thinks the service is not in use but
	 * indeed the caller is about to use the returned service object.
	 * 
	 * @param context
	 * @param name
	 * @return The requested service
	 */
	public static Object getService(BundleContext context, String name)
	{
		if (context == null)
		{
			return null;
		}
		// don't add <?> as it's for Eclipse 3.7's getServiceReference() only
		ServiceReference reference = context.getServiceReference(name);
		if (reference == null)
		{
			return null;
		}
		Object result = context.getService(reference);
		context.ungetService(reference);
		return result;
	}

	/**
	 * Log a message and an optional exception.
	 * 
	 * @param msg
	 * @param e
	 */
	public static void log(String msg, Throwable e)
	{
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, e));
	}

	/**
	 * Log a status.
	 * 
	 * @param status
	 */
	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	/**
	 * Gets the singleton ServiceTracker for the IRetrieveFileTransferFactory and starts the bundles "org.eclipse.ecf"
	 * and "org.eclipse.ecf.provider.filetransfer" on first call.
	 * 
	 * @return ServiceTracker
	 */
	private synchronized ServiceTracker getFileTransferServiceTracker()
	{
		if (retrievalFactoryTracker == null)
		{
			retrievalFactoryTracker = new ServiceTracker(getContext(), IRetrieveFileTransferFactory.class.getName(),
					null);
			retrievalFactoryTracker.open();
			startBundle("org.eclipse.ecf"); //$NON-NLS-1$
			startBundle("org.eclipse.ecf.provider.filetransfer"); //$NON-NLS-1$
		}
		return retrievalFactoryTracker;
	}

	// Starts a bundle
	private boolean startBundle(String bundleId)
	{
		PackageAdmin packageAdmin = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
		if (packageAdmin == null)
			return false;

		Bundle[] bundles = packageAdmin.getBundles(bundleId, null);
		if (bundles != null && bundles.length > 0)
		{
			for (int i = 0; i < bundles.length; i++)
			{
				try
				{
					if ((bundles[0].getState() & Bundle.INSTALLED) == 0)
					{
						bundles[0].start();
						return true;
					}
				}
				catch (BundleException e)
				{
					// failed, try next bundle
				}
			}
		}
		return false;
	}
}
