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

/**
 * Property that has the only value of the type, which should be specified by descendants.
 * 
 * @author Denis Denisenko
 */
@SuppressWarnings("rawtypes")
public abstract class SingleTypeCSSProperty extends PlugableCSSProperty
{

	protected SingleTypeCSSProperty(String property)
	{
		super(property);
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param type
	 *            the property type
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @param check
	 *            whether to check property value
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected SingleTypeCSSProperty(String propertyName, Class type, ApplContext context, CssExpression expression,
			boolean check) throws InvalidParamException
	{
		super(propertyName, new Class[] { type }, null, null, context, expression, check);
	}

	/**
	 * @param propertyName
	 *            the property name
	 * @param type
	 *            the property type
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	protected SingleTypeCSSProperty(String propertyName, Class type, ApplContext context, CssExpression expression)
			throws InvalidParamException
	{
		this(propertyName, type, context, expression, false);
	}
}
