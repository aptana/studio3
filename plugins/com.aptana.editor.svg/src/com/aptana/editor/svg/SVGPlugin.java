package com.aptana.editor.svg;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SVGPlugin extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "com.aptana.editor.svg"; //$NON-NLS-1$
	private static SVGPlugin plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SVGPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * getImage
	 * 
	 * @param path
	 * @return
	 */
	public static Image getImage(String path)
	{
		ImageRegistry registry = plugin.getImageRegistry();
		Image image = registry.get(path);

		if (image == null)
		{
			ImageDescriptor id = getImageDescriptor(path);

			if (id != null)
			{
				registry.put(path, id);
				image = registry.get(path);
			}
		}

		return image;
	}

	/**
	 * getImageDescriptor
	 * 
	 * @param path
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * logError
	 * 
	 * @param msg
	 * @param e
	 */
	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	/**
	 * logInfo
	 * 
	 * @param string
	 */
	public static void logInfo(String string)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, string));
	}

	/**
	 * logWarning
	 * 
	 * @param msg
	 */
	public static void logWarning(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, msg));
	}

	/**
	 * trace
	 * 
	 * @param string
	 */
	public static void trace(String string)
	{
		if (getDefault() != null && getDefault().isDebugging())
		{
			getDefault().getLog().log(new Status(IStatus.OK, PLUGIN_ID, string));
		}
	}

	/**
	 * The constructor
	 */
	public SVGPlugin()
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

		Job job = new MetadataLoader();
		job.schedule();
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
}
