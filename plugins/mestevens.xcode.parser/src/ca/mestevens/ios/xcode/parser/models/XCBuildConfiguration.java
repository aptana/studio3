package ca.mestevens.ios.xcode.parser.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class XCBuildConfiguration implements Comparable<XCBuildConfiguration>
{

	public CommentedIdentifier getReference()
	{
		return reference;
	}

	private CommentedIdentifier reference;
	private String isa;
	private String baseConfigurationReference;
	private Map<String, String> buildSettings;
	private String name;

	private List<String> quoteSettings;

	public XCBuildConfiguration(String buildConfigurationString) throws InvalidObjectFormatException
	{
		try
		{
			buildConfigurationString = buildConfigurationString.trim();
			int equalsIndex = buildConfigurationString.indexOf('=');
			String commentPart = "";
			String uuidPart = buildConfigurationString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = buildConfigurationString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			ObjectParser parser = new ObjectParser(buildConfigurationString);
			parser = parser.getNextNestedObjects();
			String parserObject = parser.parseNextObject();
			while (parserObject != null)
			{
				// Remove the ';' character
				parserObject = parserObject.substring(0, parserObject.length() - 1);
				String[] splitObject = parserObject.split("=");
				String key = splitObject[0].trim();
				String value = "";
				for (int i = 1; i < splitObject.length; i++)
				{
					if (i > 1)
					{
						value += "=";
					}
					value += splitObject[i];
				}
				value = value.trim();
				if (key.equals("isa"))
				{
					this.isa = value;
				}
				else if (key.equals("name"))
				{
					this.name = value;
				}
				else if (key.equals("baseConfigurationReference"))
				{
					this.baseConfigurationReference = value;
				}
				else if (key.equals("buildSettings"))
				{
					value = value.substring(1, value.length() - 2);
					ObjectParser settingsParser = new ObjectParser(value);
					Map<String, String> settingsMap = new TreeMap<String, String>();
					List<String> quoteSettings = new ArrayList<String>();
					String settingsObject = settingsParser.parseNextObject();
					while (settingsObject != null)
					{
						settingsObject = settingsObject.substring(0, settingsObject.length() - 1);
						String[] splitSetting = settingsObject.split("=");
						String settingsKey = splitSetting[0];
						String settingsValue = "";
						if (settingsKey.startsWith("\""))
						{
							boolean addToKey = true;
							for (int i = 1; i < splitSetting.length; i++)
							{
								if (addToKey)
								{
									settingsKey += "=";
									if (splitSetting[i].contains("\""))
									{
										addToKey = false;
									}
									settingsKey += splitSetting[i];
								}
								else
								{
									if (settingsValue != "")
									{
										settingsValue += "=";
									}
									settingsValue += splitSetting[i];
								}
							}
							settingsKey = settingsKey.trim();

							// remove the quotes
							settingsKey = settingsKey.substring(1, settingsKey.length() - 1);
							quoteSettings.add(settingsKey);
						}
						else
						{
							settingsKey = settingsKey.trim();
							for (int i = 1; i < splitSetting.length; i++)
							{
								if (i > 1)
								{
									settingsValue += "=";
								}
								settingsValue += splitSetting[i];
							}
						}
						settingsValue = settingsValue.trim();
						settingsMap.put(settingsKey, settingsValue);
						settingsObject = settingsParser.parseNextObject();
					}
					this.buildSettings = settingsMap;
					this.quoteSettings = quoteSettings;
				}
				parserObject = parser.parseNextObject();
			}
		}
		catch (Exception ex)
		{
			throw new InvalidObjectFormatException(ex);
		}
	}

	public void setBuildSetting(String key, String value)
	{
		if (key.startsWith("\"") && key.endsWith("\""))
		{
			key = key.substring(1, key.length() - 1);
			this.quoteSettings.add(key);
		}
		buildSettings.put(key, value);
	}

	public void setBuildSetting(String key, List<String> values)
	{
		String valueString = StringUtils.join(values, ',');
		valueString = "(" + valueString + ")";
		if (key.startsWith("\"") && key.endsWith("\""))
		{
			key = key.substring(1, key.length() - 1);
			this.quoteSettings.add(key);
		}
		buildSettings.put(key, valueString);
	}

	public String getBuildSetting(String key)
	{
		if (key.startsWith("\"") && key.endsWith("\""))
		{
			key = key.substring(1, key.length() - 1);
		}
		return buildSettings.get(key);
	}

	public List<String> getBuildSettingAsList(String key)
	{
		if (key.startsWith("\"") && key.endsWith("\""))
		{
			key = key.substring(1, key.length() - 1);
		}
		String returnValue = buildSettings.get(key);
		if (returnValue != null)
		{
			if (!returnValue.startsWith("(") || !returnValue.endsWith(")"))
			{
				return null;
			}
			returnValue = returnValue.substring(1, returnValue.length() - 1);
			String[] returnArray = returnValue.split(",");
			List<String> returnList = new ArrayList<String>();
			for (String value : returnArray)
			{
				if (!value.trim().equals(""))
				{
					returnList.add(value.trim());
				}
			}
			return returnList;
		}
		return null;
	}

	@Override
	public String toString()
	{
		return toString(0);
	}

	public String toString(int numberOfTabs)
	{
		String tabString = "";
		for (int i = 0; i < numberOfTabs; i++)
		{
			tabString += "\t";
		}
		String returnString = "";
		returnString += tabString + this.reference.toString() + " = {\n";
		returnString += tabString + "\tisa = " + this.isa + ";\n";
		if (this.baseConfigurationReference != null)
		{
			returnString += tabString + "\tbaseConfigurationReference = " + this.baseConfigurationReference + ";\n";
		}
		returnString += tabString + "\tbuildSettings = {\n";
		for (String key : this.buildSettings.keySet())
		{
			String value = this.buildSettings.get(key);
			if (this.quoteSettings.contains(key))
			{
				returnString += tabString + "\t\t\"" + key + "\" = " + value + ";\n";
			}
			else
			{
				returnString += tabString + "\t\t" + key + " = " + value + ";\n";
			}
		}
		returnString += tabString + "\t};\n";
		returnString += tabString + "\tname = " + this.name + ";\n";
		returnString += tabString + "};";
		return returnString;
	}

	@Override
	public int compareTo(XCBuildConfiguration o)
	{
		return this.reference.getIdentifier().compareTo(o.reference.getIdentifier());
	}

}
