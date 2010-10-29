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
import com.aptana.formatter.nodes.FormatterCommentNode;

/**
 * A JS formatter comment node.<br>
 * The comment node formatting will indent with an extra space any lines that appear under a multi-line comment block
 * (accept for the first line).
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSCommentNode extends FormatterCommentNode
{

	private final boolean isFirstline;
	private final boolean isMultiLine;

	/**
	 * Constructs a new formatter node for JS comments
	 * 
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 * @param isMultiLine
	 *            Indicates that this comment line is part of a multi-lines comment.
	 * @param isFirstline
	 *            Indicates that this line is the first line of the comment block.
	 */
	public FormatterJSCommentNode(IFormatterDocument document, int startOffset, int endOffset, boolean isMultiLine,
			boolean isFirstline)
	{
		super(document, startOffset, endOffset);
		this.isMultiLine = isMultiLine;
		this.isFirstline = isFirstline;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#accept(com.aptana.formatter.IFormatterContext,
	 * com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		boolean currentCommentState = context.isComment();
		context.setComment(true);
		if (getDocument().getBoolean(getWrappingKey()))
		{
			final boolean savedWrapping = context.isWrapping();
			context.setWrapping(true);
			writeCommentLine(visitor, context, getStartOffset(), getEndOffset());
			context.setWrapping(savedWrapping);
		}
		else
		{
			writeCommentLine(visitor, context, getStartOffset(), getEndOffset());
		}
		context.setComment(currentCommentState);
	}

	/**
	 * Do the actual writing of the comment line.
	 * 
	 * @param visitor
	 * @param context
	 * @param startOffset
	 * @param endOffset
	 */
	protected void writeCommentLine(IFormatterWriter visitor, IFormatterContext context, int startOffset, int endOffset)
	{
		if (!visitor.endsWithNewLine() && !isFirstline && isMultiLine)
		{
			visitor.writeLineBreak(context);
		}
		if (!isMultiLine)
		{
			// just write the comment as it is
			visitor.write(context, startOffset, endOffset);
		}
		else
		{
			if (!isFirstline)
			{
				// it's a multi-line, so we have to treat it differently and add the space in-front of the
				// lines, accept for the first line.
				visitor.ensureLineStarted(context);
				visitor.writeText(context, " "); //$NON-NLS-1$
				visitor.writeIndent(context);
			}
			visitor.write(context, startOffset, endOffset);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#getWrappingKey()
	 */
	public String getWrappingKey()
	{
		return JSFormatterConstants.WRAP_COMMENTS;
	}
}
