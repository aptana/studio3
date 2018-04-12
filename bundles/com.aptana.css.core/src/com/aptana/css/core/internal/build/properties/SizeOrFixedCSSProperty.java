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
import org.w3c.css.values.CssLength;
import org.w3c.css.values.CssNumber;

/**
 * Property which value can be of the several size-related types or one from the fixed set.
 * 
 * @author Denis Denisenko
 */
public abstract class SizeOrFixedCSSProperty extends TypeOrFixedCSSProperty
{

	private static ICSSValueTypeConverter[] CONVERTERS_ARRAY = new ICSSValueTypeConverter[] { new NumberToLengthConverter() };

	protected SizeOrFixedCSSProperty(String propertyName)
	{
		super(propertyName);
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param values
	 *            the list of fixed values
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @param check
	 *            whether to check property value
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected SizeOrFixedCSSProperty(String propertyName, String[] values, ApplContext context, CssExpression expression,
			boolean check) throws InvalidParamException
	{
		super(propertyName, new Class[] { CssLength.class, CssNumber.class }, CONVERTERS_ARRAY, values, context,
				expression, check);
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param values
	 *            the list of fixed values
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected SizeOrFixedCSSProperty(String propertyName, String[] values, ApplContext context, CssExpression expression)
			throws InvalidParamException
	{
		this(propertyName, values, context, expression, false);
	}
}
