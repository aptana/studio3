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
	@Override
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
