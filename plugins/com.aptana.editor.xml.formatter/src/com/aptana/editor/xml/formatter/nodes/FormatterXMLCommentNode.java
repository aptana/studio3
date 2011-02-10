/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.nodes;

import com.aptana.editor.xml.formatter.XMLFormatterConstants;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterCommentNode;

public class FormatterXMLCommentNode extends FormatterCommentNode
{

	/**
	 * Constructs a new formatter node for XML comments
	 * 
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 */
	public FormatterXMLCommentNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#getWrappingKey()
	 */
	public String getWrappingKey()
	{
		return XMLFormatterConstants.WRAP_COMMENTS;
	}

}
