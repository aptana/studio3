/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaBuilder
 */
public class SchemaBuilder extends SchemaReader
{
	/**
	 * SchemaBuilder
	 */
	public SchemaBuilder()
	{
		super();

		this.setSchema(this.createSchema());
	}

	/**
	 * createSchema
	 * 
	 * @return
	 */
	private Schema createSchema()
	{
		Schema result = new Schema();
		
		// create SchemaDocument
		SchemaObject document = (SchemaObject) result.addType("SchemaDocument");
		document.addProperty("schema", "Schema");
		document.addProperty("types", "Array<Type>");
		
		// create Schema
		SchemaObject schema = (SchemaObject) result.addType("Schema");
		schema.addProperty("name", "String");
		schema.addProperty("version", "String");
		schema.addProperty("description", "String");
		schema.addProperty("result", "String");
		
		// create Type
		SchemaObject type = (SchemaObject) result.addType("Type");
		type.addProperty("name", "String");
		type.addProperty("description", "String");
		type.addProperty("properties", "Array<Property>");
		
		// create Property
		SchemaObject property = (SchemaObject) result.addType("Property");
		property.addProperty("name", "String");
		property.addProperty("type", "String");
		property.addProperty("description", "String");
		property.addProperty("example", "String");
		property.addProperty("optional", "Boolean");
		
		// set root type
		result.setRootType("SchemaDocument");

		return result;
	}
}
