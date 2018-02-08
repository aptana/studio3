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
 * CSS value validator.
 * 
 * @author Denis Denisenko
 */
public interface ICSSValueValidator
{

	/**
	 * Checks if the current validator is able to validate the value specified.
	 * 
	 * @param value
	 *            the value to check
	 * @return true if it is able to validator, false otherwise
	 */
	boolean canValidate(CssValue value);

	/**
	 * Checks if the value is valid.
	 * 
	 * @param value
	 *            the value to check
	 * @return true if it is valid, false otherwise.
	 */
	boolean isValid(CssValue value);
}
