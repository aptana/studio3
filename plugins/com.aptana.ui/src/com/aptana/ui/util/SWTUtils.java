/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.util;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class SWTUtils
{

	private static final String SMALL_FONT = "com.aptana.ui.small_font"; //$NON-NLS-1$
	private static Color errorColor;
	private static ModifyListener modifyListener;
	
	static
	{
		ColorRegistry cm = JFaceResources.getColorRegistry();
		RGB errorRGB = new RGB(255, 255, 180);
		cm.put("error", errorRGB); //$NON-NLS-1$
		errorColor = cm.get("error"); //$NON-NLS-1$
	}

	/**
	 * Centers the shell on screen, and re-packs it to the preferred size. Packing is necessary as otherwise dialogs
	 * tend to get cut off on the Mac
	 * 
	 * @param shell
	 *            The shell to center
	 * @param parent
	 *            The shell to center within
	 */
	public static void centerAndPack(Shell shell, Shell parent)
	{
		center(shell, parent);
		shell.pack();
	}

	/**
	 * Centers the shell on screen.
	 * 
	 * @param shell
	 *            The shell to center
	 * @param parent
	 *            The shell to center within
	 */
	public static void center(Shell shell, Shell parent)
	{
		Rectangle parentSize = parent.getBounds();
		Rectangle mySize = shell.getBounds();

		int locationX, locationY;
		locationX = (parentSize.width - mySize.width) / 2 + parentSize.x;
		locationY = (parentSize.height - mySize.height) / 2 + parentSize.y;
		shell.setLocation(new Point(locationX, locationY));
	}

	/**
	 * Gets the default small font from the JFace font registry.
	 * 
	 * @return default small font
	 */
	public static Font getDefaultSmallFont()
	{
		Font small = JFaceResources.getFontRegistry().get(SMALL_FONT);
		if (small != null)
		{
			return small;
		}

		Font f = JFaceResources.getDefaultFont();
		FontData[] smaller = resizeFont(f, -2);
		JFaceResources.getFontRegistry().put(SMALL_FONT, smaller);
		return JFaceResources.getFontRegistry().get(SMALL_FONT);
	}

	/**
	 * Finds and caches the iamge from the image descriptor for this particular plugin.
	 * 
	 * @param plugin
	 *            the plugin to search
	 * @param path
	 *            the path to the image
	 * @return the image, or null if not found
	 */
	public static Image getImage(AbstractUIPlugin plugin, String path)
	{
		return getImage(plugin.getBundle(), path);
	}

	/**
	 * Finds and caches the image from the image descriptor for this particular bundle.
	 * 
	 * @param bundle
	 *            the bundle to search
	 * @param path
	 *            the path to the image
	 * @return the image, or null if not found
	 */
	public static Image getImage(Bundle bundle, String path)
	{
		if (path.charAt(0) != '/')
		{
			path = "/" + path; //$NON-NLS-1$
		}

		String computedName = bundle.getSymbolicName() + path;
		Image image = JFaceResources.getImage(computedName);
		if (image != null)
		{
			return image;
		}

		ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(bundle.getSymbolicName(), path);
		if (id != null)
		{
			JFaceResources.getImageRegistry().put(computedName, id);
			return JFaceResources.getImage(computedName);
		}
		return null;
	}

	/**
	 * Returns a version of the specified font, resized by the requested size.
	 * 
	 * @param font
	 *            the font to resize
	 * @param size
	 *            the font size
	 * @return resized font data
	 */
	public static FontData[] resizeFont(Font font, int size)
	{
		FontData[] datas = font.getFontData();
		for (FontData data : datas)
		{
			data.setHeight(data.getHeight() + size);
		}

		return datas;
	}

	/**
	 * Bolds a font.
	 * 
	 * @param font
	 * @return bolded font data
	 */
	public static FontData[] boldFont(Font font)
	{
		FontData[] datas = font.getFontData();
		for (FontData data : datas)
		{
			data.setStyle(data.getStyle() | SWT.BOLD);
		}
		return datas;
	}

	/**
	 * Makes a font italic.
	 * 
	 * @param font
	 * @return italicized font data
	 */
	public static FontData[] italicizedFont(Font font)
	{
		FontData[] datas = font.getFontData();
		for (FontData data : datas)
		{
			data.setStyle(data.getStyle() | SWT.ITALIC);
		}
		return datas;
	}

	/**
	 * Tests if the Combo value is empty. If so, it adds an error color to the background of the cell.
	 * 
	 * @param combo
	 *            the combo to validate
	 * @param validSelectionIndex
	 *            the first item that is a "valid" selection
	 * @return boolean
	 */
	public static boolean validateCombo(Combo combo, int validSelectionIndex)
	{
		int selectionIndex = Math.max(validSelectionIndex, 0);
		String text = combo.getText();
		if (text == null || text.length() == 0 || combo.getSelectionIndex() < selectionIndex)
		{
			combo.setBackground(errorColor);
			return false;
		}
		combo.setBackground(null);
		return true;
	}
	

	/**
	 * Tests if the widget value is empty. If so, it adds an error color to the background of the cell;
	 * 
	 * @param widget
	 *            the widget to set text for
	 * @return boolean
	 */
	public static boolean testWidgetValue(Text widget)
	{
		if (widget.getText() == null || "".equals(widget.getText())) //$NON-NLS-1$
		{
			widget.setBackground(errorColor);
			if(modifyListener == null) {
				modifyListener = new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						Text t = (Text) e.widget;
						if (t.getText() != null && !"".equals(t.getText())) //$NON-NLS-1$
						{
							t.setBackground(null);
						}
						else
						{
							t.setBackground(errorColor);
						}
					};
				};
				widget.addModifyListener(modifyListener);
			}
			return false;
		}
		return true;
	}	
}
