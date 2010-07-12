package com.aptana.editor.js.sdoc.model;

import com.aptana.parsing.io.SourcePrinter;

public abstract class TagWithName extends Tag
{
	private String _name;

	/**
	 * TagWithName
	 */
	public TagWithName(TagType type, String name, String text)
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
			writer.print(type.toString()).print(" {").print(this._name).print("}"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String text = this.getText();

		if (text != null && text.isEmpty() == false)
		{
			writer.print(" ").print(text); //$NON-NLS-1$
		}
	}
}
