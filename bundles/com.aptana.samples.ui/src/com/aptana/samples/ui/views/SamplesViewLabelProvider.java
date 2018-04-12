/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.views;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SamplesReference;
import com.aptana.samples.ui.SamplesUIPlugin;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
public class SamplesViewLabelProvider extends ColumnLabelProvider
{

	private static final Image IMAGE_FOLDER = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJ_FOLDER);
	private static final String ICON_REMOTE = "icons/samples_remote.gif"; //$NON-NLS-1$
	private static final String ICON_SIZE = "16"; //$NON-NLS-1$

	private ImageRegistry imageRegistry;

	public SamplesViewLabelProvider()
	{
		imageRegistry = new ImageRegistry();
	}

	@Override
	public void dispose()
	{
		imageRegistry.dispose();
		super.dispose();
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof SampleCategory)
		{
			SampleCategory category = (SampleCategory) element;
			URL iconFile = category.getIconFile();
			if (iconFile != null)
			{
				Image image = imageRegistry.get(category.getId());
				if (image == null)
				{
					ImageDescriptor desc = ImageDescriptor.createFromURL(iconFile);
					imageRegistry.put(category.getId(), desc);
				}
				return imageRegistry.get(category.getId());
			}
			// uses folder as the default image
			return IMAGE_FOLDER;
		}
		if (element instanceof SamplesReference)
		{
			SamplesReference sample = (SamplesReference) element;
			URL url = sample.getIconUrl(ICON_SIZE);
			if (url == null)
			{
				// falls back to the default icon
				url = sample.getIconUrl();
			}
			if (url != null)
			{
				String key = url.toString();
				Image image = imageRegistry.get(key);
				if (image == null)
				{
					imageRegistry.put(key, ImageDescriptor.createFromURL(url));
					image = imageRegistry.get(key);
				}
				return image;
			}
			return SamplesUIPlugin.getImage(ICON_REMOTE);
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof SampleCategory)
		{
			return ((SampleCategory) element).getName();
		}
		if (element instanceof SamplesReference)
		{
			SamplesReference samplesRef = (SamplesReference) element;
			String name = samplesRef.getName();
			return (name == null) ? samplesRef.getLocation() : name;
		}
		return super.getText(element);
	}

	@Override
	public String getToolTipText(Object element)
	{
		String toolTipText;

		if (element instanceof SampleCategory)
		{
			return ((SampleCategory) element).getName();
		}
		if (element instanceof SamplesReference)
		{
			SamplesReference samplesRef = (SamplesReference) element;
			toolTipText = samplesRef.getDescription();
			if (toolTipText == null)
			{
				toolTipText = samplesRef.getName();
			}
			if (toolTipText == null)
			{
				toolTipText = samplesRef.getLocation();
			}
			return toolTipText;
		}
		return super.getText(element);
	}
}
