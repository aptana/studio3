/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssValue;

/**
 * Abstract CSS property that has fixed set of values. Optimized. Supports default values.
 * 
 * @author Denis Denisenko
 */
public abstract class CSSFixedSetProperty extends CustomCSSProperty
{

	/**
	 * value index
	 */
	private final int valueIndex;

	/**
	 * possible values
	 */
	private final String[] values;

	/**
	 * @param propertyName
	 *            the property name
	 * @param values
	 *            fixed values set; must not be null or empty. Common convention is to set the default value of the
	 *            property first in the result array. Otherwise {@link CSSFixedSetProperty#isDefault()} should be
	 *            overridden.
	 */
	protected CSSFixedSetProperty(String propertyName, String[] values)
	{
		super(propertyName);
		this.values = values;
		valueIndex = 0;
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param values
	 *            fixed values set; must not be null or empty. Common convention is to set the default value of the
	 *            property first in the result array. Otherwise {@link CSSFixedSetProperty#isDefault()} should be
	 *            overridden.
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @param check
	 *            whether to check the property value
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected CSSFixedSetProperty(String propertyName, String[] values, ApplContext context, CssExpression expression,
			boolean check) throws InvalidParamException
	{
		super(propertyName);
		this.values = values;

		// checking the number of expressions
		if (check && expression.getCount() != 1)
		{
			throw new InvalidParamException("unrecognize", context); //$NON-NLS-1$
		}
		setByUser();

		CssValue val = expression.getValue();
		if (val instanceof CssIdent && val.get() instanceof String)
		{
			valueIndex = getIndex((String) val.get());
			if (valueIndex == -1)
			{
				throw new InvalidParamException("value", val.toString(), //$NON-NLS-1$
						getPropertyNameNoMinus(), context);
			}
			expression.next();
		}
		else
		{
			throw new InvalidParamException("value", val.toString(), //$NON-NLS-1$
					getPropertyNameNoMinus(), context);
		}
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param values
	 *            fixed values set; must not be null or empty. Common convention is to set the default value of the
	 *            property first in the result array. Otherwise {@link CSSFixedSetProperty#isDefault()} should be
	 *            overridden.
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected CSSFixedSetProperty(String propertyName, String[] values, ApplContext context, CssExpression expression)
			throws InvalidParamException
	{
		this(propertyName, values, context, expression, false);
	}

	@Override
	public Object get()
	{
		return values[valueIndex];
	}

	@Override
	public boolean isDefault()
	{
		return valueIndex == 0;
	}

	/**
	 * Gets the value index.
	 * 
	 * @param value
	 *            the value to find
	 * @return its index, or -1 if not found
	 */
	private int getIndex(String value)
	{
		if (value == null)
		{
			return -1;
		}
		// checking one by one is cheaper then storing hash map
		for (int i = 0; i < values.length; ++i)
		{
			if (value.equals(values[i]))
			{
				return i;
			}
		}
		return -1;
	}
}
