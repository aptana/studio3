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
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.IDebugCoreConstants;
import com.aptana.debug.core.util.DebugUtil;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugLineBreakpoint extends LineBreakpoint implements IJSLineBreakpoint {

	/**
	 * Default constructor is required for the breakpoint manager to re-create persisted breakpoints. After
	 * instantiating a breakpoint, the <code>setMarker(...)</code> method is called to restore this breakpoint's
	 * attributes.
	 */
	public JSDebugLineBreakpoint() {
		super();
	}

	/**
	 * Constructs a line breakpoint on the given resource at the given line number. The line number is 1-based (i.e. the
	 * first line of a file is line number 1).
	 * 
	 * @param resource
	 *            file on which to set the breakpoint
	 * @param lineNumber
	 *            1-based line number of the breakpoint
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	public JSDebugLineBreakpoint(IResource resource, int lineNumber) throws CoreException {
		this(resource, lineNumber, new HashMap<String, Object>(), true, true);
	}

	/**
	 * Constructs a line breakpoint on the given resource at the given line number. The line number is 1-based (i.e. the
	 * first line of a file is line number 1).
	 * 
	 * @param resource
	 *            file on which to set the breakpoint
	 * @param lineNumber
	 *            1-based line number of the breakpoint
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	public JSDebugLineBreakpoint(IResource resource, int lineNumber, boolean isEnableBreakPoint) throws CoreException {
		this(resource, lineNumber, new HashMap<String, Object>(), true, isEnableBreakPoint);
	}
	
	/**
	 * JSDebugLineBreakpoint
	 * 
	 * @param resource
	 * @param lineNumber
	 * @throws CoreException
	 */
	public JSDebugLineBreakpoint(IUniformResource resource, int lineNumber) throws CoreException {
		this(resource, lineNumber, new HashMap<String, Object>(), true);
	}

	/**
	 * Constructs a line breakpoint on the given resource at the given line number. The line number is 1-based (i.e. the
	 * first line of a file is line number 1).
	 * 
	 * @param resource
	 *            file on which to set the breakpoint
	 * @param lineNumber
	 *            1-based line number of the breakpoint
	 * @param attributes
	 *            the marker attributes to set
	 * @param register
	 *            whether to add this breakpoint to the breakpoint manager
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	public JSDebugLineBreakpoint(final IResource resource, final int lineNumber, final Map<String, Object> attributes,
			final boolean register, final boolean isEnableBreakPoint) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(IDebugCoreConstants.ID_LINE_BREAKPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, isEnableBreakPoint);
				attributes.put(IMarker.LINE_NUMBER, Integer.valueOf(lineNumber));
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IMarker.MESSAGE, MessageFormat.format(Messages.JSDebugLineBreakpoint_JSBreakpoint_0_1,
						resource.getFullPath().toString(), Integer.toString(lineNumber)));
				ensureMarker().setAttributes(attributes);
				register(register);
			}
		};
		run(getMarkerRule(resource), wr);
	}

	/**
	 * JSDebugLineBreakpoint
	 * 
	 * @param resource
	 * @param lineNumber
	 * @param attributes
	 * @param register
	 * @throws CoreException
	 */
	public JSDebugLineBreakpoint(final IUniformResource resource, final int lineNumber,
			final Map<String, Object> attributes, final boolean register) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = MarkerUtils.createMarker(resource, attributes,
						IDebugCoreConstants.ID_LINE_BREAKPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, Boolean.TRUE);
				attributes.put(IMarker.LINE_NUMBER, Integer.valueOf(lineNumber));
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(
						IMarker.MESSAGE,
						MessageFormat.format(Messages.JSDebugLineBreakpoint_JSBreakpoint_0_1,
								DebugUtil.getPath(resource), Integer.toString(lineNumber)));
				ensureMarker().setAttributes(attributes);
				register(register);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wr, null, 0, new NullProgressMonitor());
		} catch (CoreException e) {
			JSDebugPlugin.log(Messages.JSDebugLineBreakpoint_BreakpointMarkerCreationFailed, e);
		}
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return IJSDebugConstants.ID_DEBUG_MODEL;
	}

	/*
	 * @return whether this breakpoint is a run to line breakpoint
	 * @throws CoreException
	 */
	public boolean isRunToLine() throws CoreException {
		return ensureMarker().getAttribute(IJSDebugConstants.RUN_TO_LINE, false);
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
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#getHitCount()
	 */
	public int getHitCount() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IJSDebugConstants.BREAKPOINT_HIT_COUNT, -1);
		}
		return -1;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#setHitCount(int)
	 */
	public void setHitCount(int count) throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			m.setAttribute(IJSDebugConstants.BREAKPOINT_HIT_COUNT, count);
		}
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#getCondition()
	 */
	public String getCondition() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION, StringUtil.EMPTY);
		}
		return StringUtil.EMPTY;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#setCondition(java.lang.String)
	 */
	public void setCondition(String condition) throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			m.setAttribute(IJSDebugConstants.BREAKPOINT_CONDITION, condition);
		}
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#isConditionEnabled()
	 */
	public boolean isConditionEnabled() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_ENABLED, false);
		}
		return false;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#setConditionEnabled(boolean)
	 */
	public void setConditionEnabled(boolean enabled) throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			m.setAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_ENABLED, enabled);
		}
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#isConditionSuspendOnTrue()
	 */
	public boolean isConditionSuspendOnTrue() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_SUSPEND_ON_TRUE, true);
		}
		return true;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSLineBreakpoint#setConditionSuspendOnTrue(boolean)
	 */
	public void setConditionSuspendOnTrue(boolean suspendOnTrue) throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			m.setAttribute(IJSDebugConstants.BREAKPOINT_CONDITION_SUSPEND_ON_TRUE, suspendOnTrue);
		}
	}
}
