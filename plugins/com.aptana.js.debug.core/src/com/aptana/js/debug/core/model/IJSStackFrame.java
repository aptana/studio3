/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author Max Stepanov
 */
public interface IJSStackFrame extends IStackFrame {

	/**
	 * Source location for this stack frame
	 * 
	 * @return String
	 */
	URI getSourceFileName();

	/**
	 * Find a variable by name in this stack frame
	 * 
	 * @param variableName
	 * @return IVariable
	 * @throws DebugException
	 */
	IVariable findVariable(String variableName) throws DebugException;
}
