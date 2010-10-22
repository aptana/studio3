/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.nodes;

import java.util.List;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.IFormatterNode;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;

/**
 * A CSS formatter root node.<br>
 * This node will make sure that in case the CSS partition is nested in HTML, we prefix the formatted output with a new
 * line on the top.
 * 
 */
public class FormatterCSSRootNode extends FormatterBlockNode
{

	/**
	 * @param document
	 */
	public FormatterCSSRootNode(IFormatterDocument document)
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
