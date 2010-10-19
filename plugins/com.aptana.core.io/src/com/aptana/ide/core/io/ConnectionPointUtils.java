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

package com.aptana.ide.core.io;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.io.efs.EFSUtils;

/**
 * @author Max Stepanov
 *
 */
public final class ConnectionPointUtils {

	/**
	 * 
	 */
	private ConnectionPointUtils() {
	}
	
	public static IConnectionPoint findConnectionPoint(URI uri) {
		for (IConnectionPoint i : CoreIOPlugin.getConnectionPointManager().getConnectionPoints()) {
			if (uri.equals(i.getRootURI())) {
				return i;
			}
		}
		return null;
	}
	
	public static IConnectionPoint[] getRemoteConnectionPoints() {
		List<IConnectionPoint> list = new ArrayList<IConnectionPoint>();
		for (IConnectionPoint i : CoreIOPlugin.getConnectionPointManager().getConnectionPoints()) {
			if (isRemote(i)) {
				list.add(i);
			}
		}
		return list.toArray(new IConnectionPoint[list.size()]);
	}
	
	public static boolean isLocal(IConnectionPoint connectionPoint) {
		return connectionPoint instanceof LocalConnectionPoint;
	}

	public static boolean isWorkspace(IConnectionPoint connectionPoint) {
		return connectionPoint instanceof WorkspaceConnectionPoint;
	}

	public static boolean isRemote(IConnectionPoint connectionPoint) {
		return connectionPoint instanceof IBaseRemoteConnectionPoint;
	}

	private static IConnectionPoint createLocalConnectionPoint(IPath path) {
		LocalConnectionPoint connectionPoint = new LocalConnectionPoint(path);
		connectionPoint.setName(path.toPortableString());
		return connectionPoint;
	}
	
	private static IConnectionPoint createWorkspaceConnectionPoint(IContainer container) {
		WorkspaceConnectionPoint connectionPoint = new WorkspaceConnectionPoint(container);
		connectionPoint.setName((container instanceof IProject) ? container.getName() : container.getFullPath().toPortableString());
		return connectionPoint;
	}
	
	public static IConnectionPoint findOrCreateLocalConnectionPoint(IPath path) {
		IConnectionPoint connectionPoint = findConnectionPoint(EFSUtils.getLocalFileStore(path.toFile()).toURI());
		if (connectionPoint == null) {
			connectionPoint = ConnectionPointUtils.createLocalConnectionPoint(path);
		}
		return connectionPoint;
	}
	
	public static IConnectionPoint findOrCreateWorkspaceConnectionPoint(IContainer container) {
		IConnectionPoint connectionPoint = findConnectionPoint(EFSUtils.getFileStore(container).toURI());
		if (connectionPoint == null) {
			connectionPoint = ConnectionPointUtils.createWorkspaceConnectionPoint(container);
		}
		return connectionPoint;
	}
}
