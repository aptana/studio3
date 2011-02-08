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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * SchemaDocument
 */
public class SchemaDocument implements ContentHandler
{
	private Map<String, IState> _typesByName;
	private IState _rootType;
	private Stack<IState> _typeStack;
	private IState _currentType;

	/**
	 * SchemaDocument
	 */
	public SchemaDocument()
	{
		this._typeStack = new Stack<IState>();
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String name, IState type)
	{
		// TODO: Warn on duplicate entry or merge entries?
		if (this._typesByName == null)
		{
			this._typesByName = new HashMap<String, IState>();
		}

		this._typesByName.put(name, type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endArray()
	 */
	public boolean endArray() throws ParseException, IOException
	{
		System.out.println("end array");

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endJSON()
	 */
	public void endJSON() throws ParseException, IOException
	{
		System.out.println("end parse");
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endObject()
	 */
	public boolean endObject() throws ParseException, IOException
	{
		System.out.println("end object");

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#endObjectEntry()
	 */
	public boolean endObjectEntry() throws ParseException, IOException
	{
		System.out.println("end object entry");

		return true;
	}

	/**
	 * getRootType
	 * 
	 * @return
	 */
	public IState getRootType()
	{
		return this._rootType;
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	public IState getType(String typeName)
	{
		IState result = null;

		if (this._typesByName != null)
		{
			result = this._typesByName.get(typeName);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#primitive(java.lang.Object)
	 */
	public boolean primitive(Object value) throws ParseException, IOException
	{
		System.out.println("primitive: " + value);

		return true;
	}

	/**
	 * read
	 * 
	 * @param input
	 */
	public void read(Reader input)
	{
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
	}

	/**
	 * setRootType
	 * 
	 * @param typeName
	 */
	public void setRootType(String typeName)
	{
		this._rootType = this.getType(typeName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startArray()
	 */
	public boolean startArray() throws ParseException, IOException
	{
		System.out.println("start array");
		;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startJSON()
	 */
	public void startJSON() throws ParseException, IOException
	{
		System.out.println("start parse");

		this._typeStack.clear();
		this._currentType = this._rootType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startObject()
	 */
	public boolean startObject() throws ParseException, IOException
	{
		System.out.println("start object");

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.json.simple.parser.ContentHandler#startObjectEntry(java.lang.String)
	 */
	public boolean startObjectEntry(String key) throws ParseException, IOException
	{
		System.out.println("start object entry: " + key);

		return true;
	}
}
