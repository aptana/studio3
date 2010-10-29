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
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * Formatter node for 'else' nodes.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSElseNode extends FormatterBlockWithBeginNode
{

	private boolean hasBlock;
	private final boolean isElseIf;
	private final boolean previousIfHasBlock;

	/**
	 * @param document
	 * @param b
	 * @param hasBlock
	 * @param previousIfHasBlock
	 *            Indicate that the previous 'if' block (the 'true' part) has a blocked body in curly-braces.
	 */
	public FormatterJSElseNode(IFormatterDocument document, boolean hasBlock, boolean isElseIf,
			boolean previousIfHasBlock)
	{
		super(document);
		this.hasBlock = hasBlock;
		this.isElseIf = isElseIf;
		this.previousIfHasBlock = previousIfHasBlock;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return !isAddingBeginNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		return !previousIfHasBlock || getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT);
	}

	/**
	 * Override the default accept method to provide a unique support for the if-else indentation. We want to make sure
	 * that the indentation of the if-else blocks stays in one level as much as possible.
	 * 
	 * @see com.aptana.formatter.nodes.FormatterBlockWithBeginNode#accept(com.aptana.formatter.IFormatterContext,
	 *      com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		if (getBegin() != null)
		{
			boolean wroteIndent = false;
			if (getSpacesCountBefore() > 0 && shouldConsumePreviousWhiteSpaces())
			{
				writeSpaces(visitor, context, getSpacesCountBefore());
			}
			if (isAddingBeginNewLine())
			{
				if (!visitor.endsWithNewLine())
				{
					visitor.writeLineBreak(context);

				}
				if (isElseIf || !previousIfHasBlock)
				{
					visitor.writeIndent(context);
					wroteIndent = true;
				}
			}
			else if (visitor.endsWithNewLine())
			{
				visitor.writeIndent(context);
			}

			int indent = context.getIndent();
			if (wroteIndent)
			{
				context.resetIndent();
			}
			visitor.write(context, getBegin().getStartOffset(), getBegin().getEndOffset());
			if (wroteIndent)
			{
				context.setIndent(indent);
			}
		}
		if (!hasBlock && !isElseIf)
		{
			context.incIndent();
		}
		acceptBody(context, visitor);
		if (!hasBlock && !isElseIf)
		{
			context.decIndent();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		// We add indent after that 'else' in case it does not have a blocked child, and
		// the non-blocked child is not an else-if. In case it is, we check if this else-if should be
		// broken into two lines. If so, we indent anyway.
		return !hasBlock
				&& (!isElseIf || !getDocument()
						.getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT));
	}
}
