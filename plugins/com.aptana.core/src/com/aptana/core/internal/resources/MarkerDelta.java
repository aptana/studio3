/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.resources;

import org.eclipse.core.internal.resources.IMarkerSetElement;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;

import com.aptana.core.resources.IUniformResource;

/**
 *
 */
@SuppressWarnings("restriction")
public class MarkerDelta extends org.eclipse.core.internal.resources.MarkerDelta {

	/**
	 * uniform resource
	 */
	protected IUniformResource resource;
	
	public MarkerDelta(int kind, IUniformResource resource, MarkerInfo info)
	{
		super(kind, ResourcesPlugin.getWorkspace().getRoot(), info);
		this.resource = resource;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.MarkerDelta#isSubtypeOf(java.lang.String)
	 */
	public boolean isSubtypeOf(String superType)
	{
		return MarkerManager.getInstance().isSubtype(getType(), superType);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.MarkerDelta#getMarker()
	 */
	public IMarker getMarker()
	{
		return new UniformResourceMarker(resource, getId());
	}

	/**
	 * getUniformResource
	 *
	 * @return IUniformResource
	 */
	public IUniformResource getUniformResource() {
		return resource;
	}

	protected static MarkerSet merge(MarkerSet oldChanges, IMarkerSetElement[] newChanges) {
		MarkerSet merged = new MarkerSet();
		merged.addAll(org.eclipse.core.internal.resources.MarkerDelta.merge(oldChanges, newChanges).elements());
		return merged;
	}
}
