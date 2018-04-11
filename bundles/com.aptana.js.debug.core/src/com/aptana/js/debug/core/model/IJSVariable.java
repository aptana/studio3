/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author Max Stepanov
 */
public interface IJSVariable extends IVariable {

	/**
	 * Returns if this variable is a constant
	 * 
	 * @return boolean
	 * @throws DebugException
	 */
	boolean isConst() throws DebugException;

	/**
	 * Returns if this variable is a local variable
	 * 
	 * @return boolean
	 * @throws DebugException
	 */
	boolean isLocal() throws DebugException;

	/**
	 * Returns if this variable is a function argument
	 * 
	 * @return boolean
	 * @throws DebugException
	 */
	boolean isArgument() throws DebugException;

	/**
	 * Returns if this variable is an exception caught
	 * 
	 * @return boolean
	 * @throws DebugException
	 */
	boolean isException() throws DebugException;

	/**
	 * Returns if this variable is in a global scope
	 * 
	 * @return boolean
	 * @throws DebugException
	 */
	boolean isTopLevel() throws DebugException;

	/**
	 * Returns full variable name
	 * 
	 * @return
	 */
	String getFullName();
}
