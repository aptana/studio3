/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.net.URI;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.model.IJSImplicitBreakpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugImplicitBreakpoint extends PlatformObject implements IJSImplicitBreakpoint {

	enum Type {
		DEBUGGER_KEYWORD, FIRST_LINE, EXCEPTION, WATCHPOINT
	}

	private final URI fileName;
	private final int lineNumber;
	private final Type type;

	/**
	 * JSDebugImplicitBreakpoint
	 * 
	 * @param fileName
	 * @param lineNumber
	 * @param type
	 */
	public JSDebugImplicitBreakpoint(URI fileName, int lineNumber, Type type) {
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.type = type;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSImplicitBreakpoint#getFileName()
	 */
	public URI getFileName() throws CoreException {
		return fileName;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSImplicitBreakpoint#isDebuggerKeyword()
	 */
	public boolean isDebuggerKeyword() {
		return (type == Type.DEBUGGER_KEYWORD);
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSImplicitBreakpoint#isFirstLine()
	 */
	public boolean isFirstLine() {
		return (type == Type.FIRST_LINE);
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSImplicitBreakpoint#isException()
	 */
	public boolean isException() {
		return (type == Type.EXCEPTION);
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSImplicitBreakpoint#isWatchpoint()
	 */
	public boolean isWatchpoint() {
		return (type == Type.WATCHPOINT);
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return IJSDebugConstants.ID_DEBUG_MODEL;
	}

	/*
	 * @see org.eclipse.debug.core.model.ILineBreakpoint#getLineNumber()
	 */
	public int getLineNumber() throws CoreException {
		return lineNumber;
	}

	/*
	 * @see org.eclipse.debug.core.model.ILineBreakpoint#getCharStart()
	 */
	public int getCharStart() throws CoreException {
		return -1;
	}

	/*
	 * @see org.eclipse.debug.core.model.ILineBreakpoint#getCharEnd()
	 */
	public int getCharEnd() throws CoreException {
		return -1;
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#setMarker(org.eclipse.core.resources.IMarker)
	 */
	public void setMarker(IMarker marker) throws CoreException {
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object item) {
		if (item instanceof JSDebugImplicitBreakpoint) {
			return fileName.equals(((JSDebugImplicitBreakpoint) item).fileName)
					&& lineNumber == ((JSDebugImplicitBreakpoint) item).lineNumber;
		}
		return false;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (fileName + "\n" + lineNumber).hashCode(); //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#isEnabled()
	 */
	public boolean isEnabled() throws CoreException {
		return true;
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#isPersisted()
	 */
	public boolean isPersisted() throws CoreException {
		return false;
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#isRegistered()
	 */
	public boolean isRegistered() throws CoreException {
		return false;
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) throws CoreException {
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#setPersisted(boolean)
	 */
	public void setPersisted(boolean persisted) throws CoreException {
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#setRegistered(boolean)
	 */
	public void setRegistered(boolean registered) throws CoreException {
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#delete()
	 */
	public void delete() throws CoreException {
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#getMarker()
	 */
	public IMarker getMarker() {
		return null;
	}
}
