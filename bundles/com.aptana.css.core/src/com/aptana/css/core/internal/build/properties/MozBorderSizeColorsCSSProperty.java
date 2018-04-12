/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import java.util.ArrayList;
import java.util.List;

import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssColor;
import org.w3c.css.values.CssColorCSS21;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssValue;

/**
 * Superclass for Mozilla border color properties.
 * 
 * @author Denis Denisenko
 */
public abstract class MozBorderSizeColorsCSSProperty extends CustomCSSProperty
{

	private static final String TRANSPARENT = "transparent"; //$NON-NLS-1$

	private final List<CssValue> values;

	protected MozBorderSizeColorsCSSProperty(String propertyName)
	{
		super(propertyName);
		values = null;
	}

	/**
	 * @param property
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
	protected MozBorderSizeColorsCSSProperty(String propertyName, ApplContext context, CssExpression expression,
			boolean check) throws InvalidParamException
	{
		super(propertyName);

		setByUser();

		values = new ArrayList<CssValue>();
		CssValue val = expression.getValue();
		while (val != null)
		{
			if (val instanceof CssIdent)
			{
				if (TRANSPARENT.equals(((CssIdent) val).get()))
				{
					values.add(val);
				}
				else
				{
					values.add(getColorByIdentifier(context, (CssIdent) val));
				}
			}
			else if (val instanceof CssColor)
			{
				values.add(val);
			}
			else
			{
				throw new InvalidParamException("value", val.toString(), getPropertyNameNoMinus(), context); //$NON-NLS-1$
			}

			expression.next();
			val = expression.getValue();
		}
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
	protected MozBorderSizeColorsCSSProperty(String propertyName, ApplContext context, CssExpression expression)
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
	 * Gets color by identifier.
	 * 
	 * @param context
	 *            the current context
	 * @param colorIdentifier
	 *            the color identifier
	 * @return the color
	 * @throws InvalidParamException
	 *             if color identifier is invalid
	 */
	private static CssColor getColorByIdentifier(ApplContext context, CssIdent colorIdentifier)
			throws InvalidParamException
	{
		return new CssColorCSS21(context, (String) colorIdentifier.get());
	}
}
