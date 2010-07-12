package com.aptana.editor.js.sdoc.model;

import java.util.List;

import com.aptana.parsing.io.SourcePrinter;

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
		return this._parameter.getName();
	}

	/**
	 * getUsage
	 * 
	 * @return
	 */
	public Usage getUsage()
	{
		return this._parameter.getUsage();
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
			if (first == false)
			{
				writer.print(","); //$NON-NLS-1$
			}
			else
			{
				first = false;
			}

			type.toSource(writer);
		}
		writer.print("} "); //$NON-NLS-1$

		this._parameter.toSource(writer);

		String text = this.getText();

		if (text != null && text.isEmpty() == false)
		{
			writer.println();
			writer.printIndent().print("    ").print(text); //$NON-NLS-1$
		}
	}
}
