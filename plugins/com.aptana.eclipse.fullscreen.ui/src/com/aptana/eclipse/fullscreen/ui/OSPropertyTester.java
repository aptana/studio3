/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.eclipse.fullscreen.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Platform;

public class OSPropertyTester extends PropertyTester
{

	private static final String IS_OS_LION = "isOSLion"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (IS_OS_LION.equals(property))
		{
			boolean isLion = Platform.OS_MACOSX.equals(Platform.getOS())
					&& ToggleFullScreenHandler.getOSVersion() >= 0x1070;
			return isLion == (expectedValue == null) ? true : Boolean.parseBoolean(expectedValue.toString());
		}
		return false;
	}
}
