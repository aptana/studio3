/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
				visitor.writeText(context, " ", false); //$NON-NLS-1$
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
