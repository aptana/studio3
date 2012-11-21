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

public class ParamTag extends TagWithTypes
{
	private Parameter _parameter;

	/**
	 * ParamTag
	 */
	public ParamTag(Parameter parameter, List<Type> types, String text)
	{
		super(TagType.PARAM, types, text);

		this._parameter = parameter;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		String result = ""; //$NON-NLS-1$

		if (this._parameter != null)
		{
			result = this._parameter.getName();
		}

		return result;
	}

	/**
	 * getUsage
	 * 
	 * @return
	 */
	public Usage getUsage()
	{
		Usage result = Usage.UNKNOWN;

		if (this._parameter != null)
		{
			result = this._parameter.getUsage();
		}

		return result;
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		writer.print(this.getType().toString());

		boolean first = true;
		writer.print(" {"); //$NON-NLS-1$
		for (Type type : this.getTypes())
		{
			if (!first)
			{
				writer.print(',');
			}
			else
			{
				first = false;
			}

			type.toSource(writer);
		}
		writer.print("} "); //$NON-NLS-1$

		if (this._parameter != null)
		{
			this._parameter.toSource(writer);
		}

		String text = this.getText();

		if (text != null && !StringUtil.isEmpty(text))
		{
			writer.println();
			writer.printIndent().print("    ").print(text); //$NON-NLS-1$
		}
	}
}
