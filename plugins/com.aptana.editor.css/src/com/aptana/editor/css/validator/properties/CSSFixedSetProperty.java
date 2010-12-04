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
	public CSSFixedSetProperty(String propertyName, String[] values)
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
	public CSSFixedSetProperty(String propertyName, String[] values, ApplContext context, CssExpression expression,
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
	public CSSFixedSetProperty(String propertyName, String[] values, ApplContext context, CssExpression expression)
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
