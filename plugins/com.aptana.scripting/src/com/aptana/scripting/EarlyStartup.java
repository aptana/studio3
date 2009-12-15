package com.aptana.scripting;

import net.contentobjects.jnotify.JNotifyException;

import org.eclipse.ui.IStartup;

import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.BundleMonitor;

/**
 * EarlyStartup
 */
public class EarlyStartup implements IStartup
{
	/**
	 * EarlyStartup
	 */
	public EarlyStartup()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup()
	{
		// go ahead and process the workspace now to process bundles that exist already
		BundleManager.getInstance().loadBundles();
		
		// turn on project and file monitoring
		try
		{
			BundleMonitor.getInstance().beginMonitoring();
		}
		catch (JNotifyException e)
		{
			// log error
		}
	}
}
