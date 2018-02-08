/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model.provisional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IWatchpoint;

/**
 * @author Max Stepanov
 */
public interface IJSWatchpoint extends IWatchpoint {
	/**
	 * Returns the name of the variable associated with this watchpoint
	 * 
	 * @return field the name of the variable on which this watchpoint is installed
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's underlying marker
	 */
	public String getVariableName() throws CoreException;

}
