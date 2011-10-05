/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

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
import com.aptana.debug.core.IDebugCoreConstants;
import com.aptana.debug.core.util.DebugUtil;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.model.IJSExceptionBreakpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugExceptionBreakpoint extends Breakpoint implements IJSExceptionBreakpoint {

	/**
	 * Default constructor is required for the breakpoint manager to re-create persisted breakpoints. After
	 * instantiating a breakpoint, the <code>setMarker(...)</code> method is called to restore this breakpoint's
	 * attributes.
	 */
	public JSDebugExceptionBreakpoint() {
		super();
	}

	/**
	 * Constructs a exception breakpoint on the given resource for the given exception type name.
	 * 
	 * @param resource
	 *            file on which to set the breakpoint
	 * @param exceptionTypeName
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	public JSDebugExceptionBreakpoint(IResource resource, String exceptionTypeName) throws CoreException {
		this(resource, exceptionTypeName, new HashMap<String, Object>(), true);
	}

	/**
	 * JSDebugExceptionBreakpoint
	 * 
	 * @param resource
	 * @param exceptionTypeName
	 * @throws CoreException
	 */
	public JSDebugExceptionBreakpoint(IUniformResource resource, String exceptionTypeName) throws CoreException {
		this(resource, exceptionTypeName, new HashMap<String, Object>(), true);
	}

	/**
	 * Constructs a exception breakpoint on the given resource for the given exception type name.
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
	public JSDebugExceptionBreakpoint(final IResource resource, final String exceptionTypeName,
			final Map<String, Object> attributes, final boolean register) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(IDebugCoreConstants.ID_EXCEPTION_BREAKPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, Boolean.TRUE);
				attributes.put(IJSDebugConstants.EXCEPTION_TYPE_NAME, exceptionTypeName);
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IMarker.MESSAGE, MessageFormat.format(
						Messages.JSDebugExceptionBreakpoint_JSExceptionBreakpoint_0_1, resource.getFullPath()
								.lastSegment(), exceptionTypeName));
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
	public JSDebugExceptionBreakpoint(final IUniformResource resource, final String exceptionTypeName,
			final Map<String, Object> attributes, final boolean register) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = MarkerUtils.createMarker(resource, attributes,
						IDebugCoreConstants.ID_EXCEPTION_BREAKPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, Boolean.TRUE);
				attributes.put(IJSDebugConstants.EXCEPTION_TYPE_NAME, exceptionTypeName);
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(
						IMarker.MESSAGE,
						MessageFormat.format(Messages.JSDebugExceptionBreakpoint_JSExceptionBreakpoint_0_1,
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

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return IJSDebugConstants.ID_DEBUG_MODEL;
	}

	/*
	 * Add this breakpoint to the breakpoint manager, or sets it as unregistered.
	 */
	private void register(boolean register) throws CoreException {
		if (register) {
			org.eclipse.debug.core.DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(this);
		} else {
			setRegistered(false);
		}
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSExceptionBreakpoint#getExceptionTypeName()
	 */
	public String getExceptionTypeName() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IJSDebugConstants.EXCEPTION_TYPE_NAME, StringUtil.EMPTY);
		}
		return StringUtil.EMPTY;
	}
}
