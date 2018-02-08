/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import java.util.List;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.IFormatterNode;

/**
 * A JavaScript formatter root node.<br>
 * This node will make sure that in case the JS partition is nested in HTML, we prefix the formatted output with a new
 * line on the top.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSRootNode extends FormatterBlockNode
{

	/**
	 * @param document
	 */
	public FormatterJSRootNode(IFormatterDocument document)
	{
		super(document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#acceptNodes(java.util.List,
	 * com.aptana.formatter.IFormatterContext, com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	protected void acceptNodes(List<IFormatterNode> nodes, IFormatterContext context, IFormatterWriter visitor)
			throws Exception
	{
		if (!nodes.isEmpty())
		{
			IFormatterNode lastNode = nodes.get(nodes.size() - 1);
			if (lastNode instanceof FormatterJSPunctuationNode)
			{
				((FormatterJSPunctuationNode) lastNode).setForceLineTermination(false);
			}
		}
		super.acceptNodes(nodes, context, visitor);
	}
}
