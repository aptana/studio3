package com.aptana.theme;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

/**
 * @author cwilliams
 */
public class ThemeExporter
{

	/**
	 * Export a theme to ruby code that is placed in a ruble.
	 * 
	 * @param themeDir
	 *            Directory in which to place a bundle.rb file with the code for the theme.
	 * @param theme
	 *            The theme to export to ruby (ruble) code
	 */
	@SuppressWarnings("nls")
	public void export(File themeDir, Theme theme)
	{
		themeDir.mkdirs();
		File themeFile = new File(themeDir, "bundle.rb");
		Writer writer = null;
		try
		{
			writer = new FileWriter(themeFile, true);
			Properties props = theme.toProps();

			StringBuilder builder = new StringBuilder();
			builder.append("require 'ruble/theme'\n\n");
			builder.append("Ruble::Theme.add({\n");

			for (String key : props.stringPropertyNames())
			{
				builder.append("  '").append(key).append("' => '");
				builder.append(props.getProperty(key));
				builder.append("',\n");
			}
			if (props.size() > 0)
			{
				builder.delete(builder.length() - 2, builder.length());
			}
			builder.append("})\n");
			writer.write(builder.toString());
		}
		catch (IOException e)
		{
			ThemePlugin.logError(e);
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

}
