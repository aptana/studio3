/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;

public class TagWithName extends Tag
{
	private String _name;

	/**
	 * TagWithName
	 */
	protected TagWithName(TagType type, String name, String text)
	{
		super(type, text);

		this._name = name;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.sdoc.model.Tag#toSource(com.aptana.parsing.io.SourceWriter)
	 */
	@Override
	public void toSource(SourcePrinter writer)
	{
		TagType type = this.getType();

		if (type == TagType.UNKNOWN)
		{
			writer.print(this._name);
		}
		else
		{
			writer.print(type.toString()).print(" {").print(this._name).print('}'); //$NON-NLS-1$
		}

		String text = this.getText();

		if (text != null && !StringUtil.isEmpty(text))
		{
			writer.print(' ').print(text);
		}
	}
}
