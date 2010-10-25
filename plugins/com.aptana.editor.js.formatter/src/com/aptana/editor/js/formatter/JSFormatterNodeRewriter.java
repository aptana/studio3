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
package com.aptana.editor.js.formatter;

import com.aptana.editor.js.formatter.nodes.FormatterJSCommentNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterNodeRewriter;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

/**
 * JavaScript Formatter node rewriter
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterNodeRewriter extends FormatterNodeRewriter
{
	private static final String MULTI_LINE_COMMENT_PREFIX = "/*"; //$NON-NLS-1$

	/**
	 * Constructs a new JSFormatterNodeRewriter
	 * 
	 * @param parseResultRoot
	 */
	public JSFormatterNodeRewriter(IParseRootNode parseResultRoot, FormatterDocument document)
	{
		IParseNode[] comments = parseResultRoot.getCommentNodes();
		insertComments(document, comments);
	}

	private void insertComments(FormatterDocument document, IParseNode[] comments)
	{
		for (IParseNode node : comments)
		{
			// in case we have a multi-line block comment, we actually break the comment to its lines and
			// create a comment node for each line.
			int startingOffset = node.getStartingOffset();
			int endingOffset = node.getEndingOffset() + 1;
			String commentText = document.get(startingOffset, endingOffset);
			if (commentText.startsWith(MULTI_LINE_COMMENT_PREFIX))
			{
				// Push each line as a comment. Mark the first line as a 'first'.
				// Since we have to maintain the correct offsets, we have to scan manually.We will consume any new line
				// markers in this process, so any empty lines will be consumed.
				int length = 0;
				boolean first = true;
				boolean foundFirstNonWhite = false;
				int nextLineStartOffset = startingOffset;
				int commentEndOffset = nextLineStartOffset;
				for (int commentOffset = 0; commentOffset < commentText.length(); commentOffset++)
				{
					char c = commentText.charAt(commentOffset);
					if ((c == '\n' || c == '\r') && length > 0)
					{
						commentEndOffset = commentOffset + startingOffset;
						addComment(nextLineStartOffset, commentEndOffset, new JSCommentInfo(true, first));
						nextLineStartOffset = commentEndOffset + 1;
						length = 0;
						first = false;
						foundFirstNonWhite = false;
					}
					else
					{
						if (c != '\n' && c != '\r')
						{
							if (c == ' ' || c == '\t')
							{
								if (foundFirstNonWhite)
								{
									length++;
								}
								else
								{
									nextLineStartOffset++;
								}
							}
							else
							{
								foundFirstNonWhite = true;
								length++;
							}
						}
						else
						{
							nextLineStartOffset++;
						}
					}
				}
				// Deal with the last line. Make sure that we trim any initial spaces and mark the start where the
				// asterisk char is located.
				int asteriskoffset = startingOffset
						+ commentText.indexOf('*', commentEndOffset - startingOffset + length);
				addComment(asteriskoffset, endingOffset, new JSCommentInfo(true, first));
			}
			else
			{
				addComment(startingOffset, endingOffset, new JSCommentInfo(false, false));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterNodeRewriter#rewrite(com.aptana.formatter.nodes.IFormatterContainerNode)
	 */
	@Override
	public void rewrite(IFormatterContainerNode root)
	{
		super.rewrite(root);
		attachComments(root);
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
		JSCommentInfo info = (JSCommentInfo) object;
		return new FormatterJSCommentNode(document, startOffset, endOffset, info.isMultiLine, info.isFirstLine);
	}

	private class JSCommentInfo
	{
		boolean isMultiLine;
		boolean isFirstLine;

		JSCommentInfo(boolean isMultiLine, boolean isFirstLine)
		{
			this.isMultiLine = isMultiLine;
			this.isFirstLine = isFirstLine;
		}
	}
}
