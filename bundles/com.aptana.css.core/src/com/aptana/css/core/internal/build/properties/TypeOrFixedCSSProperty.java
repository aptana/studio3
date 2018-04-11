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

/**
 * CSS property that accepts values of some fixed types set and also predefined values list.
 * 
 * @author Denis Denisenko
 */
@SuppressWarnings("rawtypes")
public abstract class TypeOrFixedCSSProperty extends PlugableCSSProperty
{

	protected TypeOrFixedCSSProperty(String propertyName)
	{
		super(propertyName);
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param types
	 *            the types that this property accepts
	 * @param converters
	 *            the list of type converters
	 * @param values
	 *            the list of fixed values
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @param check
	 *            whether to check the property value
	 * @throws InvalidParamException
	 *             if the expression is invalid
	 */
	protected TypeOrFixedCSSProperty(String propertyName, Class[] types, ICSSValueTypeConverter[] converters,
			String[] values, ApplContext context, CssExpression expression, boolean check) throws InvalidParamException
	{
		super(propertyName, getPropertyTypes(types), converters,
				new ICSSValueValidator[] { new FixedSetIdentifierValidator(values) }, context, expression, check);
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param types
	 *            the types that this property accepts
	 * @param converters
	 *            the list of type converters
	 * @param values
	 *            the list of fixed values
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if the expression is invalid
	 */
	protected TypeOrFixedCSSProperty(String propertyName, Class[] types, ICSSValueTypeConverter[] converters,
			String[] values, ApplContext context, CssExpression expression) throws InvalidParamException
	{
		this(propertyName, types, converters, values, context, expression, false);
	}

	/**
	 * Adds Indent type to the list of needed.
	 * 
	 * @param originalTypes
	 *            the original list of types
	 * @return the new list of types
	 */
	private static Class[] getPropertyTypes(Class[] originalTypes)
	{
		for (Class type : originalTypes)
		{
			if (CssIdent.class.equals(type))
			{
				// already contained
				return originalTypes;
			}
		}

		Class[] result = new Class[originalTypes.length + 1];
		System.arraycopy(originalTypes, 0, result, 0, originalTypes.length);
		result[result.length - 1] = CssIdent.class;

		return result;
	}
}
