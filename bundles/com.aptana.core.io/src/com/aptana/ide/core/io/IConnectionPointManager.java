/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.io.events.IConnectionPointListener;

/**
 * @author Max Stepanov
 */
public interface IConnectionPointManager extends IAdaptable
{

	public void addConnectionPoint(IConnectionPoint connectionPoint);

	public void removeConnectionPoint(IConnectionPoint connectionPoint);

	public void connectionPointChanged(IConnectionPoint connectionPoint);

	public IConnectionPoint cloneConnectionPoint(IConnectionPoint connectionPoint) throws CoreException;

	public ConnectionPointType[] getTypes();

	public ConnectionPointType getType(String typeId);

	public ConnectionPointType getType(IConnectionPoint connectionPoint);

	public IConnectionPoint createConnectionPoint(ConnectionPointType type) throws CoreException;

	public IConnectionPoint createConnectionPoint(String typeId) throws CoreException;

	public IConnectionPointCategory[] getConnectionPointCategories();

	public IConnectionPointCategory getConnectionPointCategory(String categoryId);

	public IConnectionPoint[] getConnectionPoints();

	public void addConnectionPointListener(IConnectionPointListener listener);

	public void removeConnectionPointListener(IConnectionPointListener listener);

	public void saveState(IPath path);

	public List<IConnectionPoint> addConnectionsFrom(IPath path);

	public List<IConnectionPoint> readConnectionsFrom(IPath path);
}
