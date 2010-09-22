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
package com.aptana.core.resources;

import org.eclipse.core.resources.IMarkerDelta;

/**
 * @author Max Stepanov
 *
 */
public interface IUniformResourceChangeEvent {

	/**
	 * Returns the changes to markers on the corresponding resource.
	 * Returns an empty array if no markers changed.
	 *
	 * @return the marker deltas
	 */
	IMarkerDelta[] getMarkerDeltas();

	/**
	 * Returns all marker deltas of the specified type that are associated
	 * with resource deltas for this event. If <code>includeSubtypes</code>
	 * is <code>false</code>, only marker deltas whose type exactly matches 
	 * the given type are returned.  Returns an empty array if there 
	 * are no matching marker deltas.
	 * <p>
	 * Calling this method is equivalent to walking the entire resource
	 * delta for this event, and collecting all marker deltas of a given type.
	 * The speed of this method will be proportional to the number of changed
	 * markers, regardless of the size of the resource delta tree.
	 * </p>
	 * @param type the type of marker to consider, or <code>null</code> to indicate all types
	 * @param includeSubtypes whether or not to consider sub-types of the given type
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
