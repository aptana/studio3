/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Max Stepanov
 */
public interface IConnectionPoint extends IAdaptable
{

	public String getName();

	public String getId();

	public String getType();

	public URI getRootURI();

	public IFileStore getRoot() throws CoreException;

	public void connect(IProgressMonitor monitor) throws CoreException;

	public void connect(boolean force, IProgressMonitor monitor) throws CoreException;

	public boolean isConnected();

	public void disconnect(IProgressMonitor monitor) throws CoreException;

	public boolean canDisconnect();

}
