/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.util.Stack;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.JSPlugin;
import com.aptana.parsing.ParseState;

/**
 * JSParseState
 */
public class JSParseState extends ParseState
{
	private static class CommentContext
	{
		boolean collectComments;
		boolean attachComments;

		public CommentContext()
		{
			collectComments = true;
			attachComments = true;
		}

		public CommentContext(CommentContext oldContext)
		{
			collectComments = oldContext.collectComments;
			attachComments = oldContext.attachComments;
		}
	}

	private Stack<CommentContext> commentContentStack;

	/**
	 * JSParseState
	 */
	public JSParseState()
	{
		commentContentStack = new Stack<CommentContext>();

		commentContentStack.push(new CommentContext());
	}

	/**
	 * attachComments
	 * 
	 * @return
	 */
	public boolean attachComments()
	{
		return getCurrentCommentContext().attachComments;
	}

	/**
	 * collectComments
	 * 
	 * @return
	 */
	public boolean collectComments()
	{
		return getCurrentCommentContext().attachComments;
	}

	/**
	 * getCurrentCommentContext
	 * 
	 * @return
	 */
	protected CommentContext getCurrentCommentContext()
	{
		return commentContentStack.peek();
	}

	/**
	 * popCommentText
	 */
	public void popCommentContext()
	{
		if (commentContentStack.size() > 1)
		{
			commentContentStack.pop();
		}
		else
		{
			IdeLog.logError(JSPlugin.getDefault(), "Tried to pop an empty comment context stack in JSParseState"); //$NON-NLS-1$
		}
	}

	/**
	 * pushCommentContext
	 */
	public void pushCommentContext()
	{
		commentContentStack.push(new CommentContext(getCurrentCommentContext()));
	}

	/**
	 * setAttachComments
	 * 
	 * @param flag
	 */
	public void setAttachComments(boolean flag)
	{
		commentContentStack.peek().attachComments = flag;
	}

	/**
	 * setCollectComments
	 * 
	 * @param flag
	 */
	public void setCollectComments(boolean flag)
	{
		commentContentStack.peek().attachComments = flag;
	}
}
