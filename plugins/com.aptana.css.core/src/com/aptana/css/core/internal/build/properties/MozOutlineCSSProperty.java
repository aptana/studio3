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
 * -moz-outline property implementation.
 * 
 * @author Denis Denisenko
 */
public class MozOutlineCSSProperty extends ColorOrFixedCSSProperty
{

	private static final String PROPERTY_NAME = "-moz-outline"; //$NON-NLS-1$
	private static final String[] VALUES = new String[] { "inherit", //$NON-NLS-1$
			"none", //$NON-NLS-1$
			"dotted", //$NON-NLS-1$
			"dashed", //$NON-NLS-1$
			"solid", //$NON-NLS-1$
			"double", //$NON-NLS-1$
			"groove", //$NON-NLS-1$
			"ridge", //$NON-NLS-1$
			"inset", //$NON-NLS-1$
			"outset", //$NON-NLS-1$
			"hidden", //$NON-NLS-1$
			"thin", //$NON-NLS-1$
			"medium", //$NON-NLS-1$
			"thick" //$NON-NLS-1$
	};

	public MozOutlineCSSProperty()
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
	public MozOutlineCSSProperty(ApplContext context, CssExpression expression, boolean check)
			throws InvalidParamException
	{
		super(PROPERTY_NAME, VALUES, context, expression, check);
	}

	/**
	 * @param context
	 *            the current context
	 * @param expression
	 *            the expression to create property from
	 * @throws InvalidParamException
	 *             if expression is invalid
	 */
	public MozOutlineCSSProperty(ApplContext context, CssExpression expression) throws InvalidParamException
	{
		this(context, expression, false);
	}
}
