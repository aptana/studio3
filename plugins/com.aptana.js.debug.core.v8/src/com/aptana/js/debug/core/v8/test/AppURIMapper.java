/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.js.debug.core.v8.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.IURIMapper;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.js.debug.core.v8.V8DebugPlugin;

/**
 * @author Max Stepanov
 */
public class AppURIMapper implements IURIMapper {

	private static final String APP_SCHEME = "app"; //$NON-NLS-1$
	private final List<IFileStore> containers = new ArrayList<IFileStore>();

	/**
	 * 
	 */
	public AppURIMapper(IContainer container, IPath... paths) {
		for (IPath path : paths) {
			IResource resource = container.findMember(path);
			if (resource instanceof IContainer && resource.exists()) {
				containers.add(EFSUtils.getFileStore(resource));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.core.IURIMapper#resolve(org.eclipse.core.filesystem.IFileStore
	 * )
	 */
	public URI resolve(IFileStore file) {
		for (IFileStore container : containers) {
			IPath relativePath = EFSUtils.getRelativePath(container, file);
			if (relativePath != null) {
				try {
					return new URI(APP_SCHEME, relativePath.makeAbsolute().toPortableString(), null);
				} catch (URISyntaxException e) {
					IdeLog.logError(V8DebugPlugin.getDefault(), e);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.core.IURIMapper#resolve(java.net.URL)
	 */
	public IFileStore resolve(URI uri) {
		IPath relativePath = Path.fromPortableString(uri.getPath());
		for (IFileStore container : containers) {
			IFileStore fileStore = container.getFileStore(relativePath);
			if (fileStore.fetchInfo().exists()) {
				return fileStore;
			}
		}
		return null;
	}
}
