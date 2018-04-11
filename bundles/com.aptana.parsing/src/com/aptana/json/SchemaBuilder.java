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
@SuppressWarnings("nls")
public class SchemaBuilder extends SchemaReader
{
	private static final String SCHEMA_NAME = "Schema Schema";
	private static final String SCHEMA_VERSION = "http://www.appcelerator.com/studio/content-assist/schema/1.0";

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
		result.setName(SCHEMA_NAME);
		result.setVersion(SCHEMA_VERSION);
		result.setDescription("A JSON schema that describes the structure of a JSON schema");
		result.setResult("Schema");

		// create Schema
		SchemaObject schema = (SchemaObject) result.addType("Schema");
		schema.addProperty("name", "String");
		schema.addProperty("version", "String");
		schema.addProperty("description", "String");
		schema.addProperty("result", "String");
		schema.addProperty("types", "Array<Type>");

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

		return result;
	}
}
