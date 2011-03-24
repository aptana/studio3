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
	private SchemaContext _context;

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
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.END_ARRAY, null);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endJSON()
	 */
	public void endJSON() throws ParseException, IOException
	{
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.END_PARSE, null);
			this._schema.exit();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endObject()
	 */
	public boolean endObject() throws ParseException, IOException
	{
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.END_OBJECT, null);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endObjectEntry()
	 */
	public boolean endObjectEntry() throws ParseException, IOException
	{
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.END_OBJECT_ENTRY, null);
		}

		return true;
	}

	/**
	 * getContext
	 * 
	 * @return
	 */
	public SchemaContext getContext()
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
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.PRIMITIVE, value);
		}

		return true;
	}

	/**
	 * read
	 * 
	 * @param input
	 */
	public void read(Reader input, SchemaContext context)
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
			// log
		}
		catch (ParseException e)
		{
			// log
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
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.START_ARRAY, null);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startJSON()
	 */
	public void startJSON() throws ParseException, IOException
	{
		if (this._schema != null)
		{
			this._schema.enter();
			this._schema.transition(this._context, SchemaEventType.START_PARSE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startObject()
	 */
	public boolean startObject() throws ParseException, IOException
	{
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.START_OBJECT, null);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startObjectEntry(java.lang.String)
	 */
	public boolean startObjectEntry(String key) throws ParseException, IOException
	{
		if (this._schema != null)
		{
			this._schema.transition(this._context, SchemaEventType.START_OBJECT_ENTRY, key);
		}

		return true;
	}
}
