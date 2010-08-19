/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.ui.decorators;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.internal.SyncUtils;

/**
 * A class to decorate which objects are cloaked from synchronization.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class CloakedLabelDecorator implements ILightweightLabelDecorator {

    private static final ImageDescriptor IMAGE = SyncingUIPlugin
            .getImageDescriptor("icons/full/obj16/cloaked_decorator.gif"); //$NON-NLS-1$

    /**
     * The default implementation is to add the decorator to all objects. The
     * subclass should override.
     */
    public void decorate(Object element, IDecoration decoration) {
        if (!(element instanceof IAdaptable)) {
            return;
        }

        IAdaptable adaptable = (IAdaptable) element;
		// only shows the cloak decorator when the element is associated with a sync connection as a source or
		// destination
		if (SiteConnectionUtils.findSitesForSource(adaptable).length == 0
				&& SiteConnectionUtils.findSitesWithDestination(adaptable).length == 0) {
			return;
		}

        IFileStore fileStore = SyncUtils.getFileStore(adaptable);
        if (fileStore != null) {
            if (CloakingUtils.isFileCloaked(fileStore)) {
                addDecoration(decoration);
            }
        }
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    protected void addDecoration(IDecoration decoration) {
        decoration.addOverlay(IMAGE);
    }
}
