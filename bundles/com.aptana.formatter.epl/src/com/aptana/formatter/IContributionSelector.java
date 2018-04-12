/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter;

import org.eclipse.core.resources.IProject;


/**
 * Provides an interface to select between multiple implementations of a contributed extension point.
 */
public interface IContributionSelector
{

	/**
	 * Select a contribution implementation
	 * <p>
	 * To select a project specific resource, pass an instance of the desired project, otherwise, specific
	 * <code>null</code>.
	 * 
	 * @param contributions
	 *            list of contribution implementations
	 * @param project
	 *            project reference or <code>null</code>
	 * @return contribution
	 */
	IContributedExtension select(IContributedExtension[] contributions, IProject project);
}
