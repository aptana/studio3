/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
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
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class SWTUtils
{

	private static final String SMALL_FONT = "com.aptana.ui.small_font"; //$NON-NLS-1$
	private static Color backgroundErrorColor;
	private static Color errorColor;
	private static ModifyListener modifyListener;

	static
	{
		// runs in SWT thread as JFaceResources.getColorRegistry() could throw exception if not
		Display.getDefault().asyncExec(new Runnable()
		{

			public void run()
			{
				ColorRegistry cm = JFaceResources.getColorRegistry();
				RGB errorRGB = new RGB(255, 255, 180);
				cm.put("error", errorRGB); //$NON-NLS-1$
				backgroundErrorColor = cm.get("error"); //$NON-NLS-1$
			}
		});
		// Set a 'Red' error color
		errorColor = UIUtils.getDisplay().getSystemColor(SWT.COLOR_RED);
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
		shell.pack();
		center(shell, parent);
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
	 * Finds and caches the image from the image descriptor for this particular plugin.
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
	 * Finds and caches the image from the image descriptor for this particular plugin.
	 * 
	 * @param plugin
	 *            the plugin to search
	 * @param path
	 *            the path to the image
	 * @param descriptor
	 *            optional descriptor to use if the image doesn't exist
	 * @return the image, or null if not found
	 */
	public static Image getImage(AbstractUIPlugin plugin, String path, ImageDescriptor descriptor)
	{
		return getImage(plugin.getBundle(), path, descriptor);
	}

	/**
	 * Finds and caches the image from the complete image path.
	 * 
	 * @param computedPath
	 *            Represents the complete path of icon (BUNDLE_NAME/ICON_PATH)
	 * @return the image, or null if not found
	 */
	public static Image getImage(IPath computedPath)
	{
		String bundleSymbolicName = computedPath.segment(0);
		String iconPath = computedPath.removeFirstSegments(1).toOSString();
		return getImage(bundleSymbolicName, iconPath);
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
		return getImage(bundle, path, null);
	}

	private static Image getImage(Bundle bundle, String path, ImageDescriptor id)
	{
		if (path.charAt(0) != '/')
		{
			path = "/" + path; //$NON-NLS-1$
		}

		return getImage(bundle.getSymbolicName(), path, id);
	}

	private static Image getImage(String bundleSymbolicName, String path)
	{
		return getImage(bundleSymbolicName, path, null);
	}

	private static Image getImage(String bundleSymbolicName, String path, ImageDescriptor id)
	{
		String computedName = bundleSymbolicName + path;
		Image image = JFaceResources.getImage(computedName);
		if (image != null)
		{
			return image;
		}

		if (id == null)
		{
			id = AbstractUIPlugin.imageDescriptorFromPlugin(bundleSymbolicName, path);
		}

		if (id != null)
		{
			JFaceResources.getImageRegistry().put(computedName, id);
			return JFaceResources.getImage(computedName);
		}
		return null;
	}

	/**
	 * Scales the image based on the width and height, and maintains the aspect ratio
	 * 
	 * @param image
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 * @throws Exception
	 */
	public static Image scaleImage(Image image, double maxWidth, double maxHeight) throws Exception
	{
		ImageData imageData = image.getImageData();
		if (imageData.width > maxWidth || imageData.height > maxHeight)
		{
			// scale the image
			double scaleX = maxWidth / imageData.width;
			double scaleY = maxHeight / imageData.height;
			double scale = Math.min(scaleX, scaleY);
			imageData = imageData.scaledTo((int) Math.round(imageData.width * scale),
					(int) Math.round(imageData.height * scale));
			image.dispose();
			image = new Image(UIUtils.getDisplay(), imageData);
		}
		return image;
	}

	/**
	 * Convenience method for disposing of an image
	 * 
	 * @param image
	 */
	public static void disposeImage(Image image)
	{
		if (image != null && !image.isDisposed())
		{
			image.dispose();
		}
	}

	/**
	 * Convenience method for disposing of a control
	 * 
	 * @param control
	 */
	public static void disposeControl(Control control)
	{
		if (control != null && !control.isDisposed())
		{
			control.dispose();
		}
	}

	/**
	 * Convenience method for checking to see if a control is not disposed
	 * 
	 * @param control
	 */
	public static boolean isControlDisposed(Control control)
	{
		return (control != null) ? control.isDisposed() : true;
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
	 * Returns a version of the specified FontData, resized by the requested size.
	 * 
	 * @param fontData
	 *            the font-data to resize
	 * @param size
	 *            the font size
	 * @return resized font data
	 */
	public static FontData[] resizeFont(FontData[] fontData, int size)
	{
		for (FontData data : fontData)
		{
			data.setHeight(data.getHeight() + size);
		}

		return fontData;
	}

	/**
	 * Bolds a font.
	 * 
	 * @param font
	 * @return bolded font data
	 */
	public static FontData[] boldFont(Font font)
	{
		return styleFont(font, SWT.BOLD);
	}

	/**
	 * Styles a font.
	 * 
	 * @param font
	 * @return styled font data
	 */
	public static FontData[] styleFont(Font font, int fontStyle)
	{
		FontData[] datas = font.getFontData();
		for (FontData data : datas)
		{
			data.setStyle(data.getStyle() | fontStyle);
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
		return styleFont(font, SWT.ITALIC);
	}

	/**
	 * Returns a standard error {@link Color}.
	 * 
	 * @return An error {@link Color}
	 */
	public static Color getErrorColor()
	{
		return errorColor;
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
			combo.setBackground(backgroundErrorColor);
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
			widget.setBackground(backgroundErrorColor);
			if (modifyListener == null)
			{
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
							t.setBackground(backgroundErrorColor);
						}
					};
				};
				widget.addModifyListener(modifyListener);
			}
			return false;
		}
		return true;
	}

	/**
	 * Evaluates each of the controls and determines the largest width. Then sets each control with the largest width.
	 * Each control must have a GridData as its layout data
	 * 
	 * @param controls
	 */
	public static void resizeControlWidthInGrid(Collection<Control> controls)
	{
		resizeControlSizeInGrid(controls, false, true);
	}

	/**
	 * Evaluates each of the controls and determines the largest width. Then sets each control with the largest width.
	 * Each control must have a GridData as its layout data
	 * 
	 * @param controls
	 */
	public static void resizeControlHeightInGrid(Collection<Control> controls)
	{
		resizeControlSizeInGrid(controls, true, false);
	}

	/**
	 * Evaluates each of the controls and determines the largest width. Then sets each control with the largest width.
	 * Each control must have a GridData as its layout data
	 * 
	 * @param controls
	 */
	private static void resizeControlSizeInGrid(Collection<Control> controls, boolean resizeHeight, boolean resizeWidth)
	{
		int largestHeight = SWT.DEFAULT;
		int largestWidth = SWT.DEFAULT;
		List<GridData> gridDatas = new ArrayList<GridData>();
		for (Control control : controls)
		{
			Object layoutData = control.getLayoutData();
			if (layoutData instanceof GridData)
			{
				GridData gridData = (GridData) layoutData;
				gridDatas.add(gridData);

				if (resizeHeight && gridData.heightHint > largestHeight)
				{
					largestHeight = gridData.heightHint;
				}
				else if (resizeWidth && gridData.widthHint > largestWidth)
				{
					largestWidth = gridData.widthHint;
				}
				else
				{
					Point preferredSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					if (resizeHeight)
					{
						if (preferredSize.y > largestHeight)
						{
							largestHeight = preferredSize.y;
						}
					}
					else if (resizeWidth)
					{
						if (preferredSize.x > largestWidth)
						{
							largestWidth = preferredSize.x;
						}
					}
				}
			}
		}

		for (GridData gridData : gridDatas)
		{
			if (resizeHeight)
			{
				gridData.heightHint = largestHeight;
			}
			else if (resizeWidth)
			{
				gridData.widthHint = largestWidth;
			}
		}
	}

	/**
	 * Update the width of the label to take into account the error font. Only works for labels in a grid layout
	 * 
	 * @param label
	 * @param errorFont
	 */
	public static void updateErrorLabelWidth(Label label, Font errorFont)
	{
		Object layoutData = label.getLayoutData();
		if (layoutData instanceof GridData)
		{
			Font currentFont = label.getFont();
			label.setFont(errorFont);
			((GridData) layoutData).widthHint = label.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			label.setFont(currentFont);
		}
	}

	public static void updateLabelStatus(Label label, Font errorFont, Color errorColor, boolean isValid)
	{
		label.setForeground(isValid ? null : errorColor);
		label.setFont(isValid ? null : errorFont);
	}

	/**
	 * Sets the visibility of a control. Includes setting of the include property of the layoutData (if available)
	 * 
	 * @param visible
	 */
	public static void setVisiblity(Control control, boolean visible)
	{
		if (control == null)
		{
			return;
		}

		control.setVisible(visible);
		Object layoutData = control.getLayoutData();
		if (layoutData instanceof GridData)
		{
			((GridData) layoutData).exclude = !visible;
		}
	}
}
