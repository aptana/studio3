/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.HashMap;
import java.util.Map;

/**
 * SchemaObject
 */
public class SchemaObject extends SchemaValue
{
	private Map<String,SchemaProperty> _properties;
	
	/**
	 * addProperty
	 * 
	 * @param property
	 */
	public void addProperty(SchemaProperty property)
	{
		if (this._properties == null)
		{
			this._properties = new HashMap<String, SchemaProperty>();
		}
		
		this._properties.put(property.getName(), property);
	}
}
