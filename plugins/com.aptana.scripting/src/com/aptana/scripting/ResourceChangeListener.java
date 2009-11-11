package com.aptana.scripting;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * ResourceChangeListener
 */
public class ResourceChangeListener implements IResourceChangeListener
{
	private static final IResourceDeltaVisitor deltaVistor = new ResourceDeltaVisitor();
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event)
	{
		try
		{
			event.getDelta().accept(deltaVistor);
		}
		catch (CoreException e)
		{
			// log an error in the error log
		}
	}
}
