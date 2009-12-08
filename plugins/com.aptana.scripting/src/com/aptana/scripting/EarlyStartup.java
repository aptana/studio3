package com.aptana.scripting;

import org.eclipse.ui.IStartup;

import com.aptana.scripting.model.BundleManager;

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
	}
}
