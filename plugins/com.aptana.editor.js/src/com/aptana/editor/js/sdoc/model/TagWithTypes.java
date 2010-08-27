package com.aptana.editor.js.sdoc.model;

import java.util.List;

import com.aptana.parsing.io.SourcePrinter;

public abstract class TagWithTypes extends Tag
{
	private List<Type> _types;

	/**
	 * ExceptionTag
	 */
	public TagWithTypes(TagType type, List<Type> types, String text)
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
					writer.print(","); //$NON-NLS-1$
				}
				
				type.toSource(writer);
			}
		}

		writer.print("}"); //$NON-NLS-1$

		String text = this.getText();

		if (text != null && text.isEmpty() == false)
		{
			writer.print(" ").print(text); //$NON-NLS-1$
		}
	}
}
