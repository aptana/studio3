/** 
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.validator.properties;

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

	public MozBorderRadiusSideProperty(String propertyName)
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
	public MozBorderRadiusSideProperty(String propertyName, ApplContext context, CssExpression expression, boolean check)
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
	public MozBorderRadiusSideProperty(String propertyName, ApplContext context, CssExpression expression)
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
