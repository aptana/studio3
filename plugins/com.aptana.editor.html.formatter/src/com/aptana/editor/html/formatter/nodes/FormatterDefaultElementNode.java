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
import com.aptana.parsing.ast.IParseNode;

/**
 * A default tag node formatter is responsible of the formatting of a tag that has a begin and end, however, should not
 * be indented.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	protected boolean isAddingEndNewLine()
	{
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
}
