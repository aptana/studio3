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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import com.aptana.ide.core.io.events.IConnectionPointListener;

/**
 * @author Max Stepanov
 *
 */
public interface IConnectionPointManager extends IAdaptable {

	public void addConnectionPoint(IConnectionPoint connectionPoint);
	public void removeConnectionPoint(IConnectionPoint connectionPoint);
	public void connectionPointChanged(IConnectionPoint connectionPoint);

	public IConnectionPoint cloneConnectionPoint(IConnectionPoint connectionPoint) throws CoreException;
	
	public ConnectionPointType[] getTypes();
	public ConnectionPointType getType(String typeId);
	public ConnectionPointType getType(IConnectionPoint connectionPoint);
	
	public IConnectionPoint createConnectionPoint(ConnectionPointType type) throws CoreException;
	public IConnectionPoint createConnectionPoint(String typeId) throws CoreException;
	
	public IConnectionPointCategory[] getConnectionPointCategories();
	public IConnectionPointCategory getConnectionPointCategory(String categoryId);
	
	public IConnectionPoint[] getConnectionPoints();
	
	public void addConnectionPointListener(IConnectionPointListener listener);
	public void removeConnectionPointListener(IConnectionPointListener listener);
	
	public void loadState(IPath path);
	public void saveState(IPath path);

    /**
     * For migrating connection point from Studio 1.5.
     * 
     * @param type
     *            the type of the connection point (in 1.5)
     * @param data
     *            the data that represents the connection point (in 1.5)
     * @return
     */
    public IConnectionPoint restore15ConnectionPoint(String type, String data) throws CoreException;
}
