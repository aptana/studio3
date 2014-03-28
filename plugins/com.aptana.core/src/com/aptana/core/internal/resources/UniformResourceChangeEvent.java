/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
class UniformResourceChangeEvent extends EventObject implements IUniformResourceChangeEvent {

	private static final IMarkerDelta[] NO_MARKER_DELTAS = new IMarkerDelta[0];
	private static final long serialVersionUID = 1L;
	
	private transient IUniformResource resource;
	private transient IMarkerDelta[] deltas;

	/**
	 * UniformResourceChangeEvent
	 * 
	 * @param source
	 * @param resource
	 * @param deltas
	 */
	UniformResourceChangeEvent(Object source, IUniformResource resource, IMarkerDelta[] deltas) {
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
		return matching.toArray(new IMarkerDelta[matching.size()]);
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
