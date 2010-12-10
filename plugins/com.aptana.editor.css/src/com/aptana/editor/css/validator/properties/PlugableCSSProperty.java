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
