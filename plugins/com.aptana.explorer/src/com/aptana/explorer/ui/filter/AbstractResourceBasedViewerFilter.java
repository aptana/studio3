package com.aptana.explorer.ui.filter;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ViewerFilter;

public abstract class AbstractResourceBasedViewerFilter extends ViewerFilter
{

	/**
	 * The selected resource that this filter should be based upon.
	 * 
	 * @param resource
	 */
	public abstract void setResourceToFilterOn(IResource resource);

	/**
	 * Pattern used to describe the current filtering in the UI label.
	 * 
	 * @return
	 */
	public abstract String getPattern();

}
