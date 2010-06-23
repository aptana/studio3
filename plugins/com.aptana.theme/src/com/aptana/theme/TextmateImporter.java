package com.aptana.theme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.xml.sax.InputSource;

import plistreader.AbstractReader;
import plistreader.PlistFactory;
import plistreader.PlistProperties;
import plistreader.PlistReaderException;

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
		return convert(new FileInputStream(file));
	}

	public Theme convert(InputStream stream)
	{
		try
		{
			return new Theme(ThemePlugin.getDefault().getColorManager(), convertToProperties(stream));
		}
		catch (Exception e)
		{
			ThemePlugin.logError(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Properties convertToProperties(InputStream stream) throws PlistReaderException
	{
		Reader characterStream = new InputStreamReader(stream);
		InputSource source = new InputSource(characterStream);
		AbstractReader plistReader = PlistFactory.createReader();
		plistReader.setSource(source);
		PlistProperties plistProperties = plistReader.parse();
		List<PlistProperties> tokenList = (List<PlistProperties>) plistProperties.getProperty(SETTINGS);
		PlistProperties globals = (PlistProperties) tokenList.get(0).getProperty(SETTINGS);
		Properties radRailsProps = globals.convertToProperties();
		radRailsProps.put(Theme.THEME_NAME_PROP_KEY, plistProperties.getProperty(NAME));

		tokenList.remove(0);
		for (PlistProperties token : tokenList)
		{
			if (!token.hasKey(SCOPE))
				continue;
			String scope = (String) token.getProperty(SCOPE);
			PlistProperties colors = (PlistProperties) token.getProperty(SETTINGS);

			StringBuilder value = new StringBuilder();
			if (colors.hasKey(FOREGROUND))
			{
				String fg = (String) colors.getProperty(FOREGROUND);
				value.append(fg);
			}
			else
			{
				if (colors.hasKey(BACKGROUND) || colors.hasKey(FONT_STYLE))
					value.append(radRailsProps.getProperty(Theme.FOREGROUND_PROP_KEY));
				else
				{
					String tokenName = (String) token.getProperty(NAME);
					ThemePlugin.logWarning(MessageFormat.format("Token failed to import: {0}", tokenName)); //$NON-NLS-1$
					continue;
				}
			}

			if (colors.hasKey(BACKGROUND))
			{
				String bg = (String) colors.getProperty(BACKGROUND);
				if (bg != null && bg.length() > 0)
				{
					value.append(Theme.DELIMETER);
					value.append(bg);
				}
			}

			if (colors.hasKey(FONT_STYLE))
			{
				String fontStyle = (String) colors.getProperty(FONT_STYLE);
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
}
