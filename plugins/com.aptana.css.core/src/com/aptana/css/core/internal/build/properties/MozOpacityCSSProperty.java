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
import org.w3c.css.values.CssNumber;
import org.w3c.css.values.CssValue;

/**
 * -moz-opacity property implementation.
 * 
 * @author Denis Denisenko
 */
public class MozOpacityCSSProperty extends PlugableCSSProperty
{

	private static final String PROPERTY_NAME = "-moz-opacity"; //$NON-NLS-1$

	/**
	 * Opacity validator.
	 * 
	 * @author Denis Denisenko
	 */
	private static class OpacityValidator implements ICSSValueValidator
	{

		public boolean canValidate(CssValue value)
		{
			return value instanceof CssNumber;
		}

		public boolean isValid(CssValue value)
		{
			if (!canValidate(value))
			{
				throw new IllegalArgumentException("Value " + value + " can not be validated"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			float fValue = ((CssNumber) value).getValue();
			return fValue >= 0f && fValue <= 1.0f;
		}
	}

	private static final OpacityValidator VALIDATOR = new OpacityValidator();

	public MozOpacityCSSProperty()
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
	public MozOpacityCSSProperty(ApplContext context, CssExpression expression, boolean check)
			throws InvalidParamException
	{
		super(PROPERTY_NAME, new Class[] { CssNumber.class }, null, new ICSSValueValidator[] { VALIDATOR }, context,
				expression, check);
	}

	/**
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	public MozOpacityCSSProperty(ApplContext context, CssExpression expression) throws InvalidParamException
	{
		this(context, expression, false);
	}
}
