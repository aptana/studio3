package ca.mestevens.ios.xcode.parser.models;

import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class PBXContainerItemProxy implements Comparable<PBXContainerItemProxy>
{

	public CommentedIdentifier getReference()
	{
		return reference;
	}

	public String getIsa()
	{
		return isa;
	}

	public CommentedIdentifier getContainerPortal()
	{
		return containerPortal;
	}

	public Integer getProxyType()
	{
		return proxyType;
	}

	public String getRemoteGlobalIDString()
	{
		return remoteGlobalIDString;
	}

	public String getRemoteInfo()
	{
		return remoteInfo;
	}

	private CommentedIdentifier reference;
	private String isa;
	private CommentedIdentifier containerPortal;
	private Integer proxyType;
	private String remoteGlobalIDString;
	private String remoteInfo;

	public PBXContainerItemProxy(String containerItemProxyString) throws InvalidObjectFormatException
	{
		try
		{
			containerItemProxyString = containerItemProxyString.trim();
			int equalsIndex = containerItemProxyString.indexOf('=');
			String commentPart = "";
			String uuidPart = containerItemProxyString.substring(0, equalsIndex).trim();
			if (uuidPart.contains("/*"))
			{
				int commentStartIndex = uuidPart.indexOf("/*");
				int commentEndIndex = uuidPart.indexOf("*/");
				commentPart = containerItemProxyString.substring(commentStartIndex + 2, commentEndIndex).trim();
				uuidPart = uuidPart.substring(0, commentStartIndex).trim();
			}
			this.reference = new CommentedIdentifier(uuidPart, commentPart);
			ObjectParser parser = new ObjectParser(containerItemProxyString);
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
				else if (key.equals("containerPortal"))
				{
					this.containerPortal = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("proxyType"))
				{
					this.proxyType = Integer.valueOf(value);
				}
				else if (key.equals("remoteGlobalIDString"))
				{
					this.remoteGlobalIDString = value;
				}
				else if (key.equals("remoteInfo"))
				{
					this.remoteInfo = value;
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
		String returnString = tabString + reference.toString() + " = {\n";
		returnString += tabString + "\tisa = " + this.isa + ";\n";
		returnString += tabString + "\tcontainerPortal = " + this.containerPortal.toString() + ";\n";
		returnString += tabString + "\tproxyType = " + this.proxyType + ";\n";
		returnString += tabString + "\tremoteGlobalIDString = " + this.remoteGlobalIDString + ";\n";
		returnString += tabString + "\tremoteInfo = " + this.remoteInfo + ";\n";
		returnString += tabString + "};";
		return returnString;
	}

	@Override
	public int compareTo(PBXContainerItemProxy o)
	{
		return this.reference.getIdentifier().compareTo(o.getReference().getIdentifier());
	}

}
