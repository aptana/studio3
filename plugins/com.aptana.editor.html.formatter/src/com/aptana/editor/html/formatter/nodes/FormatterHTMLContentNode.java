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
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterTextNode;

/**
 * Constructs a new content node for FormatterHTMLElementNode. We construct a node for the content of an element node so
 * we can control the new lines inside the element nodes appropriately.
 * 
 * @param document
 * @param startOffset
 * @param endOffset
 */

public class FormatterHTMLContentNode extends FormatterTextNode
{

	private String parentElement;

	/**
	 * @param document
	 */
	public FormatterHTMLContentNode(IFormatterDocument document, String parentElement, int startOffset, int endOffset)
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
		Set<String> set = getDocument().getSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS);
		return set.contains(parentElement);
	}

}
