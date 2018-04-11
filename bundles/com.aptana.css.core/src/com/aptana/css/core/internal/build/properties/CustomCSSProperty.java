/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.util.ApplContext;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.internal.build.AptanaCSSInheritanceProperties;
import com.aptana.css.core.internal.build.AptanaCSSStyle;

/**
 * Abstract custom CSS property.
 * 
 * @author Denis Denisenko
 */
public abstract class CustomCSSProperty extends CssProperty
{

	private final String propertyName;

	protected CustomCSSProperty(String propertyName)
	{
		this.propertyName = propertyName;
	}

	@Override
	public void addToStyle(ApplContext ac, CssStyle style)
	{
		AptanaCSSStyle aptanaStyle = (AptanaCSSStyle) style;

		// if such property is already set, we should add a redefinition warning
		if (aptanaStyle.getProperty(getPropertyName()) != null)
		{
			aptanaStyle.addRedefinitionWarning(ac, this);
		}

		// adding self
		aptanaStyle.setProperty(getPropertyName(), this);
	}

	@Override
	public CssProperty getPropertyInStyle(CssStyle style, boolean resolve)
	{
		AptanaCSSStyle aptanaStyle = (AptanaCSSStyle) style;
		if (resolve)
		{
			// getting property using cascading order
			return aptanaStyle.getPropertyCascadingOrder(getPropertyName());
		}
		else
		{
			// getting plain property value
			return aptanaStyle.getProperty(getPropertyName());
		}
	}

	@Override
	public boolean Inherited()
	{
		// checking inheritance properties
		return AptanaCSSInheritanceProperties.getInheritance(this);
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	@Override
	public boolean equals(CssProperty property)
	{
		if (!(property instanceof CustomCSSProperty))
		{
			return false;
		}
		Object value = get();
		return value != null && value.equals(property.get());
	}

	@Override
	public String toString()
	{
		Object value = get();
		return (value == null) ? StringUtil.EMPTY : value.toString();
	}

	/**
	 * W3C validator does not accept CSS properties that start with '-' sign. That makes custom properties
	 * implementation in need to bypass such a restriction. This method would get the property name without the starting
	 * minus sign if any.
	 * 
	 * @return the property name without the leading minus
	 */
	protected String getPropertyNameNoMinus()
	{
		String propertyName = getPropertyName();
		if (StringUtil.isEmpty(propertyName))
		{
			return propertyName;
		}
		if (propertyName.charAt(0) == '-')
		{
			return propertyName.substring(1);
		}
		return propertyName;
	}
}
