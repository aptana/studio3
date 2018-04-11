/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import java.util.List;

import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;

public class TagWithTypes extends Tag
{
	private List<Type> _types;

	/**
	 * ExceptionTag
	 */
	protected TagWithTypes(TagType type, List<Type> types, String text)
	{
		super(type, text);

		this._types = types;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<Type> getTypes()
	{
		return this._types;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.sdoc.model.Tag#toSource(com.aptana.parsing.io.SourceWriter)
	 */
	@Override
	public void toSource(SourcePrinter writer)
	{
		TagType tagType = this.getType();

		if (tagType != null)
		{
			writer.print(tagType);
		}
		else
		{
			writer.print(TagType.UNKNOWN);
		}

		writer.print(" {"); //$NON-NLS-1$

		boolean first = true;

		if (this._types != null)
		{
			for (Type type : this._types)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					writer.print(',');
				}

				type.toSource(writer);
			}
		}

		writer.print('}');

		String text = this.getText();

		if (text != null && !StringUtil.isEmpty(text))
		{
			writer.print(' ').print(text);
		}
	}
}
