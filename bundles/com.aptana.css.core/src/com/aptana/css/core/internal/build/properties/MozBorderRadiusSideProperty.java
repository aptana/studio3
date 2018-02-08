/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import java.util.ArrayList;
import java.util.List;

import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssLength;
import org.w3c.css.values.CssNumber;
import org.w3c.css.values.CssValue;

/**
 * Superclass for "-moz-border-radius-*" properties.
 * 
 * @author Denis Denisenko
 */
public abstract class MozBorderRadiusSideProperty extends CustomCSSProperty
{

	private final List<CssValue> values;

	protected MozBorderRadiusSideProperty(String propertyName)
	{
		super(propertyName);
		values = null;
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @param check
	 *            whether to check property value
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected MozBorderRadiusSideProperty(String propertyName, ApplContext context, CssExpression expression, boolean check)
			throws InvalidParamException
	{
		super(propertyName);

		// checking the number of expressions
		if (check)
		{
			if (expression.getCount() > 2)
			{
				throw new InvalidParamException("unrecognize", context); //$NON-NLS-1$
			}
			else if (expression.getCount() < 2)
			{
				throw new InvalidParamException("few-value", context); //$NON-NLS-1$
			}
		}
		setByUser();

		values = new ArrayList<CssValue>();

		CssValue value1 = expression.getValue();
		CssValue value2 = expression.getNextValue();
		CssValue convertedValue1 = checkValue(value1, context);
		CssValue convertedValue2 = checkValue(value2, context);
		values.add(convertedValue1);
		values.add(convertedValue2);

		expression.next();
		expression.next();
	}

	/**
	 * @param property
	 *            the property name
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected MozBorderRadiusSideProperty(String propertyName, ApplContext context, CssExpression expression)
			throws InvalidParamException
	{
		this(propertyName, context, expression, false);
	}

	@Override
	public Object get()
	{
		return values;
	}

	/**
	 * Checks value and converts if needed.
	 * 
	 * @param value
	 *            the value to check
	 * @param context
	 *            the current context
	 * @throws InvalidParamException
	 *             if check fails
	 * @return checked or converted value
	 */
	private CssValue checkValue(CssValue value, ApplContext context) throws InvalidParamException
	{
		if (value instanceof CssLength)
		{
			return value;
		}
		if (value instanceof CssNumber)
		{
			return ((CssNumber) value).getLength();
		}

		throw new InvalidParamException("value", value.toString(), getPropertyNameNoMinus(), context); //$NON-NLS-1$
	}
}
