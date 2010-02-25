package com.aptana.scripting.model;

public class TemplateElement extends CommandElement
{
	private String _filetype;

	public TemplateElement(String path)
	{
		super(path);
	}

	/**
	 * setFiletype
	 * 
	 * @param value
	 */
	public void setFiletype(String value)
	{
		this._filetype = value;
	}

	public String getFiletype()
	{
		return this._filetype;
	}
}