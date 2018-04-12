/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.index;

public interface IKeyProvider
{
	/**
	 * The key used to store/retrieve AttributeElements in/from an index
	 * 
	 * @return
	 */
	String getAttributeKey();

	/**
	 * The key used to store/retrieve ElementElements in/from an index
	 * 
	 * @return
	 */
	String getElementKey();

	/**
	 * The location to use when storing the element and attribute information
	 * 
	 * @return
	 */
	String getMetadataLocation();
}
