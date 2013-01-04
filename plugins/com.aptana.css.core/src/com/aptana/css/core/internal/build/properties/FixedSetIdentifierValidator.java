/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build.properties;

import java.util.HashSet;
import java.util.Set;

import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssValue;

/**
 * Validator that checks if identifier has value which belongs to the fixed set.
 * 
 * @author Denis Denisenko
 */
public class FixedSetIdentifierValidator implements ICSSValueValidator
{

	private final Set<String> values = new HashSet<String>();

	public FixedSetIdentifierValidator(String[] values)
	{
		for (String value : values)
		{
			this.values.add(value);
		}
	}

	public boolean canValidate(CssValue value)
	{
		return value instanceof CssIdent;
	}

	public boolean isValid(CssValue value)
	{
		if (!canValidate(value))
		{
			throw new IllegalArgumentException(value + " cannot be validated"); //$NON-NLS-1$
		}
		return values.contains(value.get());
	}
}
