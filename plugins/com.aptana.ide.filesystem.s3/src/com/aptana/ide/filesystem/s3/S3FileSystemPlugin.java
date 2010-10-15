package com.aptana.ide.filesystem.s3;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class S3FileSystemPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ide.filesystem.s3"; //$NON-NLS-1$

	// The shared instance
	private static S3FileSystemPlugin plugin;

	/**
	 * The constructor
	 */
	public S3FileSystemPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static S3FileSystemPlugin getDefault()
	{
		return plugin;
	}

	public static void log(Exception e)
	{
		getDefault().getLog().log(status(e));
	}

	public static CoreException coreException(Exception e)
	{
		return coreException(-1, e);
	}

	private static IStatus status(Exception e)
	{
		return status(-1, e);
	}

	private static IStatus status(int errorCode, Exception e)
	{
		return new Status(IStatus.ERROR, PLUGIN_ID, errorCode, e.getMessage(), e);
	}

	public static CoreException coreException(int errorCode, Exception e)
	{
		return new CoreException(status(errorCode, e));
	}
}
