/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.IRange;

/**
 * @author Kevin Lindsey
 */
public class ParseNodeAttribute implements IParseNodeAttribute
{
	private final IParseNode _parent;
	private final String _name;
	private final String _value;
	private final IRange _nameRange;
	private final IRange _valueRange;

	/**
	 * ParseNodeAttribute
	 * 
	 * @param parent
	 * @param name
	 * @param value
	 */
	public ParseNodeAttribute(IParseNode parent, String name, String value)
	{
		this(parent, name, value, null, null);
	}

	public ParseNodeAttribute(IParseNode parent, String name, String value, IRange nameRange, IRange valueRange)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException(Messages.ParseNodeAttribute_Undefined_Parent);
		}
		if (name == null)
		{
			throw new IllegalArgumentException(Messages.ParseNodeAttribute_Undefined_Name);
		}
		if (value == null)
		{
			throw new IllegalArgumentException(Messages.ParseNodeAttribute_Undefined_Value);
		}

		this._parent = parent;
		this._name = name;
		this._value = value;
		this._nameRange = nameRange;
		this._valueRange = valueRange;
	}

	/**
	 * @see com.aptana.parsing.ast.IParseNodeAttribute#getName()
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * @see com.aptana.parsing.ast.IParseNodeAttribute#getValue()
	 */
	public String getValue()
	{
		return this._value;
	}

	/**
	 * @see com.aptana.parsing.ast.IParseNodeAttribute#getParent()
	 */
	public IParseNode getParent()
	{
		return this._parent;
	}

	/*
	 * @see com.aptana.parsing.ast.IParseNodeAttribute#getNameRange()
	 */
	public IRange getNameRange()
	{
		return _nameRange;
	}

	/*
	 * @see com.aptana.parsing.ast.IParseNodeAttribute#getValueRange()
	 */
	public IRange getValueRange()
	{
		return _valueRange;
	}

}
