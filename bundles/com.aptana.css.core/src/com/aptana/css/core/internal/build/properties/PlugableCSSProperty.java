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
import org.w3c.css.values.CssValue;

/**
 * Pluggable CSS property.
 * 
 * @author Denis Denisenko
 */
@SuppressWarnings("rawtypes")
public class PlugableCSSProperty extends CustomCSSProperty
{

	private final CssValue value;

	public PlugableCSSProperty(String propertyName)
	{
		super(propertyName);
		value = null;
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param types
	 *            the types that this property accepts
	 * @param converters
	 *            the list of type converters
	 * @param validators
	 *            the list of value validators
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @param check
	 *            whether to check the property value
	 * @throws InvalidParamException
	 *             if the expression is invalid
	 */
	public PlugableCSSProperty(String propertyName, Class[] types, ICSSValueTypeConverter[] converters,
			ICSSValueValidator[] validators, ApplContext context, CssExpression expression, boolean check)
			throws InvalidParamException
	{
		super(propertyName);

		// checking the number of expressions.
		if (check && expression.getCount() != 1)
		{
			throw new InvalidParamException("unrecognize", context); //$NON-NLS-1$
		}
		setByUser();

		CssValue val = expression.getValue();
		// checks types
		for (Class type : types)
		{
			if (type.isInstance(val))
			{
				expression.next();

				// converting
				if (converters != null && converters.length != 0)
				{
					for (ICSSValueTypeConverter converter : converters)
					{
						CssValue convertedValue = converter.convert(val);
						if (convertedValue != null)
						{
							val = convertedValue;
							break;
						}
					}
				}

				// validating
				if (validators != null && validators.length != 0)
				{
					for (ICSSValueValidator validator : validators)
					{
						if (validator.canValidate(val))
						{
							if (!validator.isValid(val))
							{
								throw new InvalidParamException("value", val.toString(), getPropertyNameNoMinus(), //$NON-NLS-1$
										context);
							}
						}
					}
				}

				this.value = val;
				return;
			}
		}

		throw new InvalidParamException("value", val.toString(), getPropertyNameNoMinus(), context); //$NON-NLS-1$
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param types
	 *            the types that this property accepts
	 * @param converters
	 *            the list of type converters
	 * @param validators
	 *            the list of value validators
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if the expression is invalid
	 */
	public PlugableCSSProperty(String propertyName, Class[] types, ICSSValueTypeConverter[] converters,
			ICSSValueValidator[] validators, ApplContext context, CssExpression expression) throws InvalidParamException
	{
		this(propertyName, types, converters, validators, context, expression, false);
	}

	@Override
	public Object get()
	{
		return value;
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
