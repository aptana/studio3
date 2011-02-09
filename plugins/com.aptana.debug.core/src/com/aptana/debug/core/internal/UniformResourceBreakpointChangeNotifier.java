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
package com.aptana.debug.core.internal;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.model.IBreakpoint;

import com.aptana.core.resources.IUniformResourceChangeEvent;
import com.aptana.core.resources.IUniformResourceChangeListener;
import com.aptana.core.resources.MarkerUtils;

/**
 * @author Max Stepanov
 */
public class UniformResourceBreakpointChangeNotifier implements IBreakpointManagerListener {

	/**
	 * Uniform resource change listener
	 */
	private class ResourceChangeListener implements IUniformResourceChangeListener {

		/*
		 * @see com.aptana.ide.core.resources.IUniformResourceChangeListener#resourceChanged(com.aptana.ide.core.resources.IUniformResourceChangeEvent)
		 */
		public void resourceChanged(IUniformResourceChangeEvent event) {
			handleResourceChanged(event);
		}
	};

	private ResourceChangeListener resourceChangeListener = new ResourceChangeListener();

	/**
	 * UniformResourceBreakpointChangeNotifier
	 */
	public UniformResourceBreakpointChangeNotifier() {
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		breakpointManager.addBreakpointManagerListener(this);
		if (breakpointManager.isEnabled()) {
			breakpointManagerEnablementChanged(true);
		}
	}

	/**
	 * cleanup
	 */
	public void cleanup() {
		if (DebugPlugin.getDefault() != null) {
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpointManagerListener(this);
		}
	}

	/**
	 * handleResourceChanged
	 * 
	 * @param event
	 */
	private void handleResourceChanged(IUniformResourceChangeEvent event) {
		IMarkerDelta[] markerDeltas = event.findMarkerDeltas("org.eclipse.debug.core.lineBreakpointMarker", true); //$NON-NLS-1$
		if (markerDeltas.length == 0) {
			return;
		}
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		if (!breakpointManager.isEnabled()) {
			return;
		}
		for (IMarkerDelta delta : markerDeltas) {
			int kind = delta.getKind();
			if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED || kind == IResourceDelta.CHANGED) {
				IBreakpoint breakpoint = breakpointManager.getBreakpoint(delta.getMarker());
				if (breakpoint != null) {
					if (kind == IResourceDelta.ADDED) {
						/* TODO */
					} else if (kind == IResourceDelta.REMOVED) {
						/* TODO */
					} else {
						breakpointManager.fireBreakpointChanged(breakpoint);
					}
				}
			}
		}

	}

	/*
	 * @see org.eclipse.debug.core.IBreakpointManagerListener#breakpointManagerEnablementChanged(boolean)
	 */
	public void breakpointManagerEnablementChanged(boolean enabled) {
		if (enabled) {
			MarkerUtils.addResourceChangeListener(resourceChangeListener);
		} else {
			MarkerUtils.removeResourceChangeListener(resourceChangeListener);
		}
	}
}
