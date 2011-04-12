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
	private final boolean commentOnPreviousLine;

	/**
	 * @param document
	 * @param b
	 * @param hasBlock
	 * @param previousIfHasBlock
	 *            Indicate that the previous 'if' block (the 'true' part) has a blocked body in curly-braces.
	 * @param commentOnPreviousLine
	 */
	public FormatterJSElseNode(IFormatterDocument document, boolean hasBlock, boolean isElseIf,
			boolean previousIfHasBlock, boolean commentOnPreviousLine)
	{
		super(document);
		this.hasBlock = hasBlock;
		this.isElseIf = isElseIf;
		this.previousIfHasBlock = previousIfHasBlock;
		this.commentOnPreviousLine = commentOnPreviousLine;
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
		return commentOnPreviousLine || !previousIfHasBlock
				|| getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT);
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
				wroteIndent = true;
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
