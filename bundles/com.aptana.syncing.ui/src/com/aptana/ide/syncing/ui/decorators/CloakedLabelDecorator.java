/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import com.aptana.ide.ui.io.Utils;

/**
 * A class to decorate which objects are cloaked from synchronization.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class CloakedLabelDecorator implements ILightweightLabelDecorator
{

	private static final ImageDescriptor IMAGE = SyncingUIPlugin
			.getImageDescriptor("icons/full/obj16/cloaked_decorator.gif"); //$NON-NLS-1$

	/**
	 * The default implementation is to add the decorator to all objects. The subclass should override.
	 */
	public void decorate(Object element, IDecoration decoration)
	{
		if (!(element instanceof IAdaptable))
		{
			return;
		}

		IAdaptable adaptable = (IAdaptable) element;
		// only shows the cloak decorator when the element is associated with a sync connection as a source or
		// destination
		if (SiteConnectionUtils.findSitesForSource(adaptable).length == 0
				&& SiteConnectionUtils.findSitesWithDestination(adaptable).length == 0)
		{
			return;
		}

		IFileStore fileStore = Utils.getFileStore(adaptable);
		if (fileStore != null)
		{
			if (CloakingUtils.isFileCloaked(fileStore))
			{
				addDecoration(decoration);
			}
		}
	}

	public void addListener(ILabelProviderListener listener)
	{
	}

	public void dispose()
	{
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
	}

	protected void addDecoration(IDecoration decoration)
	{
		decoration.addOverlay(IMAGE);
	}
}
