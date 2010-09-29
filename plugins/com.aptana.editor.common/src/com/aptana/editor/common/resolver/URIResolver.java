/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.CommonEditorPlugin;

/**
 * Resolves paths that may be relative to a base URI (filesystem, remote, etc)
 * 
 * @author cwilliams
 */
public class URIResolver implements IPathResolver
{

	private URI baseURI;

	public URIResolver(URI baseURI)
	{
		this.baseURI = baseURI;
	}

	/**
	 * Returns null if unable to resolve the path to a URI and grab the contents.
	 */
	public String resolveSource(String path, IProgressMonitor monitor) throws Exception
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		URI uri = resolveURI(path);
		if (uri == null)
			return null;
		sub.worked(5);
		// get the filesystem that can handle the URI
		IFileStore store = getFileStore(uri);

		int options = EFS.CACHE;
		// If file is local no need to cache
		if (store.getFileSystem().equals(EFS.getLocalFileSystem()))
		{
			options = EFS.NONE;
		}
		// grab down a local copy
		File aFile = store.toLocalFile(options, sub.newChild(90));
		if (aFile == null || !aFile.exists())
		{
			// Need to pass up correct original filename and says that's the one that doesn't exist
			throw new FileNotFoundException(uri.toString());
		}
		// now read in the local copy
		return IOUtil.read(new FileInputStream(aFile));
	}

	protected IFileStore getFileStore(URI uri) throws CoreException
	{
		IFileSystem fileSystem = EFS.getFileSystem(uri.getScheme());
		if (fileSystem == null)
			return EFS.getNullFileSystem().getStore(uri);
		return fileSystem.getStore(uri);
	}

	public URI resolveURI(String path)
	{
		if (path == null)
			return null;
		URI uri;
		try
		{
			// try to parse as a URI
			uri = URI.create(path);
			String scheme = uri.getScheme();
			if (scheme == null)
			{
				// no scheme, means it's relative to base URI, or an absolute file path?
				uri = baseURI.resolve(path);
			}
		}
		catch (IllegalArgumentException e)
		{
			// fails to parse, try resolving against base URI
			try {
				uri = baseURI.resolve(path);
			} catch (IllegalArgumentException e2) {
				// TODO What if it fails here, then what do we do?
				return null;
			}
		}

		try
		{
			IFileStore store = getFileStore(uri);
			IFileInfo info = store.fetchInfo();
			if (info.exists())
			{
				return uri;
			}
		}
		catch (CoreException e)
		{
			CommonEditorPlugin.logError(e);
		}

		return null;
	}
}
