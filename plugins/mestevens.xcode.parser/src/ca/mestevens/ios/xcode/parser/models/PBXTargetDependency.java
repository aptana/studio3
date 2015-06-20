package ca.mestevens.ios.xcode.parser.models;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class PBXTargetDependency implements Comparable<PBXTargetDependency>
{

	private CommentedIdentifier reference;
	private String isa;
	private CommentedIdentifier target;
	private CommentedIdentifier targetProxy;

	public PBXTargetDependency(String targetDependencyString) throws InvalidObjectFormatException
	{
		try
		{
			targetDependencyString = targetDependencyString.trim();
			int equalsIndex = targetDependencyString.indexOf('=');
			String commentPart = "";
			String uuidPart = targetDependencyString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = targetDependencyString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			ObjectParser parser = new ObjectParser(targetDependencyString);
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
				else if (key.equals("target"))
				{
					this.target = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("targetProxy"))
				{
					this.targetProxy = parser.getCommentedIdentifier(value);
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
		returnString += tabString + "\ttarget = " + this.target.toString() + ";\n";
		returnString += tabString + "\ttargetProxy = " + this.targetProxy.toString() + ";\n";
		returnString += tabString + "};";
		return returnString;
	}

	@Override
	public int compareTo(PBXTargetDependency o)
	{
		return this.reference.getIdentifier().compareTo(o.reference.getIdentifier());
	}

}
