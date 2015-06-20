package ca.mestevens.ios.xcode.parser.models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class PBXFileElement implements Comparable<PBXFileElement>
{

	public CommentedIdentifier getReference()
	{
		return reference;
	}

	public String getIsa()
	{
		return isa;
	}

	public String getName()
	{
		return name;
	}

	public String getSourceTree()
	{
		return sourceTree;
	}

	public Integer getFileEncoding()
	{
		return fileEncoding;
	}

	public Integer getIncludeInIndex()
	{
		return includeInIndex;
	}

	public String getExplicitFileType()
	{
		return explicitFileType;
	}

	public String getLastKnownFileType()
	{
		return lastKnownFileType;
	}

	public String getPath()
	{
		return path;
	}

	public List<CommentedIdentifier> getChildren()
	{
		return children;
	}

	private CommentedIdentifier reference;
	private String isa;
	private String name;
	private String sourceTree;

	// PBXFileReference
	private Integer fileEncoding;
	private Integer includeInIndex;
	private String explicitFileType;
	private String lastKnownFileType;
	private String path;

	// PBXGroup, and PBXVariantGroup
	private List<CommentedIdentifier> children;

	public PBXFileElement(String filePath, String sourceTree)
	{
		Path path = Paths.get(filePath);
		this.name = path.getFileName().toString();
		this.reference = new CommentedIdentifier(UUID.randomUUID().toString(), this.name);
		this.isa = "PBXFileReference";
		this.path = path.toString();
		this.sourceTree = sourceTree;
	}

	public PBXFileElement(String isa, String name, String sourceTree)
	{
		this.name = name;
		this.isa = isa;
		this.sourceTree = sourceTree;
		this.reference = new CommentedIdentifier(UUID.randomUUID().toString(), this.name);
		this.children = new ArrayList<CommentedIdentifier>();
	}

	public PBXFileElement(String fileElementString) throws InvalidObjectFormatException
	{
		try
		{
			fileElementString = fileElementString.trim();
			int equalsIndex = fileElementString.indexOf('=');
			String commentPart = "";
			String uuidPart = fileElementString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = fileElementString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			ObjectParser parser = new ObjectParser(fileElementString);
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
				else if (key.equals("sourceTree"))
				{
					this.sourceTree = value;
				}
				else if (key.equals("fileEncoding"))
				{
					this.fileEncoding = Integer.valueOf(value);
				}
				else if (key.equals("explicitFileType"))
				{
					this.explicitFileType = value;
				}
				else if (key.equals("lastKnownFileType"))
				{
					this.lastKnownFileType = value;
				}
				else if (key.equals("path"))
				{
					this.path = value;
				}
				else if (key.equals("children"))
				{
					this.children = parser.getIdentifierList(value);
				}
				else if (key.equals("includeInIndex"))
				{
					this.includeInIndex = Integer.valueOf(value);
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
		if (this.isa.equals("PBXFileReference"))
		{
			returnString += tabString + reference.toString() + " = {isa = " + this.isa + "; ";
			if (this.explicitFileType != null)
			{
				returnString += "explicitFileType = " + this.explicitFileType + "; ";
			}
			if (this.includeInIndex != null)
			{
				returnString += "includeInIndex = " + this.includeInIndex + "; ";
			}
			if (this.lastKnownFileType != null)
			{
				returnString += "lastKnownFileType = " + this.lastKnownFileType + "; ";
			}
			if (this.name != null)
			{
				returnString += "name = " + this.name + "; ";
			}
			if (this.path != null)
			{
				returnString += "path = " + this.path + "; ";
			}
			if (this.sourceTree != null)
			{
				returnString += "sourceTree = " + this.sourceTree + "; ";
			}
			if (this.fileEncoding != null)
			{
				returnString += "fileEncoding = " + this.fileEncoding + "; ";
			}
			returnString += "};";
		}
		else
		{
			returnString += tabString + reference.toString() + " = {\n";
			returnString += tabString + "\tisa = " + this.isa + ";\n";
			returnString += tabString + "\tchildren = (\n";
			if (this.children != null)
			{
				for (CommentedIdentifier child : this.children)
				{
					returnString += tabString + "\t\t" + child.toString() + ",\n";
				}
			}
			returnString += tabString + "\t);\n";
			if (this.name != null)
			{
				returnString += tabString + "\tname = " + this.name + ";\n";
			}
			if (this.path != null)
			{
				returnString += tabString + "\tpath = " + this.path + ";\n";
			}
			if (this.sourceTree != null)
			{
				returnString += tabString + "\tsourceTree = " + this.sourceTree + ";\n";
			}
			returnString += tabString + "};";
		}
		return returnString;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof PBXFileElement)
		{
			PBXFileElement oFileElement = (PBXFileElement) o;
			if (oFileElement.getPath() != null && this.path != null)
			{
				return oFileElement.getPath().equals(this.path);
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.reference);
		builder.append(this.isa);
		builder.append(this.name);
		builder.append(this.sourceTree);
		builder.append(this.fileEncoding);
		builder.append(this.includeInIndex);
		builder.append(this.explicitFileType);
		builder.append(this.lastKnownFileType);
		builder.append(this.path);
		builder.append(this.children);
		return builder.toHashCode();
	}

	public void addChild(String reference, String comment)
	{
		CommentedIdentifier child = new CommentedIdentifier(reference, comment);
		if (!this.children.contains(child))
		{
			this.children.add(child);
		}
	}

	public void addChild(CommentedIdentifier child)
	{
		if (!this.children.contains(child))
		{
			this.children.add(child);
		}
	}

	@Override
	public int compareTo(PBXFileElement o)
	{
		return this.reference.getIdentifier().compareTo(o.getReference().getIdentifier());
	}

}
