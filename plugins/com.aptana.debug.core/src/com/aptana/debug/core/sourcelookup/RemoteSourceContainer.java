/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
		}
		return new Object[0];
	}

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	public String getName() {
		return "Remote File Source Container"; //$NON-NLS-1$
	}

	/*
	 * Not persisted via the launch configuration
	 * 
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	public ISourceContainerType getType() {
		return null;
	}
}