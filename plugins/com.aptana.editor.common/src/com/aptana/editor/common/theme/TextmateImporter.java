package com.aptana.editor.common.theme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

import com.aptana.editor.common.CommonEditorPlugin;

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
	private static final String NAME = "name";
	private static final String SCOPE = "scope";
	private static final String FONT_STYLE = "fontStyle";
	private static final String BACKGROUND = "background";
	private static final String FOREGROUND = "foreground";
	private static final String SETTINGS = "settings";

	public TextmateImporter()
	{
	}

	/**
	 * Converts a Textmate plist backed theme to a RadRails style theme object.
	 * 
	 * @param file
	 * @return
	 */
	public Theme convert(File file)
	{
		try
		{
			// TODO Auto save to theme list?
			return new Theme(CommonEditorPlugin.getDefault().getColorManager(), convertToProperties(file));
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
		return null;
	}

	public static void main(String[] args) throws PlistReaderException, IOException
	{
		Properties radRailsProps = TextmateImporter.convertToProperties(new File(
				"/Users/cwilliams/Desktop/Bespin.tmTheme"));
		FileWriter writer = new FileWriter(
				"/Users/cwilliams/repos/red_core/plugins/com.aptana.editor.common/themes/bespin.properties");
		radRailsProps.store(writer, null);
	}

	@SuppressWarnings("unchecked")
	private static Properties convertToProperties(File file) throws FileNotFoundException, PlistReaderException
	{
		Reader characterStream = new FileReader(file);
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
					CommonEditorPlugin.logWarning(MessageFormat.format("Token failed to import: {0}", tokenName));
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
			StringTokenizer tokenizer = new StringTokenizer(scope, ",");
			while (tokenizer.hasMoreTokens())
			{
				radRailsProps.put(tokenizer.nextToken().trim(), value.toString());
			}
		}

		return radRailsProps;
	}
}
