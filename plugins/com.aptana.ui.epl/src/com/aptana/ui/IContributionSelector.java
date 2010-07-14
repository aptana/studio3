package com.aptana.ui;

import org.eclipse.core.resources.IProject;

/**
 * Provides an interface to select between multiple implementations of a
 * contributed extension point.
 */
public interface IContributionSelector {

	/**
	 * Select a contribution implementation
	 * 
	 * <p>
	 * To select a project specific resource, pass an instance of the desired
	 * project, otherwise, specific <code>null</code>.
	 * 
	 * @param contributions
	 *            list of contribution implementations
	 * 
	 * @param project
	 *            project reference or <code>null</code>
	 * 
	 * @return contribution
	 */
	IContributedExtension select(IContributedExtension[] contributions,
			IProject project);
}
