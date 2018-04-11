/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDeployProvider
{

	/**
	 * Attempt to deploy the provided project or folder.
	 * 
	 * @param container
	 * @param monitor
	 */
	public void deploy(IContainer container, IProgressMonitor monitor);

	/**
	 * Is this a project or folder that can be handled by this provider? This method is used to implicitly bind a
	 * container to a provider, when we haven't explicitly deployed via a provider yet. In real terms, this means
	 * looking to see if this container was set up to deploy to this provider outside the deploy wizard (and maybe
	 * outside the IDE).
	 * 
	 * @param selectedContainer
	 * @return
	 */
	public boolean handles(IContainer selectedContainer);

	/**
	 * Returns the text of the menu item for deploying. If null is returned, the name associated with the deploy command
	 * will be used.
	 * 
	 * @return the text of the menu item, or null if the default name for the deploy command should be used
	 */
	public String getDeployMenuName();
}
