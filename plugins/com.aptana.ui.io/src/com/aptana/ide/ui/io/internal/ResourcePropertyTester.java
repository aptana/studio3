/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ResourcePropertyTester extends PropertyTester
{

	private static final String PROPERTY_IS_ACCESSIBLE = "isAccessible"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IResource)
		{
			IResource resource = (IResource) receiver;

			boolean value = toBoolean(expectedValue);
			if (PROPERTY_IS_ACCESSIBLE.equals(property))
			{
				return resource.isAccessible() == value;
			}
		}
		return false;
	}

	private static boolean toBoolean(Object value)
	{
		if (value instanceof Boolean)
		{
			return ((Boolean) value).booleanValue();
		}
		if (value instanceof String)
		{
			return Boolean.parseBoolean((String) value);
		}
		return false;
	}
}
