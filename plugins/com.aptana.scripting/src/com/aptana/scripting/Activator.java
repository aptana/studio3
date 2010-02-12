package com.aptana.scripting;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.aptana.scripting.keybindings.internal.KeybindingsManager;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.RunType;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin
{
	public static final String PLUGIN_ID = "com.aptana.scripting"; //$NON-NLS-1$
	private static Activator plugin;

	/**
	 * Context id set by workbench part to indicate they are scripting aware.
	 */
	public static final String CONTEXT_ID = "com.aptana.scripting.context"; //$NON-NLS-1$

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}

	/**
	 * This returns the default run type to be used by ScriptingEngine and CommandElement.
	 * 
	 * @return
	 */
	public static RunType getDefaultRunType()
	{
		return RunType.CURRENT_THREAD;
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
		getDefault().getLog().log(new Status(IStatus.OK, PLUGIN_ID, string));
	}

	private FileTypeAssociationListener fileTypeListener;

	/**
	 * The constructor
	 */
	public Activator()
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
		fileTypeListener = new FileTypeAssociationListener();
		BundleManager.getInstance().addBundleChangeListener(fileTypeListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			KeybindingsManager.uninstall();
			if (fileTypeListener != null)
			{
				fileTypeListener.cleanup();
				BundleManager.getInstance().removeBundleChangeListener(fileTypeListener);
			}
			fileTypeListener = null;
		}
		catch (Exception e)
		{
			// ignore
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}
}
