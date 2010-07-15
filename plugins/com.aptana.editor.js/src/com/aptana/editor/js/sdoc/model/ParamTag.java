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
		String result = "";
		
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

		if (this._parameter != null)
		{
			this._parameter.toSource(writer);
		}

		String text = this.getText();

		if (text != null && text.isEmpty() == false)
		{
			writer.println();
			writer.printIndent().print("    ").print(text); //$NON-NLS-1$
		}
	}
}
