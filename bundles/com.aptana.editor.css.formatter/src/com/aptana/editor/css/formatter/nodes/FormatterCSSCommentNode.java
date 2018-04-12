/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.nodes;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterCommentNode;

public class FormatterCSSCommentNode extends FormatterCommentNode
{

	private final boolean isFirstLine;
	private final boolean isMultiLine;

	/**
	 * Constructs a new formatter node for CSS comments
	 * 
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 * @param isMultiLine
	 *            Indicates that this comment line is part of a multi-lines comment.
	 * @param isFirstline
	 *            Indicates that this line is the first line of the comment block.
	 */
	public FormatterCSSCommentNode(IFormatterDocument document, int startOffset, int endOffset, boolean isMultiLine,
			boolean isFirstLine)
	{
		super(document, startOffset, endOffset);
		this.isMultiLine = isMultiLine;
		this.isFirstLine = isFirstLine;
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
		if (!visitor.endsWithNewLine() && !isFirstLine && isMultiLine)
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
			if (!isFirstLine)
			{
				// it's a multi-line, so we have to treat it differently and add the space in-front of the
				// lines, except for the first line.
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
		return CSSFormatterConstants.WRAP_COMMENTS;
	}

}
