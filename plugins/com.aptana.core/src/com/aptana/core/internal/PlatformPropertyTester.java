/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Platform;

/**
 * @author Max Stepanov
 *
 */
public class PlatformPropertyTester extends PropertyTester {

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("os".equals(property)) {
			return Platform.getOS().equals(String.valueOf(expectedValue));
		} else if ("ws".equals(property)) {
			return Platform.getWS().equals(String.valueOf(expectedValue));
		} else if ("arch".equals(property)) {
			return Platform.getOSArch().equals(String.valueOf(expectedValue));				
		}
		return false;
	}

}
