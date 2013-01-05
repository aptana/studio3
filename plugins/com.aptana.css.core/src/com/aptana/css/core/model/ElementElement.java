/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

public class ElementElement extends BaseElement
{

	private static final String PROPERTIES_PROPERTY = "properties"; //$NON-NLS-1$
	private static final String REMARK_PROPERTY = "remark"; //$NON-NLS-1$
	private static final String DISPLAY_NAME_PROPERTY = "displayName"; //$NON-NLS-1$

	private String _displayName;
	private List<String> _properties;
	private String _remark;

	/**
	 * ElementElement
	 */
	public ElementElement()
	{
	}

	/**
	 * addProperty
	 * 
	 * @param name
	 */
	public void addProperty(String name)
	{
		if (!StringUtil.isEmpty(name))
		{
			if (this._properties == null)
			{
				this._properties = new ArrayList<String>();
			}

			this._properties.add(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.css.core.model.BaseElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setDisplayName(StringUtil.getStringValue(object.get(DISPLAY_NAME_PROPERTY)));
		this.setRemark(StringUtil.getStringValue(object.get(REMARK_PROPERTY)));

		this._properties = IndexUtil.createList(object.get(PROPERTIES_PROPERTY));
	}

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return StringUtil.getStringValue(this._displayName);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<String> getProperties()
	{
		return CollectionsUtil.getListValue(this._properties);
	}

	/**
	 * getRemark
	 * 
	 * @return
	 */
	public String getRemark()
	{
		return StringUtil.getStringValue(this._remark);
	}

	/**
	 * setDisplayName
	 * 
	 * @param name
	 */
	public void setDisplayName(String name)
	{
		this._displayName = name;
	}

	/**
	 * setRemark
	 * 
	 * @param remark
	 */
	public void setRemark(String remark)
	{
		this._remark = remark;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.css.core.model.BaseElement#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(DISPLAY_NAME_PROPERTY, this.getDisplayName());
		out.add(REMARK_PROPERTY, this.getRemark());
		out.add(PROPERTIES_PROPERTY, this.getProperties());
	}
}
