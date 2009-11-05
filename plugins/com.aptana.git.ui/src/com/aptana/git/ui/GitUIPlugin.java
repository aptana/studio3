package com.aptana.git.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.GitProjectRefresher;

/**
 * The activator class controls the plug-in life cycle
 */
public class GitUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	private static final String PLUGIN_ID = "com.aptana.git.ui"; //$NON-NLS-1$

	// The shared instance
	private static GitUIPlugin plugin;

	private GitProjectRefresher fRepoListener;

	/**
	 * The constructor
	 */
	public GitUIPlugin()
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
		fRepoListener = new GitProjectRefresher();
		GitRepository.addListener(fRepoListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		GitRepository.removeListener(fRepoListener);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GitUIPlugin getDefault()
	{
		return plugin;
	}

	public static void logInfo(String string)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, getPluginId(), string));
	}

	public static void trace(String string)
	{
		getDefault().getLog().log(new Status(IStatus.OK, getPluginId(), string));
	}

	public static String getPluginId()
	{
		return PLUGIN_ID;
	}

	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginId(), msg, e));
	}

	public static void logWarning(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, getPluginId(), msg));
	}

	public static Image getImage(String string)
	{
		if (getDefault().getImageRegistry().get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(getPluginId(), string);
			if (id != null)
				getDefault().getImageRegistry().put(string, id);
		}
		return getDefault().getImageRegistry().get(string);
	}

}
