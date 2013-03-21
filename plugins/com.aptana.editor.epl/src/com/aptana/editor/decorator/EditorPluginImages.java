/*******************************************************************************
 * Copyright (c) 2006 Zend Corporation and IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zend and IBM - Initial implementation
 *******************************************************************************/
package com.aptana.editor.decorator;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.osgi.framework.Bundle;

import com.aptana.editor.epl.EditorEplPlugin;

public class EditorPluginImages
{
	public static final IPath ICONS_PATH = new Path("/icons/full"); //$NON-NLS-1$

	private static final String T_OVR = "ovr16"; //$NON-NLS-1$

	// Descriptors
	public static final ImageDescriptor DESC_OVR_WARNING = createUnManagedCached(T_OVR, "warning_co.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_ERROR = createUnManagedCached(T_OVR, "error_co.gif"); //$NON-NLS-1$

	private static final class CachedImageDescriptor extends ImageDescriptor
	{
		private ImageDescriptor fDescriptor;
		private ImageData fData;

		public CachedImageDescriptor(ImageDescriptor descriptor)
		{
			fDescriptor = descriptor;
		}

		public ImageData getImageData()
		{
			if (fData == null)
			{
				fData = fDescriptor.getImageData();
			}
			return fData;
		}
	}

	private static ImageDescriptor createUnManagedCached(String prefix, String name)
	{
		return new CachedImageDescriptor(create(prefix, name, true));
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the DLTK UI bundle. The path can contain variables
	 * like $NL$. If no image could be found, <code>useMissingImageDescriptor</code> decides if either the 'missing
	 * image descriptor' is returned or <code>null</code>. or <code>null</code>.
	 */
	private static ImageDescriptor create(String prefix, String name, boolean useMissingImageDescriptor)
	{
		IPath path = ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(EditorEplPlugin.getDefault().getBundle(), path, useMissingImageDescriptor);
	}

	/*
	 * Creates an image descriptor for the given path in a bundle. The path can contain variables like $NL$. If no image
	 * could be found, <code>useMissingImageDescriptor</code> decides if either the 'missing image descriptor' is
	 * returned or <code>null</code>. Added for 3.1.1.
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor)
	{
		URL url = FileLocator.find(bundle, path, null);
		if (url != null)
		{
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor)
		{
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

}
