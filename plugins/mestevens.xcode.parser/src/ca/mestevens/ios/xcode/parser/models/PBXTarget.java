package ca.mestevens.ios.xcode.parser.models;

import java.util.List;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class PBXTarget implements Comparable<PBXTarget>
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

	public List<CommentedIdentifier> getBuildPhases()
	{
		return buildPhases;
	}

	public List<CommentedIdentifier> getBuildRules()
	{
		return buildRules;
	}

	public List<CommentedIdentifier> getDependencies()
	{
		return dependencies;
	}

	public String getName()
	{
		return name;
	}

	public String getProductName()
	{
		return productName;
	}

	public String getProductInstallPath()
	{
		return productInstallPath;
	}

	public CommentedIdentifier getProductReference()
	{
		return productReference;
	}

	public String getProductType()
	{
		return productType;
	}

	private CommentedIdentifier reference;
	private String isa;
	private CommentedIdentifier buildConfigurationList;
	private List<CommentedIdentifier> buildPhases;
	private List<CommentedIdentifier> buildRules;
	private List<CommentedIdentifier> dependencies;
	private String name;
	private String productName;

	// PBXNativeTarget
	private String productInstallPath;
	private CommentedIdentifier productReference;
	private String productType;

	public PBXTarget(String targetString) throws InvalidObjectFormatException
	{
		try
		{
			targetString = targetString.trim();
			int equalsIndex = targetString.indexOf('=');
			String commentPart = "";
			String uuidPart = targetString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = targetString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			ObjectParser parser = new ObjectParser(targetString);
			parser = parser.getNextNestedObjects();
			String parserObject = parser.parseNextObject();
			while (parserObject != null && !parserObject.equals(""))
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
				else if (key.equals("buildPhases"))
				{
					this.buildPhases = parser.getIdentifierList(value);
				}
				else if (key.equals("dependencies"))
				{
					this.dependencies = parser.getIdentifierList(value);
				}
				else if (key.equals("name"))
				{
					this.name = value;
				}
				else if (key.equals("productName"))
				{
					this.productName = value;
				}
				else if (key.equals("productInstallPath"))
				{
					this.productInstallPath = value;
				}
				else if (key.equals("productReference"))
				{
					this.productReference = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("productType"))
				{
					this.productType = value;
				}
				else if (key.equals("buildRules"))
				{
					this.buildRules = parser.getIdentifierList(value);
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
		returnString += tabString + "\tbuildConfigurationList = " + this.buildConfigurationList.toString() + ";\n";
		returnString += tabString + "\tbuildPhases = (\n";
		for (CommentedIdentifier buildPhase : buildPhases)
		{
			returnString += tabString + "\t\t" + buildPhase.toString() + ",\n";
		}
		returnString += tabString + "\t);\n";
		returnString += tabString + "\tbuildRules = (\n";
		for (CommentedIdentifier rule : this.buildRules)
		{
			returnString += tabString + "\t\t" + rule.toString() + ",\n";
		}
		returnString += tabString + "\t);\n";
		returnString += tabString + "\tdependencies = (\n";
		for (CommentedIdentifier dependency : this.dependencies)
		{
			returnString += tabString + "\t\t" + dependency.toString() + ",\n";
		}
		returnString += tabString + "\t);\n";
		returnString += tabString + "\tname = " + this.name + ";\n";
		returnString += tabString + "\tproductName = " + this.productName + ";\n";
		if (this.productInstallPath != null)
		{
			returnString += tabString + "\tproductInstallPath = " + this.productInstallPath + ";\n";
		}
		if (this.productReference != null)
		{
			returnString += tabString + "\tproductReference = " + this.productReference.toString() + ";\n";
		}
		if (this.productType != null)
		{
			returnString += tabString + "\tproductType = " + this.productType + ";\n";
		}
		returnString += tabString + "};";
		return returnString;
	}

	@Override
	public int compareTo(PBXTarget o)
	{
		return this.reference.getIdentifier().compareTo(o.getReference().getIdentifier());
	}

}
