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
 * White space CSS3 property.
 * 
 * @author Denis Denisenko
 */
public class CssWhiteSpaceCSS3 extends CSSFixedSetProperty
{

	private static final String PROPERTY_NAME = "white-space"; //$NON-NLS-1$
	private static final String[] VALUES = { "normal", "pre", "nowrap", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"pre-wrap", "pre-line", "inherit" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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
	public CssWhiteSpaceCSS3(ApplContext context, CssExpression expression, boolean check) throws InvalidParamException
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
	public CssWhiteSpaceCSS3(ApplContext context, CssExpression expression) throws InvalidParamException
	{
		this(context, expression, false);
	}

	/**
	 * MozUserSelectCSSProperty constructor.
	 */
	public CssWhiteSpaceCSS3()
	{
		super(PROPERTY_NAME, VALUES);
	}
}
