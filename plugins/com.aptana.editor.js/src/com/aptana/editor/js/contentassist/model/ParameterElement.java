package com.aptana.editor.js.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public class ParameterElement
{
	private String _name;
	private List<String> _types = new LinkedList<String>();
	private String _usage;
	private String _description;

	/**
	 * ParameterElement
	 */
	public ParameterElement()
	{
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String type)
	{
		this._types.add(type);
	}

	/**
	 * getDescription
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getName
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public String[] getTypes()
	{
		return this._types.toArray(new String[this._types.size()]);
	}

	/**
	 * getUsage
	 * 
	 * @return
	 */
	public String getUsage()
	{
		return this._usage;
	}

	/**
	 * setDescription
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setName
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * setUsage
	 * 
	 * @param value
	 */
	public void setUsage(String usage)
	{
		this._usage = usage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		if ("optional".equals(this.getUsage())) //$NON-NLS-1$
		{
			return "[" + this.getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			return this.getName();
		}
	}
}
