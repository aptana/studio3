/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.debug.core.sourcelookup;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer;

import com.aptana.debug.core.DebugCorePlugin;

/**
 * @author Max Stepanov
 */
public class RemoteSourceContainer extends AbstractSourceContainer {

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#findSourceElements(java.lang.String)
	 */
	public Object[] findSourceElements(String path) throws CoreException {
		try {
			URI uri = new URI(path);
			String scheme = uri.getScheme();
			if ("http".equals(scheme) || "https".equals(scheme) || "dbgsource".equals(scheme)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				RemoteFileStorage storage = DebugCorePlugin.getDefault().getRemoteSourceCacheManager().getStorage(uri);
				if (storage == null) {
					storage = new RemoteFileStorage(uri, null);
					DebugCorePlugin.getDefault().getRemoteSourceCacheManager().add(uri, storage);
				}
				return new Object[] { storage };
			}
		} catch (URISyntaxException ignore) {
			ignore.getCause();
		}
		return EMPTY;
	}

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	public String getName() {
		return "Remote File Source Container"; //$NON-NLS-1$
	}

	/*
	 * Not persisted via the launch configuration
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	public ISourceContainerType getType() {
		return null;
	}
}