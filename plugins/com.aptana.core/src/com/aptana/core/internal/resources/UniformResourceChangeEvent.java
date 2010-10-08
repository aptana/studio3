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
package com.aptana.core.internal.resources;

import java.util.ArrayList;
import java.util.EventObject;

import org.eclipse.core.resources.IMarkerDelta;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceChangeEvent;

/**
 * @author Max Stepanov
 *
 */
public class UniformResourceChangeEvent extends EventObject implements IUniformResourceChangeEvent {

	private static final IMarkerDelta[] NO_MARKER_DELTAS = new IMarkerDelta[0];
	private static final long serialVersionUID = 1L;
	
	private IUniformResource resource;
	private IMarkerDelta[] deltas;

	/**
	 * UniformResourceChangeEvent
	 * 
	 * @param source
	 * @param resource
	 * @param deltas
	 */
	public UniformResourceChangeEvent(Object source, IUniformResource resource, IMarkerDelta[] deltas) {
		super(source);
		this.resource = resource;
		this.deltas = deltas;
	}

	/**
	 * @see com.aptana.ide.core.resources.IUniformResourceChangeEvent#findMarkerDeltas(java.lang.String, boolean)
	 */
	public IMarkerDelta[] findMarkerDeltas(String type, boolean includeSubtypes) {
		ArrayList<IMarkerDelta> matching = new ArrayList<IMarkerDelta>();
		IMarkerDelta[] deltas = getMarkerDeltas();
		for( int i = 0; i < deltas.length; ++i ) {
			IMarkerDelta markerDelta = deltas[i];
			if (type == null || (includeSubtypes ? markerDelta.isSubtypeOf(type) : markerDelta.getType().equals(type)))
			{
				matching.add(markerDelta);
			}
		}
		if ( matching.size() == 0 )
		{
			return NO_MARKER_DELTAS;
		}
		return (IMarkerDelta[]) matching.toArray(new IMarkerDelta[matching.size()]);
	}

	/**
	 * @see com.aptana.ide.core.resources.IUniformResourceChangeEvent#getResource()
	 */
	public IUniformResource getResource() {
		return resource;
	}

	/**
	 * @see com.aptana.ide.core.resources.IUniformResourceChangeEvent#getMarkerDeltas()
	 */
	public IMarkerDelta[] getMarkerDeltas() {
		if ( deltas == null )
		{
			return NO_MARKER_DELTAS;
		}
		return deltas;
	}

}
