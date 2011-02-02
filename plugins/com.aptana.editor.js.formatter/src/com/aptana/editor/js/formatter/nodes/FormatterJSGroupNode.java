/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import java.util.List;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.IFormatterNode;

/**
 * Formatter node for JavaScript groups.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSGroupNode extends FormatterBlockWithBeginEndNode
{

	/**
	 * @param document
	 */
	public FormatterJSGroupNode(IFormatterDocument document)
	{
		super(document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#acceptBody(com.aptana.formatter.IFormatterContext,
	 * com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	protected void acceptBody(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		// We need to check here if the first child in the group body 'eats' the indentation.
		// If so, we correct this indent and de-dent by one.
		// (see https://aptana.lighthouseapp.com/projects/35272/tickets/1181)
		List<IFormatterNode> groupBody = getBody();
		boolean indentFixed = false;
		if (groupBody != null && !groupBody.isEmpty())
		{
			IFormatterNode firstNode = groupBody.get(0);
			if (firstNode.shouldConsumePreviousWhiteSpaces())
			{
				context.decIndent();
				indentFixed = true;
			}
		}
		super.acceptBody(context, visitor);
		if (indentFixed)
		{
			context.incIndent();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return getDocument().getBoolean(JSFormatterConstants.INDENT_GROUP_BODY);
	}
}
