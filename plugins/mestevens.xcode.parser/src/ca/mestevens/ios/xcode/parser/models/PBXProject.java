package ca.mestevens.ios.xcode.parser.models;

import java.util.List;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class PBXProject implements Comparable<PBXProject>
{

	public CommentedIdentifier getReference()
	{
		return reference;
	}

	public String getIsa()
	{
		return isa;
	}

	public CommentedIdentifier getBuildConfigurationList()
	{
		return buildConfigurationList;
	}

	public String getCompatibilityVersion()
	{
		return compatibilityVersion;
	}

	public String getDevelopmentRegion()
	{
		return developmentRegion;
	}

	public Integer getHasScannedForEncodings()
	{
		return hasScannedForEncodings;
	}

	public List<String> getKnownRegions()
	{
		return knownRegions;
	}

	public CommentedIdentifier getMainGroup()
	{
		return mainGroup;
	}

	public CommentedIdentifier getProductRefGroup()
	{
		return productRefGroup;
	}

	public String getProjectDirPath()
	{
		return projectDirPath;
	}

	public String getProjectRoot()
	{
		return projectRoot;
	}

	public List<CommentedIdentifier> getTargets()
	{
		return targets;
	}

	public String getAttributes()
	{
		return attributes;
	}

	private CommentedIdentifier reference;
	private String isa;
	private CommentedIdentifier buildConfigurationList;
	private String compatibilityVersion;
	private String developmentRegion;
	private Integer hasScannedForEncodings;
	private List<String> knownRegions;
	private CommentedIdentifier mainGroup;
	private CommentedIdentifier productRefGroup;
	private String projectDirPath;
	private String projectRoot;
	private List<CommentedIdentifier> targets;
	// TODO make this an actual object
	private String attributes;

	public PBXProject(String projectString) throws InvalidObjectFormatException
	{
		try
		{
			projectString = projectString.trim();
			int equalsIndex = projectString.indexOf('=');
			String commentPart = "";
			String uuidPart = projectString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = projectString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			ObjectParser parser = new ObjectParser(projectString);
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
				else if (key.equals("buildConfigurationList"))
				{
					this.buildConfigurationList = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("compatibilityVersion"))
				{
					this.compatibilityVersion = value;
				}
				else if (key.equals("developmentRegion"))
				{
					this.developmentRegion = value;
				}
				else if (key.equals("hasScannedForEncodings"))
				{
					this.hasScannedForEncodings = Integer.valueOf(value);
				}
				else if (key.equals("knownRegions"))
				{
					this.knownRegions = parser.getStringList(value);
				}
				else if (key.equals("mainGroup"))
				{
					this.mainGroup = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("productRefGroup"))
				{
					this.productRefGroup = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("projectDirPath"))
				{
					this.projectDirPath = value;
				}
				else if (key.equals("projectRoot"))
				{
					this.projectRoot = value;
				}
				else if (key.equals("targets"))
				{
					this.targets = parser.getIdentifierList(value);
				}
				else if (key.equals("attributes"))
				{
					this.attributes = value;
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
		returnString += tabString + reference.toString() + " = {\n";
		returnString += tabString + "\tisa = " + this.isa + ";\n";
		if (this.attributes != null)
		{
			returnString += tabString + "\tattributes = " + this.attributes + ";\n";
		}
		returnString += tabString + "\tbuildConfigurationList = " + this.buildConfigurationList.toString() + ";\n";
		returnString += tabString + "\tcompatibilityVersion = " + this.compatibilityVersion + ";\n";
		returnString += tabString + "\tdevelopmentRegion = " + this.developmentRegion + ";\n";
		returnString += tabString + "\thasScannedForEncodings = " + this.hasScannedForEncodings + ";\n";
		returnString += tabString + "\tknownRegions = (\n";
		for (String knownRegion : this.knownRegions)
		{
			returnString += tabString + "\t\t" + knownRegion + ",\n";
		}
		returnString += tabString + "\t);\n";
		returnString += tabString + "\tmainGroup = " + this.mainGroup.toString() + ";\n";
		returnString += tabString + "\tproductRefGroup = " + this.productRefGroup.toString() + ";\n";
		returnString += tabString + "\tprojectDirPath = " + this.projectDirPath + ";\n";
		returnString += tabString + "\tprojectRoot = " + this.projectRoot + ";\n";
		returnString += tabString + "\ttargets = (\n";
		for (CommentedIdentifier target : this.targets)
		{
			returnString += tabString + "\t\t" + target.toString() + ",\n";
		}
		returnString += tabString + "\t);\n";
		returnString += tabString + "};";
		return returnString;
	}

	@Override
	public int compareTo(PBXProject o)
	{
		return this.reference.getIdentifier().compareTo(o.getReference().getIdentifier());
	}

}
