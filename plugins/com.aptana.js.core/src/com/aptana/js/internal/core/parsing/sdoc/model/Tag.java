/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import beaver.Symbol;

import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;

public class Tag extends Symbol
{
	private TagType _type;
	private String _text;

	/**
	 * Tag
	 * 
	 * @param name
	 */
	protected Tag(TagType type)
	{
		this(type, StringUtil.EMPTY);
	}

	/**
	 * Tag
	 * 
	 * @param name
	 */
	protected Tag(TagType type, String text)
	{
		this._type = type;
		this._text = text;
	}

	/**
	 * getText
	 * 
	 * @return
	 */
	public String getText()
	{
		return this._text;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public TagType getType()
	{
		return this._type;
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter writer = new SourcePrinter();

		this.toSource(writer);

		writer.println();

		return writer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		if (this._type != null)
		{
			writer.print(this._type.toString());
		}
		else
		{
			writer.print(TagType.UNKNOWN.toString());
		}

		if (this._text != null && !StringUtil.isEmpty(this._text))
		{
			writer.print(' ').print(this._text);
		}
	}
}
