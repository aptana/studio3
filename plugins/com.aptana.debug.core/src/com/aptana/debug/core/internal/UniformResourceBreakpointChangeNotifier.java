/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
		 * @see
		 * com.aptana.ide.core.resources.IUniformResourceChangeListener#resourceChanged(com.aptana.ide.core.resources
		 * .IUniformResourceChangeEvent)
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
						breakpoint.hashCode();
					} else if (kind == IResourceDelta.REMOVED) {
						/* TODO */
						breakpoint.hashCode();
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
