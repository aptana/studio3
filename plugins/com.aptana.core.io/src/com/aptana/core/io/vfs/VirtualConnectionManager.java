/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable questionableAssignment

package com.aptana.core.io.vfs;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.Assert;

import com.aptana.core.io.efs.VirtualFileSystem;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.ConnectionPoint;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 *
 */
public class VirtualConnectionManager {

	private static final String ROOT_STRING = "/"; //$NON-NLS-1$
	
	private static VirtualConnectionManager instance;
	
	private Map<ConnectionPoint, URI> connections = new WeakHashMap<ConnectionPoint, URI>();
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
				IdeLog.logError(CoreIOPlugin.getDefault(), e);
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
			IdeLog.logWarning(CoreIOPlugin.getDefault(), MessageFormat.format(
					Messages.VirtualConnectionManager_NoMatchingConnectionForURI, uri.toASCIIString()));
		}
		return connectionPoint;
	}
	
	public static URI getVirtualRootURI(URI uri) {
		if (!ROOT_STRING.equals(uri.getPath()) || uri.getFragment() != null) {
			try {
				uri = new URI(uri.getScheme(), uri.getAuthority(), ROOT_STRING, null, null);
			} catch (URISyntaxException e) {
				IdeLog.logError(CoreIOPlugin.getDefault(), e);
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
