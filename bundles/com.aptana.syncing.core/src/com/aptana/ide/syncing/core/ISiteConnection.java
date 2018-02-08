/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.core;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 */
public interface ISiteConnection extends IAdaptable
{

	public String getName();

	public IConnectionPoint getSource();

	public IConnectionPoint getDestination();

	public boolean excludes(IPath path);

	/**
	 * Sets the source connection point.
	 * 
	 * @param source
	 */
	public void setSource(IConnectionPoint source);

	/**
	 * Sets the destination connection point.
	 * 
	 * @param destination
	 */
	public void setDestination(IConnectionPoint destination);

	/**
	 * set site connection name
	 * 
	 * @param name
	 */
	public void setName(String name);

}
