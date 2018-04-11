/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.vfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Max Stepanov
 *
 */
public interface IConnectionFileManager {

	public void connect(IProgressMonitor monitor) throws CoreException;
	public boolean isConnected();
	public void disconnect(IProgressMonitor monitor) throws CoreException;

	public URI getCanonicalURI(IPath path);

	public IExtendedFileInfo fetchInfo(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public String[] childNames(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public IExtendedFileInfo[] childInfos(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	
	public InputStream openInputStream(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public OutputStream openOutputStream(IPath path, int options, IProgressMonitor monitor) throws CoreException;

	public void delete(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	public void mkdir(IPath path, int options, IProgressMonitor monitor) throws CoreException;
	
	public void putInfo(IPath path, IFileInfo info, int options, IProgressMonitor monitor) throws CoreException;
	
	public void move(IPath sourcePath, IPath destinationPath, int options, IProgressMonitor monitor) throws CoreException;

}
