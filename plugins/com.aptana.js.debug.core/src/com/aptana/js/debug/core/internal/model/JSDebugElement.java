/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
	 * Constructs a debug element referring to an artifact in the given debug
	 * target.
	 * 
	 * @param target debug target containing this element
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
		throw new DebugException(
				new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED, message, null));
	}

	/**
	 * Throws a debug exception with the given message, error code, and
	 * underlying exception.
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
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED,
				StringUtil.EMPTY, exception));
	}

	/**
	 * Throws an unimplemented debug exception.
	 * 
	 * XXX: remove me later
	 * 
	 * @throws DebugException
	 */
	protected void throwNotImplemented() throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK, "not implemented", null)); //$NON-NLS-1$
	}
}
