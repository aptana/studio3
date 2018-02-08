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
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterTextNode;

/**
 * Constructs a new content node for XML Element nodes. We construct a node for the content of an element node so we can
 * control the new lines inside the element nodes appropriately.
 * 
 * @param document
 * @param startOffset
 * @param endOffset
 */

public class FormatterXMLContentNode extends FormatterTextNode
{

	private String parentElement;

	/**
	 * @param document
	 */
	public FormatterXMLContentNode(IFormatterDocument document, String parentElement, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
		this.parentElement = parentElement;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		Set<String> set = getDocument().getSet(XMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		boolean excludeNewLineOnTextNodes = getDocument().getBoolean(
				XMLFormatterConstants.NEW_LINES_EXCLUDED_ON_TEXT_NODES);
		return set.contains(parentElement) || excludeNewLineOnTextNodes;
	}

}
