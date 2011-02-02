/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.resources;

import org.eclipse.core.internal.resources.IMarkerSetElement;

/**
 *
 */
@SuppressWarnings("restriction")
/*package*/ class MarkerSet extends org.eclipse.core.internal.resources.MarkerSet {
	/**
	 * 
	 */
	public MarkerSet()
	{
		super();
	}

	/**
	 * @param capacity
	 */
	public MarkerSet(int capacity)
	{
		super(capacity);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() {
		return super.clone();
	}

	/**
	 * copyInto
	 *
	 * @param array
	 */
	public void copyInto(Object[] array) {
		int j = 0;
		for (int i = 0; i < elements.length; i++) {
			IMarkerSetElement element = elements[i];
			if (element != null)
			{
				array[j++] = element;
			}
		}
	}

}
