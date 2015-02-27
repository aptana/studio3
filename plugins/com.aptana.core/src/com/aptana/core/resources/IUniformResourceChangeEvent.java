/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import org.eclipse.core.resources.IMarkerDelta;

/**
 * @author Max Stepanov
 */
public interface IUniformResourceChangeEvent
{

	/**
	 * Returns the changes to markers on the corresponding resource. Returns an empty array if no markers changed.
	 * 
	 * @return the marker deltas
	 */
	IMarkerDelta[] getMarkerDeltas();

	/**
	 * Returns all marker deltas of the specified type that are associated with resource deltas for this event. If
	 * <code>includeSubtypes</code> is <code>false</code>, only marker deltas whose type exactly matches the given type
	 * are returned. Returns an empty array if there are no matching marker deltas.
	 * <p>
	 * Calling this method is equivalent to walking the entire resource delta for this event, and collecting all marker
	 * deltas of a given type. The speed of this method will be proportional to the number of changed markers,
	 * regardless of the size of the resource delta tree.
	 * </p>
	 * 
	 * @param type
	 *            the type of marker to consider, or <code>null</code> to indicate all types
	 * @param includeSubtypes
	 *            whether or not to consider sub-types of the given type
	 * @return an array of marker deltas
	 */
	IMarkerDelta[] findMarkerDeltas(String type, boolean includeSubtypes);

	/**
	 * getResource
	 * 
	 * @return IUniformResource
	 */
	IUniformResource getResource();

	/**
	 * Returns an object identifying the source of this event.
	 * 
	 * @return an object identifying the source of this event
	 * @see java.util.EventObject
	 */
	Object getSource();

}
