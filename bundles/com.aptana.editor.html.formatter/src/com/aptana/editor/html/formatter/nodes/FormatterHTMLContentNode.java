/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.nodes;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.editor.html.formatter.HTMLFormatterNodeBuilder;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterTextNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * Constructs a new content node for FormatterHTMLElementNode. We construct a node for the content of an element node so
 * we can control the new lines inside the element nodes appropriately.<br>
 * The node can be defined as 'space-sensitive' to make sure that the first char that is written back is a whitespace,
 * in case it was a whitespace before.
 * 
 * @param document
 * @param startOffset
 * @param endOffset
 */

public class FormatterHTMLContentNode extends FormatterTextNode
{
	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\r\n|\r|\n"); //$NON-NLS-1$
	private String parentTagName;
	private String previousSiblingTagName;
	private IParseNode parentElement;
	private boolean isSpaceSensitive;

	/**
	 * @param document
	 * @param parentElement
	 * @param previousSiblingElement
	 * @param startOffset
	 * @param endOffset
	 */
	public FormatterHTMLContentNode(IFormatterDocument document, IParseNode parentElement,
			IParseNode previousSiblingElement, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
		this.parentElement = parentElement;
		if (parentElement != null)
		{
			parentTagName = parentElement.getNameNode().getName().toLowerCase();
			isSpaceSensitive = HTMLFormatterNodeBuilder.SPACE_SENSITIVE_ELEMENTS.contains(parentTagName);
		}
		if (previousSiblingElement != null)
		{
			previousSiblingTagName = previousSiblingElement.getNameNode().getName().toLowerCase();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterTextNode#accept(com.aptana.formatter.IFormatterContext,
	 * com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		boolean emptyTagsInSameLine = getDocument()
				.getBoolean(HTMLFormatterConstants.NEW_LINES_EXCLUSION_IN_EMPTY_TAGS);
		boolean trimSpaces = getDocument().getBoolean(HTMLFormatterConstants.TRIM_SPACES);
		if (!(emptyTagsInSameLine || trimSpaces))
		{
			// Do a common accept
			visitor.write(context, getStartOffset(), getEndOffset());
			return;
		}
		String content = getDocument().get(getStartOffset(), getEndOffset());
		int contentTrimmedLength = content.trim().length();
		boolean insertNewLine = false;
		boolean lookForNewline = true;
		Set<String> tagsWithoutNewLine = getDocument().getSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		if (parentTagName != null)
		{
			if (!tagsWithoutNewLine.contains(parentTagName))
			{
				if (preserveNewLines(content))
				{
					super.accept(context, visitor);
					return;
				}
				else
				{
					if (contentTrimmedLength == 0 && emptyTagsInSameLine && !isSpaceSensitive
							&& !tagsWithoutNewLine.contains(previousSiblingTagName)
							&& parentElement.getChildCount() <= 1)
					{
						// this will trim any new-lines previously entered by the wrapping tag
						visitor.appendToPreviousLine(context, StringUtil.EMPTY);
						content = content.trim();
						lookForNewline = false;
					}
					else
					{
						insertNewLine = previousSiblingTagName == null
								|| !tagsWithoutNewLine.contains(previousSiblingTagName);
					}
				}
			}
			else
			{
				lookForNewline = false;
			}
		}
		if (!insertNewLine && content.length() == 0)
		{
			return;
		}
		if (!insertNewLine && lookForNewline)
		{
			// Check for a new line in the content. We set the 'insertNewLine' boolean to true only when the new
			// line arrive after all-whitespace characters
			for (int i = 0; i < content.length(); i++)
			{
				char c = content.charAt(i);
				if (c == '\n' || c == '\r')
				{
					insertNewLine = true;
					break;
				}
				if (!Character.isWhitespace(c))
				{
					break;
				}
			}
		}
		// Check if we need to trim the entire content.
		if (emptyTagsInSameLine)
		{
			if (contentTrimmedLength == 0)
			{
				if (insertNewLine)
				{
					visitor.writeLineBreak(context);
				}
				// if it's a space-sensitive content, add a space before returning
				if (isSpaceSensitive && content.length() > 0)
				{
					writeSpaces(visitor, context, 1);
				}
				return;
			}
		}
		// Check if we need to trim left & right spaces.
		if (trimSpaces)
		{
			content = content.trim();
		}
		// Write any spaces before writing the text
		int spacesCountBefore = getSpacesCountBefore();
		if (insertNewLine)
		{
			visitor.writeLineBreak(context);
		}
		else if (spacesCountBefore > 0)
		{
			writeSpaces(visitor, context, spacesCountBefore);
		}
		visitor.writeText(context, content, false);
		// Write any spaces after writing the text
		if (getSpacesCountAfter() > 0)
		{
			writeSpaces(visitor, context, getSpacesCountAfter());
		}
	}

	/**
	 * @param content
	 * @return
	 */
	private boolean preserveNewLines(String content)
	{
		Matcher matcher = NEW_LINE_PATTERN.matcher(content);
		int count = 0;
		while (matcher.find())
		{
			count++;
		}
		if (count == 1 && getDocument().getBoolean(HTMLFormatterConstants.NEW_LINES_EXCLUSION_IN_EMPTY_TAGS))
		{
			return false;
		}
		if (count > 1 && getDocument().getInt(HTMLFormatterConstants.PRESERVED_LINES) > 0)
		{
			// preserve new lines
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		Set<String> set = getDocument().getSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		return set.contains(parentTagName)
				|| (isSpaceSensitive && getDocument().getBoolean(HTMLFormatterConstants.TRIM_SPACES));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		if (!isEmpty() && Character.isWhitespace(getDocument().charAt(getStartOffset())))
		{
			return 1;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountAfter()
	 */
	@Override
	public int getSpacesCountAfter()
	{
		if (getStartOffset() < getEndOffset() - 1 && Character.isWhitespace(getDocument().charAt(getEndOffset() - 1)))
		{
			return 1;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterTextNode#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return getStartOffset() == getEndOffset();
	}

}
