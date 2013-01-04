/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import org.w3c.css.values.CssValue;

/**
 * CSS value type converter.
 * 
 * @author Denis Denisenko
 */
public interface ICSSValueTypeConverter
{
	/**
	 * Converts CSS value if possible.
	 * 
	 * @param in
	 *            the input value to convert
	 * @return converted value, or null if conversion is not possible
	 */
	public CssValue convert(CssValue in);
}
