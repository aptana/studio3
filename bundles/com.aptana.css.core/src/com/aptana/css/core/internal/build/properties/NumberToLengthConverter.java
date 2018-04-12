/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssNumber;
import org.w3c.css.values.CssValue;

/**
 * Converts number to length.
 * 
 * @author Denis Denisenko
 */
public class NumberToLengthConverter implements ICSSValueTypeConverter
{

	public CssValue convert(CssValue in)
	{
		if (in instanceof CssNumber)
		{
			try
			{
				return ((CssNumber) in).getLength();
			}
			catch (InvalidParamException e)
			{
			}
		}
		return null;
	}
}
