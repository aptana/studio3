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

			for (Object key : props.keySet())
			{
				builder.append("  '").append(key).append("' => '");
				builder.append(props.get(key));
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
