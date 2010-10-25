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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final Pattern COMMENT_LINE_PATTERN = Pattern.compile("(\\S.*)"); //$NON-NLS-1$
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
				Matcher matcher = COMMENT_LINE_PATTERN.matcher(commentText);
				boolean isFirstLine = true;
				while (matcher.find())
				{
					int start = matcher.start();
					int end = matcher.end();
					addComment(startingOffset + start, startingOffset + end, new JSCommentInfo(true, isFirstLine));
					isFirstLine = false;
				}
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
