package com.aptana.scripting.model;

import com.aptana.scripting.Activator;

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

	@Override
	public String getOutputType()
	{
		return OutputType.DISCARD.getName();
	}

	@Override
	public InputType[] getInputTypes()
	{
		return new InputType[0];
	}

	@Override
	public WorkingDirectoryType getWorkingDirectoryType()
	{
		return WorkingDirectoryType.UNDEFINED;
	}

	@Override
	public String[] getKeyBindings()
	{
		return new String[0];
	}

	@Override
	public String getRunType()
	{
		return Activator.getDefaultRunType().getName();
	}

	@Override
	public String[] getTriggers()
	{
		return new String[0];
	}

	/**
	 * Always executable.
	 */
	@Override
	public boolean isExecutable()
	{
		return true;
	}

}