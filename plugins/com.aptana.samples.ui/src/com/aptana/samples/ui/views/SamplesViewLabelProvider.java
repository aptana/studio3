/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.views;

import java.io.File;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SampleEntry;
import com.aptana.samples.model.SamplesReference;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
@SuppressWarnings("restriction")
public class SamplesViewLabelProvider extends LabelProvider
{
	private static final Image IMAGE_FOLDER = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJ_FOLDER);
	private static final Image IMAGE_FILE = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJ_FILE);

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
			String iconFile = ((SampleCategory) element).getIconFile();
			if (iconFile != null)
			{
				File file = new File(iconFile);
				if (file.exists())
				{
					String iconFilename = file.getAbsolutePath();
					Image image = imageRegistry.get(iconFilename);
					if (image == null)
					{
						image = new Image(Display.getDefault(), iconFilename);
						imageRegistry.put(iconFilename, image);
					}
					return image;
				}
			}
			// uses folder as the default image
			return IMAGE_FOLDER;
		}
		if (element instanceof SampleEntry)
		{
			File file = ((SampleEntry) element).getFile();
			if (file != null)
			{
				if (file.isDirectory())
				{
					return IMAGE_FOLDER;
				}

				IEditorDescriptor editorDescriptor = WorkbenchPlugin.getDefault().getEditorRegistry()
						.getDefaultEditor(file.getName());
				if (editorDescriptor == null || editorDescriptor.getImageDescriptor() == null)
				{
					return IMAGE_FILE;
				}
				String key = editorDescriptor.getId();
				Image image = imageRegistry.get(key);
				if (image == null)
				{
					image = editorDescriptor.getImageDescriptor().createImage();
					imageRegistry.put(key, image);
				}
				return image;
			}
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
			return name == null ? samplesRef.getPath() : name;
		}
		if (element instanceof SampleEntry)
		{
			File file = ((SampleEntry) element).getFile();
			if (file != null)
			{
				return file.getName();
			}
		}
		return super.getText(element);
	}
}
