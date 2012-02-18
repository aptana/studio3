/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.util;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import com.aptana.ui.epl.UIEplPlugin;

/**
 * Image decorators and composers utility class.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ImageDecoratorUtil
{
	private static final String IMG_WIZBAN_DEFAULT = "icons/full/wizban/defaultWizard.png"; //$NON-NLS-1$

	/**
	 * Returns a composite image-descriptor that was composed from a base and an overlay image-descriptors.<br>
	 * Note that the overlay image is always centered on top of the base image.
	 * 
	 * @param base
	 * @param overlay
	 * @return An {@link ImageDescriptor} that is a composition of the given descriptors.
	 */
	public static ImageDescriptor getCompositeDescriptor(ImageDescriptor baseImageDescriptor,
			ImageDescriptor overlayImageDescriptor)
	{
		return new CenteredCompositeImageDescriptor(baseImageDescriptor, overlayImageDescriptor);
	}

	/**
	 * Returns a composition of a standard basic wizard-banner image with a given overlay image.
	 * 
	 * @param overlayImage
	 * @return An {@link ImageDescriptor} composition.
	 */
	public static ImageDescriptor getCompositeWizbanDescriptor(ImageDescriptor overlayImage)
	{
		return getCompositeDescriptor(UIEplPlugin.imageDescriptorFromPlugin(UIEplPlugin.PLUGIN_ID, IMG_WIZBAN_DEFAULT),
				overlayImage);
	}

	/**
	 * A composite {@link ImageDescriptor} that centers an overlay image on top of a base image.
	 */
	private static class CenteredCompositeImageDescriptor extends CompositeImageDescriptor
	{

		private ImageData baseImageData;
		private ImageData overlayImageData;
		private Point size;

		/**
		 * Constructs a new CenteredCompositeImageDescriptor
		 * 
		 * @param baseImage
		 * @param overlayImage
		 */
		CenteredCompositeImageDescriptor(ImageDescriptor baseImage, ImageDescriptor overlayImage)
		{
			this.baseImageData = baseImage.getImageData();
			this.overlayImageData = overlayImage.getImageData();
			this.size = new Point(baseImageData.width, baseImageData.height);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
		 */
		@Override
		protected void drawCompositeImage(int width, int height)
		{
			drawImage(baseImageData, 0, 0);
			// compute the x and y for drawing close to the center.
			// If the overlay image is larger than the base image, just position it on the top-left.
			int x = (size.x - Math.min(size.x, overlayImageData.width)) / 2;
			int y = (size.y - Math.min(size.y, overlayImageData.height)) / 2;
			drawImage(overlayImageData, x, y);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
		 */
		@Override
		protected Point getSize()
		{
			return size;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getTransparentPixel()
		 */
		protected int getTransparentPixel()
		{
			return baseImageData.transparentPixel;
		}
	}
}
