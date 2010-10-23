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
 *     Aptana, inc. - Improvements
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.internal;

import java.util.Iterator;

import org.jrubyparser.SourcePosition;
import org.jrubyparser.ast.CommentNode;
import org.jrubyparser.parser.ParserResult;

import com.aptana.editor.ruby.formatter.internal.nodes.FormatterRubyCommentNode;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterNodeRewriter;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;

/**
 * RubyFormatterNodeRewriter
 */
public class RubyFormatterNodeRewriter extends FormatterNodeRewriter
{
	/**
	 * Constructs a new Ruby formatter code rewriter.
	 * 
	 * @param result
	 */
	public RubyFormatterNodeRewriter(ParserResult result)
	{
		for (Iterator<CommentNode> i = result.getCommentNodes().iterator(); i.hasNext();)
		{
			CommentNode commentNode = i.next();
			SourcePosition position = commentNode.getPosition();
			addComment(position.getStartOffset(), position.getEndOffset(), commentNode);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterNodeRewriter#rewrite(com.aptana.formatter.nodes.IFormatterContainerNode)
	 */
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
	protected IFormatterNode createCommentNode(IFormatterDocument document, int startOffset, int endOffset,
			Object object)
	{
		return new FormatterRubyCommentNode(document, startOffset, endOffset);
	}

}
