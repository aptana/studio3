/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

/**
 * @author Max Stepanov
 * 
 */
public class StartPagePropertyTester extends PropertyTester {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.PropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IFile) {
			if ("isStartPage".equals(property)) { //$NON-NLS-1$
				boolean value = true;
				if (expectedValue != null && expectedValue instanceof Boolean) {
					value = ((Boolean) expectedValue).booleanValue();
				}
				return StartPageManager.getDefault().isStartPage((IResource) receiver) == value;
			}
		}
		return false;
	}

}
