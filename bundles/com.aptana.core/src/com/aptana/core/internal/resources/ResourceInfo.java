/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.resources;

/**
 * @author Max Stepanov
 *
 */
/*package*/ class ResourceInfo {

	/** The collection of markers for this resource. */
	private MarkerSet markers = null;

	
	/** 
	 * Returns a copy of the collection of makers on this resource.
	 * <code>null</code> is returned if there are none.
	 * @return MarkerSet
	 */
	public MarkerSet getMarkers() {
		return getMarkers(true);
	}

	/** 
	 * Returns the collection of makers on this resource.
	 * <code>null</code> is returned if there are none.
	 * @param makeCopy 
	 * @return MarkerSet
	 */
	public MarkerSet getMarkers(boolean makeCopy) {
		if (markers == null) {
			return null;
		}
		return makeCopy ? (MarkerSet) markers.clone() : markers;
	}

	/** 
	 * Sets the collection of makers for this resource.
	 * <code>null</code> is passed in if there are no markers.
	 * @param value 
	 */
	public void setMarkers(MarkerSet value) {
		markers = value;
	}

}
