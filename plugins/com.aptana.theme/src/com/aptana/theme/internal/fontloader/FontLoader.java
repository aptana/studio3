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
package com.aptana.theme.internal.fontloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IColorFactory;

import com.aptana.theme.ThemePlugin;

/**
 * This is a hack used to load the custom font used by Aptana theme before the theme itself is loaded.
 * 
 * @author schitale
 */
public class FontLoader implements IColorFactory
{
	private static final RGB FONT_LOADER = new RGB(255, 255, 255);

	/**
	 * Filename of the font we're using.
	 */
	private static final String FONT_FILE = "Inconsolata.otf"; //$NON-NLS-1$

	public FontLoader()
	{
		// Make sure that the custom font is copied to the plugin state location
		IPath targetPath = ThemePlugin.getDefault().getStateLocation().append(FONT_FILE);
		if (!targetPath.toFile().exists())
		{
			copyFontToStateLocation(targetPath);
		}

		// Possibly install the font in the user's home directory
		if (Platform.getOS().equals(Platform.OS_LINUX))
		{
			// TODO: Does this apply to gnome only and if so, should we check if it is running
			// before copying the font?
			File userHome = new File(System.getProperty("user.home")); //$NON-NLS-1$
			File fontsDirectory = new File(userHome, ".fonts"); //$NON-NLS-1$
			File font = new File(fontsDirectory, FONT_FILE);

			if (!font.exists())
			{
				// Make sure .fonts directory exists
				fontsDirectory.mkdirs();

				// Make sure we have a directory and can write to it
				if (fontsDirectory.isDirectory() && fontsDirectory.canWrite())
				{
					// Install the font
					copyFontToFile(font);
				}
			}
		}

		// Load the custom font
		if (targetPath.toFile().exists())
		{
			Display display = PlatformUI.getWorkbench().getDisplay();
			display.loadFont(targetPath.toOSString());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.themes.IColorFactory#createColor()
	 */
	public RGB createColor()
	{
		return FONT_LOADER;
	}

	private static void copyFontToStateLocation(IPath targetPath)
	{
		copyFontToFile(targetPath.toFile());
	}

	private static void copyFontToFile(File file)
	{
		// Copy font out of the JARred plug-in and stick it in the plug-in state location.
		InputStream stream = null;
		FileOutputStream out = null;
		try
		{
			stream = FileLocator.openStream(ThemePlugin.getDefault().getBundle(), new Path(FONT_FILE), false);
			out = new FileOutputStream(file);
			int b = -1;
			while ((b = stream.read()) != -1)
			{
				out.write(b);
			}
		}
		catch (Exception e)
		{
			ThemePlugin.logError(e);
		}
		finally
		{
			try
			{
				if (out != null)
					out.close();
			}
			catch (IOException e)
			{
				// ignore
			}
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}
}
