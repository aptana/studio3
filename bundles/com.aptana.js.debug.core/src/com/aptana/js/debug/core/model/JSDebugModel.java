/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

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
import org.eclipse.debug.core.model.IWatchExpressionResult;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.internal.model.JSDebugExceptionBreakpoint;
import com.aptana.js.debug.core.internal.model.JSDebugLineBreakpoint;
import com.aptana.js.debug.core.internal.model.JSDebugSourceLink;
import com.aptana.js.debug.core.internal.model.JSDebugWatchpoint;
import com.aptana.js.debug.core.internal.model.JSInspectExpression;
import com.aptana.js.debug.core.model.IJSExceptionBreakpoint;
import com.aptana.js.debug.core.model.IJSVariable;
import com.aptana.js.debug.core.model.ISourceLink;
import com.aptana.js.debug.core.model.provisional.IJSWatchpoint;

/**
 * Provides utility methods for creating debug targets and breakpoints specific to the JS debug model.
 * 
 * @author Max Stepanov
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
		return IJSDebugConstants.ID_DEBUG_MODEL;
	}

	/**
	 * Create line breakpoint
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
	 * Create line breakpoint
	 * 
	 * @param resource
	 * @param line
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	public static ILineBreakpoint createLineBreakpoint(IResource resource, int line, boolean isEnableBreakPoint) throws CoreException {
		return new JSDebugLineBreakpoint(resource, line, isEnableBreakPoint);
	}
	
	/**
	 * Create line breakpoint
	 * 
	 * @param resource
	 * @param line
	 * @param attributes
	 * @param register
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	public static ILineBreakpoint createLineBreakpoint(IResource resource, int line, Map<String, Object> attributes,
			boolean register) throws CoreException {
		return new JSDebugLineBreakpoint(resource, line, attributes, register, true);
	}

	/**
	 * Create line breakpoint
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
	 * Create line breakpoint
	 * 
	 * @param resource
	 * @param line
	 * @param attributes
	 * @param register
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	public static ILineBreakpoint createLineBreakpoint(IUniformResource resource, int line,
			Map<String, Object> attributes, boolean register) throws CoreException {
		return new JSDebugLineBreakpoint(resource, line, attributes, register);
	}

	/**
	 * Create line breakpoint
	 * 
	 * @param resource
	 * @param line
	 * @param attributes
	 * @param register
	 * @return ILineBreakpoint
	 * @throws CoreException
	 */
	public static ILineBreakpoint createLineBreakpointForResource(Object resource, int line,
			Map<String, Object> attributes, boolean register) throws CoreException {
		if (resource instanceof IResource) {
			return createLineBreakpoint((IResource) resource, line, attributes, register);
		} else if (resource instanceof IUniformResource) {
			return createLineBreakpoint((IUniformResource) resource, line, attributes, register);
		} else if (resource instanceof String) {
			attributes.put(IJSDebugConstants.BREAKPOINT_LOCATION, (String) resource);
			return new JSDebugLineBreakpoint(ResourcesPlugin.getWorkspace().getRoot(), line, attributes, register, true);
		}
		return null;
	}

	/**
	 * Finds an existing line breakpoint at the specified location
	 * 
	 * @param resource
	 * @param lineNumber
	 * @return ILineBreakpoint
	 */
	public static ILineBreakpoint lineBreakpointExists(IResource resource, int lineNumber) {
		for (IBreakpoint breakpoint : DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier())) {
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
	 * Finds an existing line breakpoint at the specified location
	 * 
	 * @param resource
	 * @param lineNumber
	 * @return ILineBreakpoint
	 */
	public static ILineBreakpoint lineBreakpointExists(IUniformResource resource, int lineNumber) {
		for (IBreakpoint breakpoint : DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier())) {
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
	 * Create exception breakpoint
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
	 * Create exception breakpoint
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
	 * Finds an existing exception breakpoint by exception type name
	 * 
	 * @param exceptionTypeName
	 * @return IJSExceptionBreakpoint
	 */
	public static IJSExceptionBreakpoint exceptionBreakpointExists(String exceptionTypeName) {
		for (IBreakpoint breakpoint : DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier())) {
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

	public static IJSInspectExpression createInspectExpression(IWatchExpressionResult expressionResult) {
		return new JSInspectExpression(expressionResult);
	}

	/**
	 * Create variable watchpoint
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

	/**
	 * Create source link for location
	 * 
	 * @param location
	 * @return
	 */
	public static ISourceLink createSourceLink(URI location) {
		return new JSDebugSourceLink(location);
	}

}
