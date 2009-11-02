package com.aptana.scripting;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;

/**
 * EarlyStartup
 */
public class EarlyStartup implements IStartup
{
	private static final IResourceChangeListener listener = new ResourceChangeListener();
	
	/**
	 * EarlyStartup
	 */
	public EarlyStartup()
	{
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup()
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}
}
