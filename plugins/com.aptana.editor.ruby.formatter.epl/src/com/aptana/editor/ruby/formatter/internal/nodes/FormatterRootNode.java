/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.internal.nodes;

import java.util.List;

import com.aptana.editor.ruby.formatter.RubyFormatterConstants;
import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;

public class FormatterRootNode extends FormatterBlockNode
{

	/**
	 * @param document
	 */
	public FormatterRootNode(IFormatterDocument document)
	{
		super(document);
	}

	protected void acceptNodes(final List<IFormatterNode> nodes, IFormatterContext context, IFormatterWriter visitor)
			throws Exception
	{
		boolean wasRequire = false;
		for (IFormatterNode node : nodes)
		{
			context.enter(node);
			if (node instanceof FormatterRequireNode)
			{
				if (wasRequire)
				{
					context.setBlankLines(0);
				}
			}
			else if (wasRequire && (node instanceof IFormatterContainerNode || !FormatterUtils.isEmptyText(node)))
			{
				context.setBlankLines(getInt(RubyFormatterConstants.LINES_FILE_AFTER_REQUIRE));
				wasRequire = false;
			}
			node.accept(context, visitor);
			context.leave(node);
			if (node instanceof FormatterRequireNode)
			{
				wasRequire = true;
			}
		}
	}

}
