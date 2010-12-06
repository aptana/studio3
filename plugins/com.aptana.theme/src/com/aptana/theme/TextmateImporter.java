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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.aptana.plist.PListParserFactory;

/**
 * An importer to bring in Textmate themes to our theme system. This is not guaranteed to work 100% because we don't
 * honor the same scope format. Scopes with spaces in them in particular are not going to translate properly and will
 * need to be tweaked manually.
 * 
 * @author cwilliams
 */
public class TextmateImporter
{

	/**
	 * Property names used in textmate plist files.
	 */
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String SCOPE = "scope"; //$NON-NLS-1$
	private static final String FONT_STYLE = "fontStyle"; //$NON-NLS-1$
	private static final String BACKGROUND = "background"; //$NON-NLS-1$
	private static final String FOREGROUND = "foreground"; //$NON-NLS-1$
	private static final String SETTINGS = "settings"; //$NON-NLS-1$

	public TextmateImporter()
	{
	}

	/**
	 * Converts a Textmate plist backed theme to a RadRails style theme object.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public Theme convert(File file) throws FileNotFoundException
	{
		try
		{
			return new Theme(ThemePlugin.getDefault().getColorManager(), convertToProperties(file));
		}
		catch (Exception e)
		{
			ThemePlugin.logError(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Properties convertToProperties(File file) throws IOException
	{
		Map<String, Object> plistProperties = parse(file);
		List<Map<String, Object>> tokenList = (List<Map<String, Object>>) plistProperties.get(SETTINGS);
		Map<String, Object> globals = (Map<String, Object>) tokenList.get(0).get(SETTINGS);
		Properties radRailsProps = new Properties();
		for (Map.Entry<String, Object> entry : globals.entrySet())
		{
			radRailsProps.put(entry.getKey(), entry.getValue());
		}
		radRailsProps.put(Theme.THEME_NAME_PROP_KEY, plistProperties.get(NAME));

		tokenList.remove(0);
		for (Map<String, Object> token : tokenList)
		{
			if (!token.containsKey(SCOPE))
				continue;
			String scope = (String) token.get(SCOPE);
			Map<String, Object> colors = (Map<String, Object>) token.get(SETTINGS);

			StringBuilder value = new StringBuilder();
			if (colors.containsKey(FOREGROUND))
			{
				String fg = (String) colors.get(FOREGROUND);
				value.append(fg);
			}
			else
			{
				if (colors.containsKey(BACKGROUND) || colors.containsKey(FONT_STYLE))
					value.append(radRailsProps.getProperty(Theme.FOREGROUND_PROP_KEY));
				else
				{
					String tokenName = (String) token.get(NAME);
					ThemePlugin.logWarning(MessageFormat.format("Token failed to import: {0}", tokenName)); //$NON-NLS-1$
					continue;
				}
			}

			if (colors.containsKey(BACKGROUND))
			{
				String bg = (String) colors.get(BACKGROUND);
				if (bg != null && bg.length() > 0)
				{
					value.append(Theme.DELIMETER);
					value.append(bg);
				}
			}

			if (colors.containsKey(FONT_STYLE))
			{
				String fontStyle = (String) colors.get(FONT_STYLE);
				if (fontStyle != null && fontStyle.length() > 0)
				{
					StringTokenizer tokenizer = new StringTokenizer(fontStyle);
					while (tokenizer.hasMoreTokens())
					{
						value.append(Theme.DELIMETER);
						value.append(tokenizer.nextToken());
					}
				}
			}
			StringTokenizer tokenizer = new StringTokenizer(scope, ","); //$NON-NLS-1$
			while (tokenizer.hasMoreTokens())
			{
				radRailsProps.put(tokenizer.nextToken().trim(), value.toString());
			}
		}

		return radRailsProps;
	}

	private static Map<String, Object> parse(File file) throws IOException
	{
		return PListParserFactory.parse(file);
	}
}
