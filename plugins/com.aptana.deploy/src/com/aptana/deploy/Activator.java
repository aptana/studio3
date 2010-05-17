package com.aptana.deploy;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{

	private static final String PLUGIN_ID = "com.aptana.deploy"; //$NON-NLS-1$

	private static Activator instance;

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
		super.stop(bundleContext);
	}

	public static String getPluginIdentifier()
	{
		return PLUGIN_ID;
	}

	public static Activator getDefault()
	{
		return instance;
	}

	public static void logError(String message, Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
	}

	public static void logError(Exception e)
	{
		if (e instanceof CoreException)
		{
			logError((CoreException) e);
		}
		else
		{
			logError(e.getMessage(), e);
		}
	}

	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	public static Image getImage(String string)
	{
		if (getDefault().getImageRegistry().get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (id != null)
			{
				getDefault().getImageRegistry().put(string, id);
			}
		}
		return getDefault().getImageRegistry().get(string);
	}

	public static ImageDescriptor getImageDescriptor(String path)
	{
		ImageDescriptor desc = getDefault().getImageRegistry().getDescriptor(path);
		if (desc != null)
		{
			return desc;
		}
		desc = imageDescriptorFromPlugin(PLUGIN_ID, path);
		getDefault().getImageRegistry().put(path, desc);
		return desc;
	}
}
