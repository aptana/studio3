/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.WeakHashMap;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.misc.ExternalProgramImageDescriptor;

import com.aptana.core.util.PlatformUtil;
import com.aptana.theme.ThemePlugin;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public final class ImageUtils {

    private static final String USER_HOME = PlatformUtil.expandEnvironmentStrings(PlatformUtil.HOME_DIRECTORY);
    private static final String DESKTOP = PlatformUtil.expandEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY);
    private static final boolean ON_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	private static javax.swing.JFileChooser jFileChooser;
	private static final WeakHashMap<Object, String> iconToKeyMap = new WeakHashMap<Object, String>();

	private static boolean shouldReset;

	/**
	 * 
	 */
	private ImageUtils() {
	}
	
	public static ImageDescriptor getImageDescriptor(File file) {
		if (file.isFile()) {
			ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(file.getName());
			if (imageDescriptor != null) {
				return imageDescriptor;
			}
		}
		if (file.exists()) {
			if (jFileChooser == null) {
				jFileChooser = new javax.swing.JFileChooser();
			}
			String fileType = jFileChooser.getTypeDescription(file);
			if (fileType == null || fileType.length() == 0 || "Directory".equals(fileType) || "System Folder".equals(fileType) || "Generic File".equals(fileType)) {  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				String name = file.getName();
				try {
                    name = file.getCanonicalFile().getName();
                } catch (IOException e) {
                    name = file.getName();
                }
                if (name.equals((new Path(DESKTOP)).lastSegment())) {
                    fileType = "Desktop"; //$NON-NLS-1$
                } else if (!file.isDirectory()) {
					int index = name.lastIndexOf('.');
					if (index >= 0 && index < name.length()-1) {
						fileType = name.substring(index+1);
					} else {
						fileType = "unknown"; //$NON-NLS-1$
					}
				} else if ("Directory".equals(fileType) || name.length() == 0) { //$NON-NLS-1$
                    fileType = file.getAbsolutePath();
                } else if ("System Folder".equals(fileType)) { //$NON-NLS-1$
                    if (file.getAbsolutePath().equals(USER_HOME)) {
                        fileType = "UserHome"; //$NON-NLS-1$
                    }
                }
			}
			String imageKey = "os.fileType_" + fileType; //$NON-NLS-1$

			ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
			if (shouldReset) {
				Collection<String> imageKeys = iconToKeyMap.values();
				for (String key : imageKeys) {
					imageRegistry.remove(key);
				}
				iconToKeyMap.clear();
				shouldReset = false;
			}
			ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(imageKey);
			if (imageDescriptor != null) {
				return imageDescriptor;
			}
	
			Icon icon;
			if (ON_WINDOWS) {
			    icon = FileSystemView.getFileSystemView().getSystemIcon(file);
			} else {
			    icon = jFileChooser.getIcon(file);
			}
			if (icon != null) {
				String existingImageKey = iconToKeyMap.get(icon);
				if (existingImageKey != null) {
					imageDescriptor = imageRegistry.getDescriptor(existingImageKey);
					if (imageDescriptor != null) {
						return imageDescriptor;
					}
				}
				ImageData imageData = awtImageIconToSWTImageData(icon, null);
				if (imageData != null) {
					imageDescriptor = ImageDescriptor.createFromImageData(imageData);
					imageRegistry.put(imageKey, imageDescriptor);
					iconToKeyMap.put(icon, imageKey);
					return imageRegistry.getDescriptor(imageKey);
				}
			}
		}
		return getImageDescriptor(file.getName());
	}
	
	public static ImageDescriptor getImageDescriptor(String filename) {
		ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(filename);
		if (imageDescriptor == null) {
			imageDescriptor = getExtensionImageDescriptor(new Path(filename).getFileExtension());
		}
		return imageDescriptor;
	}

	public static void themeChanged()
	{
		shouldReset = true;
	}

	private static ImageDescriptor getExtensionImageDescriptor(String extension) {
		ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
		String imageKey = "extension_" + extension; //$NON-NLS-1$
		ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(imageKey);
		if (imageDescriptor == null) {
			Program program = Program.findProgram(extension);
			if (program != null) {
				imageDescriptor = new ExternalProgramImageDescriptor(program);
			} else {
				return null;
			}
			imageRegistry.put(imageKey, imageDescriptor);
			imageDescriptor = imageRegistry.getDescriptor(imageKey);
		}
		return imageDescriptor;
	}
	
	private static ImageData awtImageIconToSWTImageData(javax.swing.Icon icon, Color backgroundColor) {
		java.awt.Color bgColor = swtColorToAWTColor(backgroundColor != null ? backgroundColor : ThemePlugin
				.getDefault().getColorManager()
				.getColor(ThemePlugin.getDefault().getThemeManager().getCurrentTheme().getBackground()));

		java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(icon.getIconWidth(), icon.getIconHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics2D imageGraphics = bi.createGraphics();

		try {
			if (icon instanceof javax.swing.ImageIcon) {
				imageGraphics.drawImage(((javax.swing.ImageIcon) icon).getImage(), 0, 0, bgColor, (java.awt.image.ImageObserver) null);
			} else {
				if (jFileChooser == null) {
					jFileChooser = new javax.swing.JFileChooser();
				}
				imageGraphics.setBackground(bgColor);
				imageGraphics.clearRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
				icon.paintIcon(jFileChooser, imageGraphics, 0, 0);				
			}
			return awtBufferedImageToSWTImageData(bi);
		} finally {
			imageGraphics.dispose();
		}
	}

	private static java.awt.Color swtColorToAWTColor(Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	private static ImageData awtBufferedImageToSWTImageData(java.awt.image.BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof java.awt.image.DirectColorModel) {
			java.awt.image.DirectColorModel colorModel = (java.awt.image.DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			java.awt.image.WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof java.awt.image.IndexColorModel) {
			java.awt.image.IndexColorModel colorModel = (java.awt.image.IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];

			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}

			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
					.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			java.awt.image.WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];

			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}
}
