/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.ILineBreakpoint;

/**
 * @author Max Stepanov
 */
public interface IJSImplicitBreakpoint extends ILineBreakpoint {

	/**
	 * Returns breakpoint location filename
	 * 
	 * @return String
	 * @throws CoreException
	 */
	URI getFileName() throws CoreException;

	/**
	 * Returns if this breakpoint is a debugger keyword hard-coded breakpoint
	 * 
	 * @return boolean
	 */
	boolean isDebuggerKeyword();

	/**
	 * Returns if this breakpoint is a first-line stop breakpoint
	 * 
	 * @return boolean
	 */
	boolean isFirstLine();

	/**
	 * Returns if this breakpoint is a exception stop breakpoint
	 * 
	 * @return boolean
	 */
	boolean isException();

	/**
	 * Returns if this breakpoint is a watchpoint stop breakpoint
	 * 
	 * @return boolean
	 */
	boolean isWatchpoint();

}
