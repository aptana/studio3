/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.editor.css.formatter.nodes.FormatterCSSCommentNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterNodeRewriter;
import com.aptana.formatter.nodes.IFormatterNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class CSSFormatterNodeRewriter extends FormatterNodeRewriter
{
	private static final Pattern COMMENT_LINE_PATTERN = Pattern.compile("(\\S.*)"); //$NON-NLS-1$
	private static final String MULTI_LINE_COMMENT_PREFIX = "/*"; //$NON-NLS-1$

	public CSSFormatterNodeRewriter(IParseRootNode parseResult, FormatterDocument document)
	{
		insertComments(document, parseResult.getCommentNodes());
	}

	private void insertComments(FormatterDocument document, IParseNode[] comments)
	{
		for (IParseNode commentNode : comments)
		{
			// in case we have a multi-line block comment, we actually break the comment to its lines and
			// create a comment node for each line.
			int startingOffset = commentNode.getStartingOffset();
			int endingOffset = commentNode.getEndingOffset() + 1;
			String commentText = document.get(startingOffset, endingOffset);
			if (commentText.startsWith(MULTI_LINE_COMMENT_PREFIX))
			{
				// Push each line as a comment. Mark the first line as a 'first'.
				Matcher matcher = COMMENT_LINE_PATTERN.matcher(commentText);
				boolean isFirstLine = true;
				while (matcher.find())
				{
					int start = matcher.start();
					int end = matcher.end();
					addComment(startingOffset + start, startingOffset + end, new CSSCommentInfo(true, isFirstLine));
					isFirstLine = false;
				}
			}
			else
			{
				addComment(startingOffset, endingOffset, new CSSCommentInfo(false, false));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterNodeRewriter#createCommentNode(com.aptana.formatter.IFormatterDocument,
	 * int, int, java.lang.Object)
	 */
	@Override
	protected IFormatterNode createCommentNode(IFormatterDocument document, int startOffset, int endOffset,
			Object object)
	{
		CSSCommentInfo info = (CSSCommentInfo) object;

		return new FormatterCSSCommentNode(document, startOffset, endOffset, info.isMultiLine, info.isFirstLine);
	}

	private class CSSCommentInfo
	{
		final boolean isMultiLine;
		final boolean isFirstLine;

		CSSCommentInfo(boolean isMultiLine, boolean isFirstLine)
		{
			this.isMultiLine = isMultiLine;
			this.isFirstLine = isFirstLine;
		}

	}

}