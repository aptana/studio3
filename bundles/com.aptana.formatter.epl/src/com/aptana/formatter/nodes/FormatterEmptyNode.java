/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.nodes;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

/**
 * JSONEmptyFormatNode
 */
public class FormatterEmptyNode extends FormatterBlockNode
{
	/**
	 * JSONEmptyFormatNode
	 * 
	 * @param document
	 */
	public FormatterEmptyNode(IFormatterDocument document)
	{
		super(document);
	}

	/* (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#accept(com.aptana.formatter.IFormatterContext, com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		// do nothing
	}
}
