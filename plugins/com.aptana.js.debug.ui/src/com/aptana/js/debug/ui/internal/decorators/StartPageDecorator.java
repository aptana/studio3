/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable declaredExceptions

package com.aptana.js.debug.ui.internal.decorators;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.js.debug.ui.internal.StartPageManager;

/**
 * @author Max Stepanov
 */
public class StartPageDecorator implements ILightweightLabelDecorator, StartPageManager.IStartPageChangeListener {

	private static final ImageDescriptor START_PAGE;

	static {
		START_PAGE = JSDebugUIPlugin.getImageDescriptor("icons/full/ovr16/start_page_ovr.gif"); //$NON-NLS-1$
	}

	private ListenerList listeners = new ListenerList();

	/**
	 * 
	 */
	public StartPageDecorator() {
		StartPageManager.getDefault().addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 * org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IFile) {
			if (StartPageManager.getDefault().isStartPage((IResource) element)) {
				decoration.addOverlay(START_PAGE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		listeners.clear();
		StartPageManager.getDefault().removeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.js.debug.ui.internal.StartPageManager.IStartPageChangeListener#startPageChanged(org.eclipse.core.resources
	 * .IResource)
	 */
	public void startPageChanged(IResource resource) {
		LabelProviderChangedEvent event;
		if (resource != null) {
			event = new LabelProviderChangedEvent(this, resource);
		} else {
			event = new LabelProviderChangedEvent(this);
		}
		fireLabelProviderChanged(event);
	}

	private void fireLabelProviderChanged(final LabelProviderChangedEvent event) {
		for (Object object : listeners.getListeners()) {
			final ILabelProviderListener listener = (ILabelProviderListener) object;
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
				}

				public void run() throws Exception {
					listener.labelProviderChanged(event);
				}
			});
		}
	}
}
