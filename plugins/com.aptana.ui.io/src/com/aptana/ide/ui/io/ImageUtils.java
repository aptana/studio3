/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.ide.ui.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.misc.ExternalProgramImageDescriptor;

import com.aptana.core.util.PlatformUtil;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public final class ImageUtils
{

	private static final String USER_HOME = PlatformUtil.expandEnvironmentStrings(PlatformUtil.HOME_DIRECTORY);
	private static final String DESKTOP = PlatformUtil.expandEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY);
	private static final boolean ON_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	private static javax.swing.JFileChooser jFileChooser;
	private static final Map<Object, String> iconToKeyMap = new WeakHashMap<Object, String>();

	// Maintain a map of image keys that indicate if a specific icon needs a reset (usually after a theme change).
	// We cannot just reset all images at once, as it yields image-disposed errors.
	private static Map<String, Boolean> resetMap = new HashMap<String, Boolean>();

	/**
	 * 
	 */
	private ImageUtils()
	{
	}

	public static ImageDescriptor getImageDescriptor(File file)
	{
		if (file.isFile())
		{
			ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry()
					.getImageDescriptor(file.getName());
			if (imageDescriptor != null)
			{
				return imageDescriptor;
			}
		}
		if (file.exists())
		{
			if (jFileChooser == null)
			{
				jFileChooser = new javax.swing.JFileChooser();
			}
			String fileType = jFileChooser.getTypeDescription(file);
			if (fileType == null
					|| fileType.length() == 0
					|| "Directory".equals(fileType) || "System Folder".equals(fileType) || "Generic File".equals(fileType)) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				String name = file.getName();
				try
				{
					name = file.getCanonicalFile().getName();
				}
				catch (IOException e)
				{
					name = file.getName();
				}
				if (name.equals((new Path(DESKTOP)).lastSegment()))
				{
					fileType = "Desktop"; //$NON-NLS-1$
				}
				else if (!file.isDirectory())
				{
					int index = name.lastIndexOf('.');
					if (index >= 0 && index < name.length() - 1)
					{
						fileType = name.substring(index + 1);
					}
					else
					{
						fileType = "unknown"; //$NON-NLS-1$
					}
				}
				else if ("Directory".equals(fileType) || name.length() == 0) { //$NON-NLS-1$
					fileType = file.getAbsolutePath();
				}
				else if ("System Folder".equals(fileType)) { //$NON-NLS-1$
					if (file.getAbsolutePath().equals(USER_HOME))
					{
						fileType = "UserHome"; //$NON-NLS-1$
					}
				}
			}
			String imageKey = "os.fileType_" + fileType; //$NON-NLS-1$

			ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
			if (resetMap.get(imageKey) != null && resetMap.get(imageKey))
			{
				imageRegistry.remove(imageKey);
				resetMap.remove(imageKey);
			}
			ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(imageKey);
			if (imageDescriptor != null)
			{
				return imageDescriptor;
			}

			Icon icon;
			if (ON_WINDOWS)
			{
				icon = FileSystemView.getFileSystemView().getSystemIcon(file);
			}
			else
			{
				icon = jFileChooser.getIcon(file);
			}
			if (icon != null)
			{
				String existingImageKey = iconToKeyMap.get(icon);
				if (existingImageKey != null)
				{
					imageDescriptor = imageRegistry.getDescriptor(existingImageKey);
					if (imageDescriptor != null)
					{
						return imageDescriptor;
					}
				}
				ImageData imageData = awtImageIconToSWTImageData(icon, null);
				if (imageData != null)
				{
					imageDescriptor = ImageDescriptor.createFromImageData(imageData);
					imageRegistry.put(imageKey, imageDescriptor);
					iconToKeyMap.put(icon, imageKey);
					resetMap.put(imageKey, false);
					return imageRegistry.getDescriptor(imageKey);
				}
			}
		}
		return getImageDescriptor(file.getName());
	}

	public static ImageDescriptor getImageDescriptor(String filename)
	{
		ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(filename);
		if (imageDescriptor == null)
		{
			imageDescriptor = getExtensionImageDescriptor(new Path(filename).getFileExtension());
		}
		return imageDescriptor;
	}

	public static void themeChanged()
	{
		String[] keySet = resetMap.keySet().toArray(new String[resetMap.size()]);
		for (String key : keySet)
		{
			resetMap.put(key, true);
		}
	}

	private static ImageDescriptor getExtensionImageDescriptor(String extension)
	{
		ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
		String imageKey = "extension_" + extension; //$NON-NLS-1$
		ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(imageKey);
		if (imageDescriptor == null)
		{
			Program program = Program.findProgram(extension);
			if (program != null)
			{
				imageDescriptor = new ExternalProgramImageDescriptor(program);
			}
			else
			{
				return null;
			}
			imageRegistry.put(imageKey, imageDescriptor);
			imageDescriptor = imageRegistry.getDescriptor(imageKey);
		}
		return imageDescriptor;
	}

	private static ImageData awtImageIconToSWTImageData(javax.swing.Icon icon, Color backgroundColor)
	{
		if (backgroundColor == null)
		{
			backgroundColor = UIUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		}
		java.awt.Color bgColor = swtColorToAWTColor(backgroundColor);

		java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics2D imageGraphics = bi.createGraphics();

		try
		{
			if (icon instanceof javax.swing.ImageIcon)
			{
				imageGraphics.drawImage(((javax.swing.ImageIcon) icon).getImage(), 0, 0, bgColor,
						(java.awt.image.ImageObserver) null);
			}
			else
			{
				if (jFileChooser == null)
				{
					jFileChooser = new javax.swing.JFileChooser();
				}
				imageGraphics.setBackground(bgColor);
				imageGraphics.clearRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
				icon.paintIcon(jFileChooser, imageGraphics, 0, 0);
			}
			return awtBufferedImageToSWTImageData(bi);
		}
		finally
		{
			imageGraphics.dispose();
		}
	}

	private static java.awt.Color swtColorToAWTColor(Color color)
	{
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	private static ImageData awtBufferedImageToSWTImageData(java.awt.image.BufferedImage bufferedImage)
	{
		if (bufferedImage.getColorModel() instanceof java.awt.image.DirectColorModel)
		{
			java.awt.image.DirectColorModel colorModel = (java.awt.image.DirectColorModel) bufferedImage
					.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(),
					colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			java.awt.image.WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			int pixel;
			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		else if (bufferedImage.getColorModel() instanceof java.awt.image.IndexColorModel)
		{
			java.awt.image.IndexColorModel colorModel = (java.awt.image.IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];

			for (int i = 0; i < rgbs.length; i++)
			{
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}

			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			java.awt.image.WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];

			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}
}
