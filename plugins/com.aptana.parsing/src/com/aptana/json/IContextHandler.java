/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * IContextHandler
 */
public interface IContextHandler
{
	/**
	 * addElement
	 * 
	 * @param elementType
	 */
	void addElement(String elementTypeName, IState elementType);

	/**
	 * createType
	 * 
	 * @param typeName
	 * @param type
	 * @param value
	 */
	void createType(String typeName, IState type, Object value);

	/**
	 * setProperty
	 * 
	 * @param propertyName
	 * @param propertyTypeName
	 * @param propertyType
	 */
	void setProperty(String propertyName, String propertyTypeName, IState propertyType);
}
