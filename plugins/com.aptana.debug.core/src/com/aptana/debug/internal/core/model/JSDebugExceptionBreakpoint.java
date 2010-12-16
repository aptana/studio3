/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.internal.core.model;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.IDebugConstants;
import com.aptana.debug.core.JSDebugPlugin;
import com.aptana.debug.core.util.DebugUtil;
import com.aptana.js.debug.core.model.IJSExceptionBreakpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugExceptionBreakpoint extends Breakpoint implements IJSExceptionBreakpoint {
	/**
	 * Default constructor is required for the breakpoint manager to re-create
	 * persisted breakpoints. After instantiating a breakpoint, the
	 * <code>setMarker(...)</code> method is called to restore this breakpoint's
	 * attributes.
	 */
	public JSDebugExceptionBreakpoint() {
		super();
	}

	/**
	 * Constructs a exception breakpoint on the given resource for the given
	 * exception type name.
	 * 
	 * @param resource
	 *            file on which to set the breakpoint
	 * @param exceptionTypeName
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	@SuppressWarnings("rawtypes")
	public JSDebugExceptionBreakpoint(IResource resource, String exceptionTypeName) throws CoreException {
		this(resource, exceptionTypeName, new HashMap(), true);
	}

	/**
	 * JSDebugExceptionBreakpoint
	 * 
	 * @param resource
	 * @param exceptionTypeName
	 * @throws CoreException
	 */
	@SuppressWarnings("rawtypes")
	public JSDebugExceptionBreakpoint(IUniformResource resource, String exceptionTypeName) throws CoreException {
		this(resource, exceptionTypeName, new HashMap(), true);
	}

	/**
	 * Constructs a exception breakpoint on the given resource for the given
	 * exception type name.
	 * 
	 * @param resource
	 *            file on which to set the breakpoint
	 * @param exceptionTypeName
	 * @param attributes
	 *            the marker attributes to set
	 * @param register
	 *            whether to add this breakpoint to the breakpoint manager
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	@SuppressWarnings("rawtypes")
	public JSDebugExceptionBreakpoint(final IResource resource, final String exceptionTypeName, final Map attributes,
			final boolean register) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			@SuppressWarnings("unchecked")
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(IDebugConstants.ID_EXCEPTION_BREAKPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, Boolean.TRUE);
				attributes.put(IDebugConstants.EXCEPTION_TYPE_NAME, exceptionTypeName);
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IMarker.MESSAGE, MessageFormat.format(
						Messages.JSDebugExceptionBreakpoint_JSExceptionBreakpoint_0_1,
								resource.getFullPath().lastSegment(), exceptionTypeName));
				ensureMarker().setAttributes(attributes);

				register(register);
			}
		};
		run(getMarkerRule(resource), wr);
	}

	/**
	 * JSDebugExceptionBreakpoint
	 * 
	 * @param resource
	 * @param exceptionTypeName
	 * @param attributes
	 * @param register
	 * @throws CoreException
	 */
	@SuppressWarnings("rawtypes")
	public JSDebugExceptionBreakpoint(final IUniformResource resource, final String exceptionTypeName,
			final Map attributes, final boolean register) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			@SuppressWarnings("unchecked")
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = MarkerUtils.createMarker(resource, attributes,
						IDebugConstants.ID_EXCEPTION_BREAKPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, Boolean.TRUE);
				attributes.put(IDebugConstants.EXCEPTION_TYPE_NAME, exceptionTypeName);
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IMarker.MESSAGE, MessageFormat.format(
						Messages.JSDebugExceptionBreakpoint_JSExceptionBreakpoint_0_1,
								DebugUtil.getPath(resource), exceptionTypeName));
				ensureMarker().setAttributes(attributes);

				register(register);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wr, null, 0, new NullProgressMonitor());
		} catch (CoreException e) {
			JSDebugPlugin.log(Messages.JSDebugExceptionBreakpoint_BreakpointMarkerCreationFailed, e);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return IDebugConstants.ID_DEBUG_MODEL;
	}

	/**
	 * Add this breakpoint to the breakpoint manager, or sets it as
	 * unregistered.
	 */
	private void register(boolean register) throws CoreException {
		if (register) {
			org.eclipse.debug.core.DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(this);
		} else {
			setRegistered(false);
		}
	}

	/**
	 * @see com.aptana.js.debug.core.model.IJSExceptionBreakpoint#getExceptionTypeName()
	 */
	public String getExceptionTypeName() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IDebugConstants.EXCEPTION_TYPE_NAME, StringUtil.EMPTY);
		}
		return StringUtil.EMPTY;
	}
}
