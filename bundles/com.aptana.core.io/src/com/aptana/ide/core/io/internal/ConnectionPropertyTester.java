/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io.internal;

import org.eclipse.core.expressions.PropertyTester;

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 *
 */
public class ConnectionPropertyTester extends PropertyTester {

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IConnectionPoint) {
			if ( "isConnected".equals(property)) { //$NON-NLS-1$
				return ((IConnectionPoint) receiver).isConnected() == toBoolean(expectedValue);
			}
			if ( "canDisconnect".equals(property)) { //$NON-NLS-1$
				return ((IConnectionPoint) receiver).canDisconnect() == toBoolean(expectedValue);
			}
		}
		return false;
	}
	
	private static boolean toBoolean(Object value) {
		if ( value instanceof Boolean ) {
			 return ((Boolean)value).booleanValue();
		} else if (value instanceof String) {
			return Boolean.parseBoolean((String) value);
		}
		return false;
	}

}
