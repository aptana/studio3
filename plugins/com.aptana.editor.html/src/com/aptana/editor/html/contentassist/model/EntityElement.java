/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class EntityElement extends BaseElement
{
	private static final String DECIMAL_VALUE_PROPERTY = "decimalValue"; //$NON-NLS-1$
	private static final String HEX_VALUE_PROPERTY = "hexValue"; //$NON-NLS-1$

	private String _decimalValue;
	private String _hexValue;

	public EntityElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.mortbay.util.ajax.JSON.Convertible#fromJSON(java.util.Map)
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
	 * @see org.mortbay.util.ajax.JSON.Convertible#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(HEX_VALUE_PROPERTY, this.getHexValue());
		out.add(DECIMAL_VALUE_PROPERTY, this.getDecimalValue());
	}
}
