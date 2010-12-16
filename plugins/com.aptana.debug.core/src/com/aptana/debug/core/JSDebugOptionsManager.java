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

package com.aptana.debug.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;

import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.js.debug.core.model.IJSDebugTarget;
import com.aptana.js.debug.core.model.JSDebugModel;

/**
 * @author Max Stepanov
 */
public final class JSDebugOptionsManager implements IDebugEventSetListener {
	/**
	 * DEBUGGER_ACTIVE
	 */
	public static final String DEBUGGER_ACTIVE = IDebugConstants.ID_DEBUG_MODEL + ".debuggerActive"; //$NON-NLS-1$
	/**
	 * Singleton options manager
	 */
	private static JSDebugOptionsManager fgOptionsManager = null;

	/**
	 * Not to be instantiated
	 */
	private JSDebugOptionsManager() {
	}

	/**
	 * Return the default options manager
	 * 
	 * @return JSDebugOptionsManager
	 */
	public static JSDebugOptionsManager getDefault() {
		if (fgOptionsManager == null) {
			fgOptionsManager = new JSDebugOptionsManager();
		}
		return fgOptionsManager;
	}

	/**
	 * startup
	 */
	public void startup() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	/**
	 * shutdown
	 */
	public void shutdown() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	/**
	 * Parses the comma separated string into an array of strings
	 * 
	 * @param listString
	 * @return String[]
	 */
	public static String[] parseList(String listString) {
		List<String> list = new ArrayList<String>(10);
		StringTokenizer tokenizer = new StringTokenizer(listString, ","); //$NON-NLS-1$
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			list.add(token);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Serializes the array of strings into one comma separated string.
	 * 
	 * @param list
	 *            array of strings
	 * @return a single string composed of the given list
	 */
	public static String serializeList(String[] list) {
		if (list == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.length; i++) {
			if (i > 0) {
				buffer.append(',');
			}
			buffer.append(list[i]);
		}
		return buffer.toString();
	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; ++i) {
			DebugEvent event = events[i];
			if (event.getSource() instanceof IJSDebugTarget) {
				switch (event.getKind()) {
				case DebugEvent.CREATE:
					System.setProperty(DEBUGGER_ACTIVE, "true"); //$NON-NLS-1$
					break;
				case DebugEvent.TERMINATE:
					System.getProperties().remove(DEBUGGER_ACTIVE);
					cleanupBreakpoints();
					break;
				default:
					break;
				}
			}
		}
	}

	private void cleanupBreakpoints() {
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints(JSDebugModel.getModelIdentifier());
		for (int i = 0; i < breakpoints.length; ++i) {
			try {
				IMarker marker = breakpoints[i].getMarker();
				URI uri = null;
				if (marker instanceof IUniformResourceMarker) {
					uri = ((IUniformResourceMarker) marker).getUniformResource().getURI();
				} else {
					IResource resource = marker.getResource();
					if (resource instanceof IWorkspaceRoot) {
						uri = URI.create((String) marker.getAttribute(IDebugConstants.BREAKPOINT_LOCATION));
					} else {
						uri = resource.getLocation().makeAbsolute().toFile().toURI();
					}
				}
				if (uri != null && "dbgsource".equals(uri.getScheme())) //$NON-NLS-1$
				{
					breakpoints[i].delete();
				}
			} catch (CoreException e) {
				JSDebugPlugin.log(e);
			}
		}
	}

}
