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
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.IDebugCoreConstants;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.model.IJSVariable;
import com.aptana.js.debug.core.model.provisional.IJSWatchpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugWatchpoint extends Breakpoint implements IJSWatchpoint {

	/**
	 * Watchpoint attribute storing the access value (value <code>"com.aptana.debug.core.watchpoint.access"</code>).
	 * This attribute is stored as a <code>boolean</code>, indicating whether a watchpoint is an access watchpoint.
	 */
	protected static final String ACCESS = "com.aptana.debug.core.watchpoint.access"; //$NON-NLS-1$

	/**
	 * Watchpoint attribute storing the modification value (value <code>"com.aptana.debug.core.watchpoint.access"</code>
	 * ). This attribute is stored as a <code>boolean</code>, indicating whether a watchpoint is a modification
	 * watchpoint.
	 */
	protected static final String MODIFICATION = "com.aptana.debug.core.watchpoint.modification"; //$NON-NLS-1$	

	/**
	 * Watchpoint attribute storing the auto_disabled value (value
	 * <code>"com.aptana.debug.core.watchpoint.auto_disabled"</code>). This attribute is stored as a
	 * <code>boolean</code>, indicating whether a watchpoint has been auto-disabled (as opposed to being disabled
	 * explicitly by the user)
	 */
	protected static final String AUTO_DISABLED = "com.aptana.debug.core.watchpoint.auto_disabled"; //$NON-NLS-1$

	/**
	 * Default constructor is required for the breakpoint manager to re-create persisted breakpoints. After
	 * instantiating a breakpoint, the <code>setMarker(...)</code> method is called to restore this breakpoint's
	 * attributes.
	 */
	public JSDebugWatchpoint() {
		super();
	}

	/**
	 * Constructs a watchpoint for the given variable.
	 * 
	 * @param variable
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	public JSDebugWatchpoint(IJSVariable variable) throws CoreException {
		this(ResourcesPlugin.getWorkspace().getRoot(), variable, new HashMap<String, Object>(), true);
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
	public JSDebugWatchpoint(final IResource resource, final IJSVariable variable,
			final Map<String, Object> attributes, final boolean register) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(IDebugCoreConstants.ID_WATCHPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, Boolean.TRUE);
				attributes.put(IJSDebugConstants.WATCHPOINT_VARIABLE_ACCESSOR,
						((JSDebugVariable) variable).getQualifier());
				attributes.put(IJSDebugConstants.WATCHPOINT_VARIABLE_NAME, variable.getFullName());
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IMarker.MESSAGE,
						MessageFormat.format(Messages.JSDebugWatchpoint_JS_Watchpoint, variable.getFullName()));
				addDefaultAccessAndModification(attributes);
				ensureMarker().setAttributes(attributes);

				register(register);
			}
		};
		run(getMarkerRule(resource), wr);
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
	 * @see com.aptana.js.debug.core.model.provisional.IJSWatchpoint#getVariableName()
	 */
	public String getVariableName() throws CoreException {
		return ensureMarker().getAttribute(IJSDebugConstants.WATCHPOINT_VARIABLE_NAME, StringUtil.EMPTY);
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchpoint#isAccess()
	 */
	public boolean isAccess() throws CoreException {
		return ensureMarker().getAttribute(ACCESS, false);
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchpoint#isModification()
	 */
	public boolean isModification() throws CoreException {
		return ensureMarker().getAttribute(MODIFICATION, false);
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchpoint#setAccess(boolean)
	 */
	public void setAccess(boolean access) throws CoreException {
		if (access == isAccess()) {
			return;
		}
		setAttribute(ACCESS, access);
		if (access && !isEnabled()) {
			setEnabled(true);
		} else if (!(access || isModification())) {
			setEnabled(false);
		}
		// recreate();
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchpoint#setModification(boolean)
	 */
	public void setModification(boolean modification) throws CoreException {
		if (modification == isModification()) {
			return;
		}
		setAttribute(MODIFICATION, modification);
		if (modification && !isEnabled()) {
			setEnabled(true);
		} else if (!(modification || isAccess())) {
			setEnabled(false);
		}
		// recreate();
	}

	/*
	 * @see org.eclipse.debug.core.model.Breakpoint#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) throws CoreException {
		if (enabled) {
			if (!(isAccess() || isModification())) {
				setDefaultAccessAndModification();
			}
		}
		super.setEnabled(enabled);
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchpoint#supportsAccess()
	 */
	public boolean supportsAccess() {
		return true;
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchpoint#supportsModification()
	 */
	public boolean supportsModification() {
		return true;
	}

	/*
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return IJSDebugConstants.ID_DEBUG_MODEL;
	}

	/*
	 * Adds the default access and modification attributes of the watchpoint to the given map <ul> <li>access = false
	 * <li>modification = true <li>auto disabled = false <ul>
	 */
	private void addDefaultAccessAndModification(Map<String, Object> attributes) {
		boolean[] values = new boolean[] { false, true }; // getDefaultAccessAndModificationValues();
		attributes.put(ACCESS, (values[0] ? Boolean.TRUE : Boolean.FALSE));
		attributes.put(MODIFICATION, (values[1] ? Boolean.TRUE : Boolean.FALSE));
		attributes.put(AUTO_DISABLED, Boolean.FALSE);
	}

	/*
	 * Sets the default access and modification attributes of the watchpoint. The default values are: <ul> <li>access =
	 * <code>false</code> <li>modification = <code>true</code> <ul>
	 */
	private void setDefaultAccessAndModification() throws CoreException {
		boolean[] def = new boolean[] { false, true }; // getDefaultAccessAndModificationValues();
		Object[] values = new Object[def.length];
		for (int i = 0; i < def.length; i++) {
			values[i] = Boolean.valueOf(def[i]);
		}
		String[] attributes = new String[] { ACCESS, MODIFICATION };
		setAttributes(attributes, values);
	}

}
