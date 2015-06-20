package ca.mestevens.ios.xcode.parser.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CommentedIdentifier implements Comparable<CommentedIdentifier>
{

	public String getComment()
	{
		return comment;
	}

	private final String identifier;
	private final String comment;

	public CommentedIdentifier(String identifier, String comment)
	{
		this.identifier = identifier;
		this.comment = null;
	}

	@Override
	public String toString()
	{
		String returnString = "";
		if (this.identifier != null && !this.identifier.equals(""))
		{
			returnString += this.identifier;
		}
		if (this.comment != null && !this.comment.equals(""))
		{
			if (!returnString.equals(""))
			{
				returnString += " ";
			}
			returnString += "/* " + this.comment + " */";
		}
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
		if (o instanceof CommentedIdentifier)
		{
			CommentedIdentifier oCommentedIdentifier = (CommentedIdentifier) o;
			return oCommentedIdentifier.getIdentifier().equals(this.identifier);
		}
		return false;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.identifier);
		builder.append(this.comment);
		return builder.toHashCode();
	}

	@Override
	public int compareTo(CommentedIdentifier o)
	{
		return this.identifier.compareTo(o.getIdentifier());
	}

}
