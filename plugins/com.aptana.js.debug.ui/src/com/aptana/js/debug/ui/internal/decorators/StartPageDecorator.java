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

import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.ui.internal.StartPageManager;

/**
 * @author Max Stepanov
 */
public class StartPageDecorator implements ILightweightLabelDecorator, StartPageManager.IStartPageChangeListener {

	private static final ImageDescriptor START_PAGE;

	static {
		START_PAGE = DebugUiPlugin.getImageDescriptor("icons/full/ovr16/start_page_ovr.gif"); //$NON-NLS-1$
	}

	private ListenerList listeners = new ListenerList();

	/**
	 * 
	 */
	public StartPageDecorator() {
		StartPageManager.getDefault().addListener(this);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 *      org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IFile) {
			if (StartPageManager.getDefault().isStartPage((IResource) element)) {
				decoration.addOverlay(START_PAGE);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		listeners.add(listener);
	}

	/**
	 * see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		listeners.clear();
		StartPageManager.getDefault().removeListener(this);
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.js.debug.ui.internal.StartPageManager.IStartPageChangeListener
	 * #startPageChanged(org.eclipse.core.resources.IResource)
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
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i) {
			final ILabelProviderListener listener = (ILabelProviderListener) list[i];
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
