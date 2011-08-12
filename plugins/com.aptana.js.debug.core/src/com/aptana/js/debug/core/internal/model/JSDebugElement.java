/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.JSDebugPlugin;

/**
 * @author Max Stepanov
 */
public abstract class JSDebugElement extends DebugElement {

	/**
	 * Constructs a debug element referring to an artifact in the given debug target.
	 * 
	 * @param target
	 *            debug target containing this element
	 */
	public JSDebugElement(IDebugTarget target) {
		super(target);
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return IJSDebugConstants.ID_DEBUG_MODEL;
	}

	/**
	 * Throws a debug exception with the given message.
	 * 
	 * @param message
	 * @throws DebugException
	 */
	protected void throwDebugException(String message) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID,
				DebugException.TARGET_REQUEST_FAILED, message, null));
	}

	/**
	 * Throws a debug exception with the given message, error code, and underlying exception.
	 * 
	 * @param code
	 * @param message
	 * @param exception
	 * @throws DebugException
	 */
	protected void throwDebugException(int code, String message, Exception exception) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, code, message, exception));
	}

	/**
	 * Throws a debug exception with the given underlying exception.
	 * 
	 * @param exception
	 * @throws DebugException
	 */
	protected void throwDebugException(Exception exception) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID,
				DebugException.TARGET_REQUEST_FAILED, StringUtil.EMPTY, exception));
	}

	/**
	 * Throws an unimplemented debug exception. XXX: remove me later
	 * 
	 * @throws DebugException
	 */
	protected void throwNotImplemented() throws DebugException {
		throw new DebugException(
				new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK, "not implemented", null)); //$NON-NLS-1$
	}
}
