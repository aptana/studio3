/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.nodes;

import java.util.Set;

import com.aptana.editor.xml.formatter.XMLFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A formatter node which represents a 'Void' element (as described at W3C).<br>
 * A 'Void' element does not, and should not, have any closing tag pair. They only have a start tag.
 */
public class FormatterXMLVoidElementNode extends FormatterBlockWithBeginNode
{

	private String element;

	/**
	 * Constructs a new FormatterVoidElementNode
	 * 
	 * @param document
	 */
	public FormatterXMLVoidElementNode(IFormatterDocument document, String element)
	{
		super(document);
		this.element = element;
	}

	/**
	 * Returns true if the element should trigger a deeper indentation on the next line. <br>
	 * Note that this node is representing a 'void' node, so in most cases (if not all) this call should return false.
	 * However, we leave this open for the user decision, and in case this node is removed from the exclusions list, it
	 * will increase the indentation.<br>
	 * Because this node does not have any closing tag, there will be no way to decrease the indentation once it's
	 * increased!
	 * 
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		Set<String> set = getDocument().getSet(XMLFormatterConstants.INDENT_EXCLUDED_TAGS);
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
		return isAddingBeginNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginNode#getBlankLinesBefore(com.aptana.formatter.IFormatterContext
	 * )
	 */
	@Override
	protected int getBlankLinesBefore(IFormatterContext context)
	{
		if (context.getParent() != null && context.getChildIndex() > 1)
		{
			return getInt(XMLFormatterConstants.LINES_AFTER_ELEMENTS);
		}
		return super.getBlankLinesBefore(context);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesAfter(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesAfter(IFormatterContext context)
	{
		return -1;
	}
}
