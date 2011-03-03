/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.io.IOException;
import java.io.Reader;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * SchemaReader
 */
public class SchemaReader implements ContentHandler
{
	private Schema _schema;
	private Context _context;

	/**
	 * SchemaReader
	 * 
	 * @param schema
	 */
	public SchemaReader(Schema schema)
	{
		this._schema = schema;
	}

	/**
	 * SchemaReader
	 */
	protected SchemaReader()
	{

	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endArray()
	 */
	public boolean endArray() throws ParseException, IOException
	{
		System.out.println("end array");

		this._schema.transition(this._context, EventType.END_ARRAY, null);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endJSON()
	 */
	public void endJSON() throws ParseException, IOException
	{
		System.out.println("end parse");

		this._schema.transition(this._context, EventType.END_PARSE, null);
		this._schema.exit();
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endObject()
	 */
	public boolean endObject() throws ParseException, IOException
	{
		System.out.println("end object");

		this._schema.transition(this._context, EventType.END_OBJECT, null);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endObjectEntry()
	 */
	public boolean endObjectEntry() throws ParseException, IOException
	{
		System.out.println("end object entry");

		this._schema.transition(this._context, EventType.END_OBJECT_ENTRY, null);

		return true;
	}

	/**
	 * getContext
	 * 
	 * @return
	 */
	public Context getContext()
	{
		return this._context;
	}

	/**
	 * getSchema
	 * 
	 * @return
	 */
	public Schema getSchema()
	{
		return this._schema;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#primitive(java.lang.Object)
	 */
	public boolean primitive(Object value) throws ParseException, IOException
	{
		System.out.println("primitive: " + value);

		this._schema.transition(this._context, EventType.PRIMITIVE, value);

		return true;
	}

	/**
	 * read
	 * 
	 * @param input
	 */
	public void read(Reader input, Context context)
	{
		// save reference to context so we can use it in the event handlers
		this._context = context;

		JSONParser parser = new JSONParser();

		try
		{
			parser.parse(input, this);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		// drop ref to context
		this._context = null;
	}

	/**
	 * setSchema
	 * 
	 * @param schema
	 */
	protected void setSchema(Schema schema)
	{
		this._schema = schema;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startArray()
	 */
	public boolean startArray() throws ParseException, IOException
	{
		System.out.println("start array");

		this._schema.transition(this._context, EventType.START_ARRAY, null);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startJSON()
	 */
	public void startJSON() throws ParseException, IOException
	{
		System.out.println("start parse");

		this._schema.enter();
		this._schema.transition(this._context, EventType.START_PARSE, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startObject()
	 */
	public boolean startObject() throws ParseException, IOException
	{
		System.out.println("start object");

		this._schema.transition(this._context, EventType.START_OBJECT, null);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startObjectEntry(java.lang.String)
	 */
	public boolean startObjectEntry(String key) throws ParseException, IOException
	{
		System.out.println("start object entry: " + key);

		this._schema.transition(this._context, EventType.START_OBJECT_ENTRY, key);

		return true;
	}
}
