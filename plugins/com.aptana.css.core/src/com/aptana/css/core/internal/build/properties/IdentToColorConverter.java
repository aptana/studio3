/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import org.w3c.css.util.ApplContext;
import org.w3c.css.values.CssColorCSS21;
import org.w3c.css.values.CssValue;

/**
 * Identifier -> Color converter.
 * 
 * @author Denis Denisenko
 */
public class IdentToColorConverter implements ICSSValueTypeConverter
{

	private ApplContext context;

	/**
	 * @param context
	 *            the current context
	 */
	public IdentToColorConverter(ApplContext context)
	{
		this.context = context;
	}

	public CssValue convert(CssValue in)
	{
		try
		{
			return new CssColorCSS21(context, (String) in.get());
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
