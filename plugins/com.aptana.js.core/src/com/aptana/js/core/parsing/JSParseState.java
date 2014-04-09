/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.util.Stack;

import com.aptana.core.logging.IdeLog;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.parsing.IParseStateCacheKey;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParseStateCacheKeyWithComments;

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
	 * In this constructor, startingOffset is considered 0 and comments won't be attached nor collected.
	 */
	public JSParseState(String source)
	{
		this(source, 0, false, false);
	}

	/**
	 * JSParseState
	 */
	public JSParseState(String source, int startingOffset, boolean attachComments, boolean collectComments)
	{
		super(source, startingOffset);
		commentContentStack = new Stack<CommentContext>();

		commentContentStack.push(new CommentContext());
		setAttachComments(attachComments);
		setCollectComments(collectComments);
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
		return getCurrentCommentContext().collectComments;
	}

	/**
	 * getCurrentCommentContext
	 * 
	 * @return
	 */
	private CommentContext getCurrentCommentContext()
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
			IdeLog.logError(JSCorePlugin.getDefault(), "Tried to pop an empty comment context stack in JSParseState"); //$NON-NLS-1$
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
	private void setAttachComments(boolean flag)
	{
		commentContentStack.peek().attachComments = flag;
	}

	/**
	 * setCollectComments
	 * 
	 * @param flag
	 */
	private void setCollectComments(boolean flag)
	{
		commentContentStack.peek().attachComments = flag;
	}

	@Override
	public IParseStateCacheKey getCacheKey(String contentTypeId)
	{
		IParseStateCacheKey cacheKey = super.getCacheKey(contentTypeId);
		return new ParseStateCacheKeyWithComments(attachComments(), collectComments(), cacheKey);
	}

}
