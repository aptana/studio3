/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter;

import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterNodeRewriter;
import com.aptana.formatter.nodes.IFormatterNode;

public class XMLFormatterNodeRewriter extends FormatterNodeRewriter
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterNodeRewriter#createCommentNode(com.aptana.formatter.IFormatterDocument,
	 * int, int, java.lang.Object)
	 */
	@Override
	protected IFormatterNode createCommentNode(IFormatterDocument document, int startOffset, int endOffset,
			Object object)
	{
		// return new FormatterXMLCommentNode(document, startOffset, endOffset);
		return null;
	}

}