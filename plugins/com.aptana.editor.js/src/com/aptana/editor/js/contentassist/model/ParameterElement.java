package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterElement
{
	private String _name;
	private List<String> _types;
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
		if (type != null && type.length() > 0)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<String>();
			}
			
			this._types.add(type);
		}
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
	public List<String> getTypes()
	{
		List<String> result = this._types;
		
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return result;
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
