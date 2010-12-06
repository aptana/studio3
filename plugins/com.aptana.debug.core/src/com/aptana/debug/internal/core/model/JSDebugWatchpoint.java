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
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.IDebugConstants;
import com.aptana.debug.core.model.IJSVariable;
import com.aptana.debug.core.model.IJSWatchpoint;

/**
 * @author Max Stepanov
 */
public class JSDebugWatchpoint extends Breakpoint implements IJSWatchpoint {

	/**
	 * Watchpoint attribute storing the access value (value
	 * <code>"com.aptana.debug.core.watchpoint.access"</code>). This
	 * attribute is stored as a <code>boolean</code>, indicating whether a
	 * watchpoint is an access watchpoint.
	 */
	protected static final String ACCESS = "com.aptana.debug.core.watchpoint.access"; //$NON-NLS-1$

	/**
	 * Watchpoint attribute storing the modification value (value
	 * <code>"com.aptana.debug.core.watchpoint.access"</code>). This
	 * attribute is stored as a <code>boolean</code>, indicating whether a
	 * watchpoint is a modification watchpoint.
	 */
	protected static final String MODIFICATION = "com.aptana.debug.core.watchpoint.modification"; //$NON-NLS-1$	

	/**
	 * Watchpoint attribute storing the auto_disabled value (value
	 * <code>"com.aptana.debug.core.watchpoint.auto_disabled"</code>). This
	 * attribute is stored as a <code>boolean</code>, indicating whether a
	 * watchpoint has been auto-disabled (as opposed to being disabled
	 * explicitly by the user)
	 */
	protected static final String AUTO_DISABLED = "com.aptana.debug.core.watchpoint.auto_disabled"; //$NON-NLS-1$

	/**
	 * Default constructor is required for the breakpoint manager to re-create
	 * persisted breakpoints. After instantiating a breakpoint, the
	 * <code>setMarker(...)</code> method is called to restore this breakpoint's
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
	@SuppressWarnings("rawtypes")
	public JSDebugWatchpoint(IJSVariable variable) throws CoreException {
		this(ResourcesPlugin.getWorkspace().getRoot(), variable, new HashMap(), true);
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
	public JSDebugWatchpoint(final IResource resource, final IJSVariable variable, final Map attributes,
			final boolean register) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			@SuppressWarnings("unchecked")
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(IDebugConstants.ID_WATCHPOINT_MARKER);
				setMarker(marker);
				attributes.put(IBreakpoint.ENABLED, Boolean.TRUE);
				attributes.put(IDebugConstants.WATCHPOINT_VARIABLE_ACCESSOR, ((JSDebugVariable) variable)
						.getQualifier());
				attributes.put(IDebugConstants.WATCHPOINT_VARIABLE_NAME, variable.getFullName());
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IMarker.MESSAGE, MessageFormat.format(Messages.JSDebugWatchpoint_JS_Watchpoint,
						variable.getFullName()));
				addDefaultAccessAndModification(attributes);
				ensureMarker().setAttributes(attributes);

				register(register);
			}
		};
		run(getMarkerRule(resource), wr);
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
	 * @see com.aptana.debug.core.model.IJSWatchpoint#getVariableName()
	 */
	public String getVariableName() throws CoreException {
		return ensureMarker().getAttribute(IDebugConstants.WATCHPOINT_VARIABLE_NAME, StringUtil.EMPTY);
	}

	/**
	 * @see org.eclipse.debug.core.model.IWatchpoint#isAccess()
	 */
	public boolean isAccess() throws CoreException {
		return ensureMarker().getAttribute(ACCESS, false);
	}

	/**
	 * @see org.eclipse.debug.core.model.IWatchpoint#isModification()
	 */
	public boolean isModification() throws CoreException {
		return ensureMarker().getAttribute(MODIFICATION, false);
	}

	/**
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

	/**
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

	/**
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

	/**
	 * Adds the default access and modification attributes of the watchpoint to
	 * the given map
	 * <ul>
	 * <li>access = false
	 * <li>modification = true
	 * <li>auto disabled = false
	 * <ul>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addDefaultAccessAndModification(Map attributes) {
		boolean[] values = new boolean[] { false, true }; // getDefaultAccessAndModificationValues();
		attributes.put(ACCESS, (values[0] ? Boolean.TRUE : Boolean.FALSE));
		attributes.put(MODIFICATION, (values[1] ? Boolean.TRUE : Boolean.FALSE));
		attributes.put(AUTO_DISABLED, Boolean.FALSE);
	}

	/**
	 * Sets the default access and modification attributes of the watchpoint.
	 * The default values are:
	 * <ul>
	 * <li>access = <code>false</code>
	 * <li>modification = <code>true</code>
	 * <ul>
	 */
	protected void setDefaultAccessAndModification() throws CoreException {
		boolean[] def = new boolean[] { false, true }; // getDefaultAccessAndModificationValues();
		Object[] values = new Object[def.length];
		for (int i = 0; i < def.length; i++) {
			values[i] = new Boolean(def[i]);
		}
		String[] attributes = new String[] { ACCESS, MODIFICATION };
		setAttributes(attributes, values);
	}

	/**
	 * @see org.eclipse.debug.core.model.IWatchpoint#supportsAccess()
	 */
	public boolean supportsAccess() {
		return true;
	}

	/**
	 * @see org.eclipse.debug.core.model.IWatchpoint#supportsModification()
	 */
	public boolean supportsModification() {
		return true;
	}

	/**
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return IDebugConstants.ID_DEBUG_MODEL;
	}

}
