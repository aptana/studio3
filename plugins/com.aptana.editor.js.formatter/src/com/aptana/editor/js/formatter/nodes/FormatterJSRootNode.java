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
import com.aptana.formatter.ui.ScriptFormattingContextProperties;

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
	protected void acceptNodes(final List<IFormatterNode> nodes, IFormatterContext context, IFormatterWriter visitor)
			throws Exception
	{
		int indent = context.getIndent();
		if (!visitor.endsWithNewLine()
				&& getDocument().getInt(ScriptFormattingContextProperties.CONTEXT_ORIGINAL_OFFSET) > 0)
		{
			visitor.ensureLineStarted(context);
			visitor.writeLineBreak(context);
		}
		super.acceptNodes(nodes, context, visitor);
		if (indent > 0)
		{
			context.decIndent();
		}
	}
}
