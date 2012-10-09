/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;

/**
 * Define a cached image descriptor which only creates the image data once
 */
public class CachedImageDescriptor extends ImageDescriptor
{
	ImageDescriptor descriptor;

	ImageData data;

	public CachedImageDescriptor(ImageDescriptor descriptor)
	{
		this.descriptor = descriptor;
	}

	public ImageData getImageData()
	{
		if (data == null)
		{
			data = descriptor.getImageData();
		}
		return data;
	}
}