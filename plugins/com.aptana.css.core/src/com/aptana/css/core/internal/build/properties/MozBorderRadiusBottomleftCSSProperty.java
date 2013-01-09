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
 * -moz-border-radius-bottomleft property implementation.
 * 
 * @author Denis Denisenko
 */
public class MozBorderRadiusBottomleftCSSProperty extends MozBorderRadiusSideProperty
{

	private static final String PROPERTY_NAME = "-moz-border-radius-bottomleft"; //$NON-NLS-1$

	public MozBorderRadiusBottomleftCSSProperty()
	{
		super(PROPERTY_NAME);
	}

	/**
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @param check
	 *            whether to check property value
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	public MozBorderRadiusBottomleftCSSProperty(ApplContext context, CssExpression expression, boolean check)
			throws InvalidParamException
	{
		super(PROPERTY_NAME, context, expression, check);
	}

	/**
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	public MozBorderRadiusBottomleftCSSProperty(ApplContext context, CssExpression expression)
			throws InvalidParamException
	{
		this(context, expression, false);
	}
}
