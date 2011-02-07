/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaSchema
 */
public class SchemaSchema extends SchemaDocument
{
	/**
	 * SchemaSchema
	 */
	public SchemaSchema()
	{
		this.initialize();
	}

	/**
	 * initialize
	 */
	protected void initialize()
	{
		// create type values
		SchemaObject document_value = new SchemaObject();

		// create schema value
		SchemaObject schema_value = new SchemaObject();
		SchemaProperty schema_name = new SchemaProperty("name", "String");
		SchemaProperty schema_result = new SchemaProperty("result", "<type-spec>");

		schema_value.addProperty(schema_name);
		schema_value.addProperty(schema_result);

		SchemaProperty schema_property = new SchemaProperty("schema", "Schema");
		document_value.addProperty(schema_property);
		
		// create types
		SchemaType schema_type = new SchemaType("Schema", schema_value);
		SchemaType document_type = new SchemaType("SchemaDocument", document_value);

		// create document and add types
		this.addType(document_type);
		this.addType(schema_type);
	}
}
