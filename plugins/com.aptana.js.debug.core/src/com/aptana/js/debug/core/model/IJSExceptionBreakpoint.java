/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;

/**
 * @author Max Stepanov
 */
public interface IJSExceptionBreakpoint extends IBreakpoint {

	/**
	 * Returns exception type name
	 * 
	 * @return String
	 * @throws CoreException
	 */
	String getExceptionTypeName() throws CoreException;
}
