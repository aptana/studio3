/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.nodes;

import java.util.Set;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.editor.html.formatter.HTMLFormatterNodeBuilder;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.IFormatterTextNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * A default tag node formatter is responsible of the formatting of a tag that has a begin and end, however, should not
 * be indented.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterDefaultElementNode extends FormatterBlockWithBeginEndNode
{
	private String element;
	private IParseNode[] children;

	/**
	 * @param document
	 */
	public FormatterDefaultElementNode(IFormatterDocument document, String element, IParseNode[] children)
	{
		super(document);
		this.element = element;
		this.children = children;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	protected boolean isIndenting()
	{
		Set<String> set = getDocument().getSet(HTMLFormatterConstants.INDENT_EXCLUDED_TAGS);
		return !set.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	protected boolean isAddingBeginNewLine()
	{
		if (getDocument().getBoolean(HTMLFormatterConstants.NEW_LINES_EXCLUSION_IN_EMPTY_TAGS))
		{
			if (isEmptyContent())
			{
				return false;
			}
		}
		Set<String> set = getDocument().getSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		if (set.contains(element))
		{
			return false;
		}
		// In case the element contains text, we need to make sure that a text without any prefix whitespace does not
		// break into a new line.
		return !shouldPreventNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	protected boolean isAddingEndNewLine()
	{
		if (!isAddingBeginNewLine())
		{
			return false;
		}
		if (getDocument().getBoolean(HTMLFormatterConstants.NEW_LINES_EXCLUSION_IN_EMPTY_TAGS))
		{
			if (isEmptyContent())
			{
				return false;
			}
		}
		Set<String> set = getDocument().getSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		if (children == null || children.length == 0)
		{
			return (!set.contains(element));
		}

		// We only want to add a newline at the end if the last child is not in the exclusion list
		IParseNode child = children[children.length - 1];

		return !(set.contains(element) && set.contains(child.getNameNode().getName()) && HTMLFormatterNodeBuilder.VOID_ELEMENTS
				.contains(child.getNameNode().getName()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesAfter(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesAfter(IFormatterContext context)
	{
		int linesAfter = getInt(HTMLFormatterConstants.LINES_AFTER_ELEMENTS);
		if (linesAfter == 0)
		{
			return -1;
		}
		return linesAfter;
	}

	/**
	 * Returns true in case the node is one of the space-sensitive nodes, and its content prefix does not start with a
	 * whitespace. In that case, we should prevent any new-line breaking to avoid any visual changes in the way the
	 * browser renders the content.
	 * 
	 * @return True, in case the node should not have any new line; False, otherwise.
	 */
	private boolean shouldPreventNewLine()
	{
		if (children != null && children.length > 0
				&& HTMLFormatterNodeBuilder.SPACE_SENSITIVE_ELEMENTS.contains(element))
		{
			return !Character.isWhitespace(getDocument().charAt(children[0].getStartingOffset()));
		}
		return false;
	}

	/**
	 * @return true if the content of this node is empty.
	 */
	private boolean isEmptyContent()
	{
		int bodyElementsCount = getBody().size();
		if (bodyElementsCount == 0)
		{
			return true;
		}
		if (bodyElementsCount == 1 && getBody().get(0) instanceof IFormatterTextNode)
		{
			IFormatterTextNode contentNode = (IFormatterTextNode) getBody().get(0);
			String text = contentNode.getText();
			if (text.trim().length() == 0)
			{
				return true;
			}
		}
		return false;
	}
}
