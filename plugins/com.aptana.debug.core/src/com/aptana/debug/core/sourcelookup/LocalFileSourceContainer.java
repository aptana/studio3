/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.sourcelookup;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer;

import com.aptana.core.io.efs.EFSUtils;

/**
 * @author Max Stepanov
 */
public class LocalFileSourceContainer extends AbstractSourceContainer {

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#findSourceElements(java.lang.String)
	 */
	public Object[] findSourceElements(String uriString) throws CoreException {
		URI uri = null;
		try {
			uri = new URI(uriString);
			if (uri.getScheme() == null) {
				uri = null;
			}
		} catch (URISyntaxException ignore) {
			ignore.getCause();
		}
		if (uri == null) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromPortableString(uriString));
			if (resource != null) {
				uri = EFSUtils.getFileStore(resource).toURI();
			}
		}
		if (uri != null) {
			String scheme = uri.getScheme();
			if ("http".equals(scheme) || "https".equals(scheme) || ("ti".equals(scheme))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return EMPTY;
			}
			IFileStore fileStore = EFS.getStore(uri);
			if (fileStore.fetchInfo().exists()) {
				IResource resource = (IResource) fileStore.getAdapter(IResource.class);
				if (resource != null && resource.exists()) {
					return new Object[] { resource };
				}
				File file = (File) fileStore.getAdapter(File.class);
				if (file != null && file.isFile()) {
					return new Object[] { fileStore }; // TODO: check if we could use iFileStore here instead
				}
				file = fileStore.toLocalFile(EFS.CACHE, new NullProgressMonitor());
				if (file != null && file.isFile()) {
					return new Object[] { fileStore }; // TODO: check if we could use iFileStore here instead
				}
			}
		}
		return EMPTY;
	}

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	public String getName() {
		return Messages.JSSourceContainer_LocalFileSourceContainer;
	}

	/*
	 * Not persisted via the launch configuration
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	public ISourceContainerType getType() {
		return null;
	}
}