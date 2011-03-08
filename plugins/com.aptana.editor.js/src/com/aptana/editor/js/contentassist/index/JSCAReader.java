/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import com.aptana.json.Schema;
import com.aptana.json.SchemaObject;
import com.aptana.json.SchemaReader;

/**
 * JSCAReader
 */
public class JSCAReader extends SchemaReader
{
	/**
	 * JSCAReader
	 */
	public JSCAReader()
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
		result.setName("JSCA Schema");
		result.setVersion("http://www.appcelerator.com/studio/content-assist/jsca/1.0");
		result.setDescription("A JSON schema describing the structure of JS Metadata used in Aptana Studio for content assist");
		result.setResult("JSMetadata");

		// JSMetadata
		SchemaObject jsMetadata = (SchemaObject) result.addType("JSMetadata");
		jsMetadata.addProperty("version", "Number");
		jsMetadata.addProperty("aliases", "Array<Alias>");
		jsMetadata.addProperty("types", "Array<Type>");

		// Alias
		SchemaObject alias = (SchemaObject) result.addType("Alias");
		alias.addProperty("name", "String");
		alias.addProperty("description", "String");
		alias.addProperty("type", "String");

		// Type
		SchemaObject type = (SchemaObject) result.addType("Type");
		type.addProperty("name", "String");
		type.addProperty("description", "String");
		type.addProperty("deprecated", "Boolean");
		type.addProperty("since", "Array<Since>");
		type.addProperty("inherits", "String");
		type.addProperty("properties", "Array<Property>");
		type.addProperty("functions", "Array<Function>");
		type.addProperty("events", "Array<Event>");
		type.addProperty("remarks", "Array<String>");
		
		// NOTE: These are not currently valid, but are being used here so we can find errors in the api.jsca file
		type.addProperty("examples", "Array<Example>");
		type.addProperty("userAgents", "Array<UserAgent>");

		// UserAgent
		SchemaObject userAgent = (SchemaObject) result.addType("UserAgent");
		userAgent.addProperty("platform", "String");
		userAgent.addProperty("version", "String");
		userAgent.addProperty("os", "String");
		userAgent.addProperty("osVersion", "String");
		userAgent.addProperty("description", "String");

		// Since
		SchemaObject since = (SchemaObject) result.addType("Since");
		since.addProperty("name", "String");
		since.addProperty("version", "String");

		// Property
		SchemaObject property = (SchemaObject) result.addType("Property");
		property.addProperty("name", "String");
		property.addProperty("description", "String");
		property.addProperty("userAgents", "Array<UserAgent>");
		property.addProperty("since", "Array<Since>");
		property.addProperty("isInstanceProperty", "Boolean");
		property.addProperty("isClassProperty", "Boolean");
		property.addProperty("isInternal", "Boolean");
		property.addProperty("type", "String");
		property.addProperty("examples", "Array<Example>");

		// Function
		SchemaObject function = (SchemaObject) result.addType("Function");
		function.addProperty("name", "String");
		function.addProperty("description", "String");
		function.addProperty("userAgents", "Array<UserAgent>");
		function.addProperty("since", "Array<Since>");
		function.addProperty("isInstanceProperty", "Boolean");
		function.addProperty("isClassProperty", "Boolean");
		function.addProperty("isInternal", "Boolean");
		function.addProperty("examples", "Array<Example>");
		function.addProperty("parameters", "Array<Parameter>");
		function.addProperty("references", "Array<Reference>");
		function.addProperty("exceptions", "Array<Exceptions>");
		function.addProperty("returnTypes", "Array<ReturnType>");
		function.addProperty("isConstructor", "Boolean");
		function.addProperty("isMethod", "Boolean");

		// Event
		SchemaObject event = (SchemaObject) result.addType("Event");
		event.addProperty("name", "String");
		event.addProperty("description", "String");
		event.addProperty("properties", "Array<EventProperty>");

		// EventProperty
		SchemaObject eventProperty = (SchemaObject) result.addType("EventProperty");
		eventProperty.addProperty("name", "String");
		eventProperty.addProperty("description", "String");
		eventProperty.addProperty("type", "String");

		// ReturnType
		SchemaObject returnType = (SchemaObject) result.addType("ReturnType");
		returnType.addProperty("type", "String");
		returnType.addProperty("description", "String");

		// Example
		SchemaObject example = (SchemaObject) result.addType("Example");
		example.addProperty("name", "String");
		example.addProperty("code", "String");

		// Parameter
		SchemaObject parameter = (SchemaObject) result.addType("Parameter");
		parameter.addProperty("name", "String");
		//parameter.addProperty("type", "String");
		parameter.addProperty("types", "Array<String>");	// NOTE: see previous commented line for correct schema
		parameter.addProperty("usage", "String");
		parameter.addProperty("description", "String");

		// Exception
		SchemaObject exception = (SchemaObject) result.addType("Exception");
		exception.addProperty("type", "String");
		exception.addProperty("description", "String");

		return result;
	}
}
