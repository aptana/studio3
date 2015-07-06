package ca.mestevens.ios.xcode.parser.models;

import java.util.List;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class XCConfigurationList implements Comparable<XCConfigurationList>
{

	public CommentedIdentifier getReference()
	{
		return reference;
	}

	private CommentedIdentifier reference;
	private String isa;
	private List<CommentedIdentifier> buildConfigurations;
	private Integer defaultConfigurationIsVisible;
	private String defaultConfigurationName;

	public XCConfigurationList(String configurationListString) throws InvalidObjectFormatException
	{
		try
		{
			configurationListString = configurationListString.trim();
			int equalsIndex = configurationListString.indexOf('=');
			String commentPart = "";
			String uuidPart = configurationListString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = configurationListString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			ObjectParser parser = new ObjectParser(configurationListString);
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
				else if (key.equals("buildConfigurations"))
				{
					this.buildConfigurations = parser.getIdentifierList(value);
				}
				else if (key.equals("defaultConfigurationIsVisible"))
				{
					this.defaultConfigurationIsVisible = Integer.valueOf(value);
				}
				else if (key.equals("defaultConfigurationName"))
				{
					this.defaultConfigurationName = value;
				}
				parserObject = parser.parseNextObject();
			}
		}
		catch (Exception ex)
		{
			throw new InvalidObjectFormatException(ex);
		}
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
		returnString += tabString + "\tbuildConfigurations = (\n";
		for (CommentedIdentifier configuration : this.buildConfigurations)
		{
			returnString += tabString + "\t\t" + configuration.toString() + ",\n";
		}
		returnString += tabString + "\t);\n";
		returnString += tabString + "\tdefaultConfigurationIsVisible = " + this.defaultConfigurationIsVisible + ";\n";
		if (this.defaultConfigurationName != null)
		{
			returnString += tabString + "\tdefaultConfigurationName = " + this.defaultConfigurationName + ";\n";
		}
		returnString += tabString + "};";
		return returnString;
	}

	@Override
	public int compareTo(XCConfigurationList o)
	{
		return this.reference.getIdentifier().compareTo(o.reference.getIdentifier());
	}

}
