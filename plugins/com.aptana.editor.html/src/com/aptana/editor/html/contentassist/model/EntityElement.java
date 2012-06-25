/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.util.Map;

import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;

public class EntityElement extends BaseElement<EntityElement.Property>
{
	enum Property implements IPropertyInformation<EntityElement>
	{
		NAME(Messages.EntityElement_NameLabel)
		{
			public Object getPropertyValue(EntityElement node)
			{
				return node.getName();
			}
		};

		private String header;
		private String category;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		private Property(String header, String category)
		{
			this.category = category;
		}

		public String getCategory()
		{
			return category;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private static final String DECIMAL_VALUE_PROPERTY = "decimalValue"; //$NON-NLS-1$
	private static final String HEX_VALUE_PROPERTY = "hexValue"; //$NON-NLS-1$

	private String _decimalValue;
	private String _hexValue;

	public EntityElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setHexValue(StringUtil.getStringValue(object.get(HEX_VALUE_PROPERTY)));
		this.setDecimalValue(StringUtil.getStringValue(object.get(DECIMAL_VALUE_PROPERTY)));
	}

	/**
	 * getDecimalValue
	 * 
	 * @return the decimalValue
	 */
	public String getDecimalValue()
	{
		return StringUtil.getStringValue(this._decimalValue);
	}

	/**
	 * getHexValue
	 * 
	 * @return the hexValue
	 */
	public String getHexValue()
	{
		return StringUtil.getStringValue(this._hexValue);
	}

	/**
	 * setDecimalValue
	 * 
	 * @param decimalValue
	 *            the value to set
	 */
	public void setDecimalValue(String value)
	{
		this._decimalValue = value;
	}

	/**
	 * setHexValue
	 * 
	 * @param hexValue
	 *            the hexValue to set
	 */
	public void setHexValue(String value)
	{
		this._hexValue = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(HEX_VALUE_PROPERTY, this.getHexValue());
		out.add(DECIMAL_VALUE_PROPERTY, this.getDecimalValue());
	}
}
