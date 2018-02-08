/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.properties;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.internal.OverlayIcon;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.core.util.StringUtil;
import com.aptana.ui.epl.UIEplPlugin;

@SuppressWarnings("restriction")
public class NaturesLabelProvider extends LabelProvider implements IFontProvider
{

	private static final ImageDescriptor APTANA_NATURE_IMAGE = AbstractUIPlugin.imageDescriptorFromPlugin(
			UIEplPlugin.PLUGIN_ID, "icons/aptana_nature.gif"); //$NON-NLS-1$;
	private static final ImageDescriptor EMPTY_IMAGE = AbstractUIPlugin.imageDescriptorFromPlugin(
			UIEplPlugin.PLUGIN_ID, "icons/transparent_16x16.png"); //$NON-NLS-1$;

	// a map between nature id and its text description
	private Map<String, String> fNatureDescriptions;
	private String fPrimaryNature;

	public NaturesLabelProvider(Map<String, String> natureDescriptions)
	{
		fNatureDescriptions = natureDescriptions;
	}

	@Override
	public String getText(Object element)
	{
		String description = fNatureDescriptions.get(element.toString());
		if (description == null)
		{
			return StringUtil.EMPTY;
		}
		if (isPrimary(element.toString()))
		{
			description += EplMessages.ProjectNaturesPage_LBL_Primary;
		}
		return description;
	}

	@Override
	public Image getImage(Object element)
	{

		String nature = element.toString();
		OverlayIcon oi = null;
		ImageData id = EMPTY_IMAGE.getImageData();

		try
		{
			ImageDescriptor d = IDEWorkbenchPlugin.getDefault().getProjectImageRegistry()
					.getNatureImage(element.toString());
			oi = new CenterIcon(EMPTY_IMAGE, d, new Point(id.width, id.height));
		}
		catch (Exception e)
		{
			oi = new CenterIcon(EMPTY_IMAGE, APTANA_NATURE_IMAGE, new Point(id.width, id.height));
		}

		if (UIEplPlugin.getDefault().getImageRegistry().get(nature) == null)
		{
			if (oi != null)
			{
				UIEplPlugin.getDefault().getImageRegistry().put(nature, oi.createImage());
			}
		}
		return UIEplPlugin.getDefault().getImageRegistry().get(nature);

	}

	public Font getFont(Object element)
	{
		// make the primary nature bold
		return isPrimary(element.toString()) ? JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT)
				: null;
	}

	private static class CenterIcon extends OverlayIcon
	{
		public CenterIcon(ImageDescriptor base, ImageDescriptor overlay, Point size)
		{
			super(base, overlay, size);
		}

		protected void drawTopRight(ImageDescriptor overlay)
		{
			if (overlay == null)
			{
				return;
			}
			int x = getSize().x / 2;
			int y = getSize().y / 2;
			ImageData id = overlay.getImageData();
			x -= id.width / 2;
			y -= id.height / 2;
			drawImage(id, x, y);
		}

	}

	public void setPrimaryNature(String primary)
	{
		fPrimaryNature = primary;
	}

	private boolean isPrimary(String nature)
	{
		return fPrimaryNature != null && fPrimaryNature.equals(nature);
	}
}
