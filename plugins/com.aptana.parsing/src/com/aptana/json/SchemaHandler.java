/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * @author klindsey
 */
public class SchemaHandler implements IContextHandler
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#addElement(java.lang.String, com.aptana.json.IState)
	 */
	public void addElement(String elementTypeName, IState elementType)
	{
		System.out.println("add element of type '" + elementTypeName + "' to list");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#createList(java.lang.String, com.aptana.json.IState)
	 */
	public void createList(String elementTypeName, IState elementType)
	{
		System.out.println("create list of '" + elementTypeName + "'");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#createType(java.lang.String, com.aptana.json.IState)
	 */
	public void createType(String typeName, IState type)
	{
		System.out.println("create type '" + typeName + "'");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#setProperty(java.lang.String, com.aptana.json.IState)
	 */
	public void setProperty(String propertyName, IState propertyType)
	{
		System.out.println("set property '" + propertyName + "'");
	}
}
