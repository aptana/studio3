package ca.mestevens.ios.xcode.parser.models;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class PBXBuildFile implements Comparable<PBXBuildFile>
{

	private CommentedIdentifier reference;
	private String isa;
	private CommentedIdentifier fileRef;
	private Map<String, String> settings;

	public PBXBuildFile(String filename, String fileRef)
	{
		this.reference = new CommentedIdentifier(UUID.randomUUID().toString(), filename);
		this.isa = "PBXBuildFile";
		this.fileRef = new CommentedIdentifier(fileRef, filename);
		this.settings = new TreeMap<String, String>();
	}

	public PBXBuildFile(String buildFileString) throws InvalidObjectFormatException
	{
		try
		{
			buildFileString = buildFileString.trim();
			int equalsIndex = buildFileString.indexOf('=');
			String commentPart = "";
			String uuidPart = buildFileString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = buildFileString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			this.settings = new TreeMap<String, String>();
			ObjectParser parser = new ObjectParser(buildFileString);
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
				else if (key.equals("fileRef"))
				{
					this.fileRef = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("settings"))
				{
					// Remove the surrounding { }
					value = value.substring(1, value.length() - 1).trim();
					String[] splitValues = value.split(";");
					for (String splitValueObject : splitValues)
					{
						String[] splitValueSplit = splitValueObject.split("=");
						String splitValueKey = splitValueSplit[0].trim();
						String splitValueValue = "";
						for (int i = 1; i < splitValueSplit.length; i++)
						{
							if (i > 1)
							{
								splitValueValue += "=";
							}
							splitValueValue += splitValueSplit[i];
						}
						splitValueValue = splitValueValue.trim();
						settings.put(splitValueKey, splitValueValue);
					}
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
		String returnString = reference.toString() + " = {";
		returnString += "isa" + " = " + this.isa + "; ";
		returnString += "fileRef" + " = " + this.fileRef.toString() + "; ";
		if (this.settings != null && this.settings.size() > 0)
		{
			returnString += "settings = {";
			for (String key : this.settings.keySet())
			{
				returnString += key + " = " + this.settings.get(key) + "; ";
			}
			returnString += "}; ";
		}
		returnString += "};";
		return returnString;
	}

	public String toString(int numberOfTabs)
	{
		String returnString = "";
		for (int i = 0; i < numberOfTabs; i++)
		{
			returnString += "\t";
		}
		returnString += this.toString();
		return returnString;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof PBXBuildFile)
		{
			PBXBuildFile oBuildFile = (PBXBuildFile) o;
			return oBuildFile.getFileRef().equals(this.fileRef)
					&& oBuildFile.getReference().getComment().equals(this.reference.getComment());
		}
		return false;
	}

	public CommentedIdentifier getReference()
	{
		return reference;
	}

	public String getIsa()
	{
		return isa;
	}

	public CommentedIdentifier getFileRef()
	{
		return fileRef;
	}

	public Map<String, String> getSettings()
	{
		return settings;
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.reference);
		builder.append(this.isa);
		builder.append(this.fileRef);
		return builder.toHashCode();
	}

	@Override
	public int compareTo(PBXBuildFile o)
	{
		return this.reference.getIdentifier().compareTo(o.getReference().getIdentifier());
	}

}