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
package com.aptana.debug.core.model;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IVariable;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.debug.core.IDebugConstants;
import com.aptana.debug.core.JSDebugPlugin;
import com.aptana.debug.internal.core.model.JSDebugExceptionBreakpoint;
import com.aptana.debug.internal.core.model.JSDebugLineBreakpoint;
import com.aptana.debug.internal.core.model.JSDebugSourceLink;
import com.aptana.debug.internal.core.model.JSDebugWatchpoint;

/**
 * Provides utility methods for creating debug targets and breakpoints specific
 * to the JS debug model.
 * 
 * @author Max Stepanov
 * 
 */
public final class JSDebugModel {

	/**
	 * Not to be instantiated.
	 */
	private JSDebugModel() {
	}

	/**
	 * getModelIdentifier
	 * 
	 * @return String
	 */
	public static String getModelIdentifier() {
		return IDebugConstants.ID_DEBUG_MODEL;
	}

	/**
	 * createLineBreakpoint
	 * 
	 * @param resource
	 * @param line
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	public static ILineBreakpoint createLineBreakpoint(IResource resource, int line) throws CoreException {
		return new JSDebugLineBreakpoint(resource, line);
	}

	/**
	 * createLineBreakpoint
	 * 
	 * @param resource
	 * @param line
	 * @param attributes
	 * @param register
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	@SuppressWarnings("rawtypes")
	public static ILineBreakpoint createLineBreakpoint(IResource resource, int line, Map attributes, boolean register)
			throws CoreException {
		return new JSDebugLineBreakpoint(resource, line, attributes, register);
	}

	/**
	 * createLineBreakpoint
	 * 
	 * @param resource
	 * @param line
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	public static ILineBreakpoint createLineBreakpoint(IUniformResource resource, int line) throws CoreException {
		return new JSDebugLineBreakpoint(resource, line);
	}

	/**
	 * createLineBreakpoint
	 * 
	 * @param resource
	 * @param line
	 * @param attributes
	 * @param register
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	@SuppressWarnings("rawtypes")
	public static ILineBreakpoint createLineBreakpoint(IUniformResource resource, int line, Map attributes,
			boolean register) throws CoreException {
		return new JSDebugLineBreakpoint(resource, line, attributes, register);
	}

	/**
	 * createLineBreakpointForResource
	 * 
	 * @param resource
	 * @param line
	 * @param attributes
	 * @param register
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ILineBreakpoint createLineBreakpointForResource(Object resource, int line, Map attributes,
			boolean register) throws CoreException {
		URI location = null;
		if (resource instanceof IResource) {
			location = ((IResource) resource).getLocation().makeAbsolute().toFile().toURI();
		} else if (resource instanceof IUniformResource) {
			location = ((IUniformResource) resource).getURI();
		}
		if (location == null) {
			return null;
		}
		attributes.put(IDebugConstants.BREAKPOINT_LOCATION, location.toString());
		return new JSDebugLineBreakpoint(ResourcesPlugin.getWorkspace().getRoot(), line, attributes, register);
	}

	/**
	 * lineBreakpointExists
	 * 
	 * @param resource
	 * @param lineNumber
	 * @return ILineBreakpoint
	 */
	public static ILineBreakpoint lineBreakpointExists(IResource resource, int lineNumber) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; ++i) {
			IBreakpoint breakpoint = breakpoints[i];
			try {
				if (breakpoint instanceof ILineBreakpoint && resource.equals(breakpoint.getMarker().getResource())
						&& ((ILineBreakpoint) breakpoint).getLineNumber() == lineNumber) {
					return (ILineBreakpoint) breakpoint;
				}
			} catch (CoreException e) {
				JSDebugPlugin.log(e);
			}
		}
		return null;
	}

	/**
	 * lineBreakpointExists
	 * 
	 * @param resource
	 * @param lineNumber
	 * @return ILineBreakpoint
	 */
	public static ILineBreakpoint lineBreakpointExists(IUniformResource resource, int lineNumber) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; ++i) {
			IBreakpoint breakpoint = breakpoints[i];
			try {
				IMarker marker = breakpoint.getMarker();
				if (breakpoint instanceof ILineBreakpoint && marker instanceof IUniformResourceMarker
						&& resource.equals(((IUniformResourceMarker) marker).getUniformResource())
						&& ((ILineBreakpoint) breakpoint).getLineNumber() == lineNumber) {
					return (ILineBreakpoint) breakpoint;
				}
			} catch (CoreException e) {
				JSDebugPlugin.log(e);
			}
		}
		return null;
	}

	/**
	 * createExceptionBreakpoint
	 * 
	 * @param resource
	 * @param exceptionTypeName
	 * @return IJSExceptionBreakpoint
	 * @throws CoreException
	 */
	public static IJSExceptionBreakpoint createExceptionBreakpoint(IResource resource, String exceptionTypeName)
			throws CoreException {
		return new JSDebugExceptionBreakpoint(resource, exceptionTypeName);
	}

	/**
	 * createExceptionBreakpoint
	 * 
	 * @param resource
	 * @param exceptionTypeName
	 * @return IJSExceptionBreakpoint
	 * @throws CoreException
	 */
	public static IJSExceptionBreakpoint createExceptionBreakpoint(IUniformResource resource, String exceptionTypeName)
			throws CoreException {
		return new JSDebugExceptionBreakpoint(resource, exceptionTypeName);
	}

	/**
	 * exceptionBreakpointExists
	 * 
	 * @param exceptionTypeName
	 * @return IJSExceptionBreakpoint
	 */
	public static IJSExceptionBreakpoint exceptionBreakpointExists(String exceptionTypeName) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; ++i) {
			IBreakpoint breakpoint = breakpoints[i];
			try {
				if (breakpoint instanceof IJSExceptionBreakpoint
						&& exceptionTypeName.equals(((IJSExceptionBreakpoint) breakpoint).getExceptionTypeName())) {
					return (IJSExceptionBreakpoint) breakpoint;
				}
			} catch (CoreException e) {
				JSDebugPlugin.log(e);
			}
		}
		return null;
	}

	/**
	 * createWatchpoint
	 * 
	 * @param variable
	 * @return IJSWatchpoint
	 * @throws CoreException
	 */
	public static IJSWatchpoint createWatchpoint(IJSVariable variable) throws CoreException {
		return new JSDebugWatchpoint(variable);
	}

	public static boolean watchpointExists(IVariable variable) {
		return false;
	}

	public static ISourceLink createSourceLink(String location) {
		return new JSDebugSourceLink(location);
	}

}
