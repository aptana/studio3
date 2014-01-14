/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

public class ParameterElement implements Convertible
{
	private static final String TYPES_PROPERTY = "types"; //$NON-NLS-1$
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String USAGE_PROPERTY = "usage"; //$NON-NLS-1$
	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$

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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setName(StringUtil.getStringValue(object.get(NAME_PROPERTY)));
		this.setUsage(StringUtil.getStringValue(object.get(USAGE_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));

		// JSCA contains a single "type" value
		if (object.containsKey("type"))
		{
			this._types = CollectionsUtil.newList((String) object.get("type"));
		}
		else
		{
			// Our index contains multiple types as a list
			this._types = IndexUtil.createList(object.get(TYPES_PROPERTY));
		}
	}

	/**
	 * getDescription
	 */
	public String getDescription()
	{
		return StringUtil.getStringValue(this._description);
	}

	/**
	 * getName
	 */
	public String getName()
	{
		return StringUtil.getStringValue(this._name);
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<String> getTypes()
	{
		return CollectionsUtil.getListValue(this._types);
	}

	/**
	 * getUsage
	 * 
	 * @return
	 */
	public String getUsage()
	{
		return StringUtil.getStringValue(this._usage);
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
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(NAME_PROPERTY, this.getName());
		out.add(USAGE_PROPERTY, this.getUsage());
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
		out.add(TYPES_PROPERTY, this.getTypes());
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
