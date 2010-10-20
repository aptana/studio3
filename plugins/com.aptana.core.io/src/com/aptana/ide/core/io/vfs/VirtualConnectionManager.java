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

package com.aptana.ide.core.io.vfs;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.core.io.ConnectionPoint;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.VirtualFileSystem;

/**
 * @author Max Stepanov
 *
 */
public class VirtualConnectionManager {

	private static final String ROOT_STRING = "/"; //$NON-NLS-1$
	
	private static VirtualConnectionManager instance;
	
	private WeakHashMap<ConnectionPoint, URI> connections = new WeakHashMap<ConnectionPoint, URI>();
	private Map<URI, WeakReference<ConnectionPoint>> uris = new HashMap<URI, WeakReference<ConnectionPoint>>();
	
	/**
	 * 
	 */
	private VirtualConnectionManager() {
	}
	
	public static VirtualConnectionManager getInstance() {
		if (instance == null) {
			instance = new VirtualConnectionManager();
		}
		return instance;
	}
	
	public void register(ConnectionPoint connectionPoint) {
		Assert.isNotNull(connectionPoint);
		if (connections.containsKey(connectionPoint)) {
			URI uri = getConnectionVirtualURI(connectionPoint);
			if (uri != null) {
				uris.remove(uri);
			}
			connections.put(connectionPoint, null);
		}
		// assign connection URI 
		getConnectionVirtualURI(connectionPoint);
	}
			
	public URI getConnectionVirtualURI(ConnectionPoint connectionPoint) {
		URI uri = connections.get(connectionPoint);
		if (uri == null) {
			try {
				String id = connectionPoint.getId();
				if (id == null || id.length() == 0) {
					id = UUID.randomUUID().toString();
				}
				uri = new URI(VirtualFileSystem.SCHEME_VIRTUAL, id, ROOT_STRING, null, null);
			} catch (URISyntaxException e) {
			}
			// we do not update connection URI once it has been assigned even if its ID changed
			connections.put(connectionPoint, uri);
			if (uri != null) {
				uris.put(uri, new WeakReference<ConnectionPoint>(connectionPoint));
			}
		}
		return uri;
	}
	
	private IConnectionPoint getConnectionPoint(URI uri) {
		uri = getVirtualRootURI(uri);
		IConnectionPoint connectionPoint = null;
		WeakReference<ConnectionPoint> weakReference = uris.get(uri);
		if (weakReference != null) {
			connectionPoint = weakReference.get();
			if (connectionPoint == null && !connections.containsValue(uri)) {
				uris.remove(uri);
			}
		}
		if (connectionPoint == null) {
			CoreIOPlugin.log(new Status(IStatus.WARNING, CoreIOPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.VirtualConnectionManager_NoMatchingConnectionForURI, uri.toASCIIString())));
		}
		return connectionPoint;
	}
	
	public static URI getVirtualRootURI(URI uri) {
		if (!ROOT_STRING.equals(uri.getPath()) || uri.getFragment() != null) {
			try {
				uri = new URI(uri.getScheme(), uri.getAuthority(), ROOT_STRING, null, null);
			} catch (URISyntaxException e) {
			}
		}
		return uri;
	}
	
	public IConnectionFileManager getVirtualFileManager(URI uri) {
		IConnectionPoint connectionPoint = getConnectionPoint(uri);
		if (connectionPoint != null) {
			return (IConnectionFileManager) connectionPoint.getAdapter(IConnectionFileManager.class);
		}
		return null;
	}
}
