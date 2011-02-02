/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import com.aptana.scripting.ScriptingActivator;

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
		return ScriptingActivator.getDefaultRunType().getName();
	}

	@Override
	public String[] getTriggerTypeValues(TriggerType type)
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