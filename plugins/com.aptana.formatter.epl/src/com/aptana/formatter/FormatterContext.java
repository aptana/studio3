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
 *******************************************************************************/
package com.aptana.formatter;

import java.util.ArrayList;
import java.util.List;

import com.aptana.core.logging.IdeLog;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.nodes.IFormatterNode;

public abstract class FormatterContext implements IFormatterContext, Cloneable
{

	private static class PathEntry
	{
		final IFormatterNode node;
		int childIndex = 0;

		/**
		 * @param node
		 */
		public PathEntry(IFormatterNode node)
		{
			this.node = node;
		}

	}

	private int indent;
	private boolean indenting = true;
	private boolean comment = false;
	private boolean wrapping = false;
	private int blankLines = 0;
	private final List<PathEntry> path = new ArrayList<PathEntry>();

	public FormatterContext(int indent)
	{
		this.indent = indent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#copy()
	 */
	public IFormatterContext copy()
	{
		try
		{
			return (IFormatterContext) clone();
		}
		catch (CloneNotSupportedException e)
		{
			IdeLog.logError(FormatterPlugin.getDefault(), "FormatterContext.copy() error", e, IDebugScopes.DEBUG); //$NON-NLS-1$
			throw new IllegalStateException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#decIndent()
	 */
	public void decIndent()
	{
		--indent;
		// TODO assert indent >= 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#incIndent()
	 */
	public void incIndent()
	{
		++indent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#resetIndent()
	 */
	public void resetIndent()
	{
		indent = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#setIndent(int)
	 */
	public void setIndent(int indent)
	{
		this.indent = indent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#getIndent()
	 */
	public int getIndent()
	{
		return indent;
	}

	public boolean isIndenting()
	{
		return indenting;
	}

	public void setIndenting(boolean value)
	{
		this.indenting = value;
	}

	public boolean isComment()
	{
		return comment;
	}

	public void setComment(boolean value)
	{
		this.comment = value;
	}

	public int getBlankLines()
	{
		return blankLines;
	}

	public void resetBlankLines()
	{
		blankLines = -1;
	}

	public void setBlankLines(int value)
	{
		if (value >= 0 && value > blankLines)
		{
			blankLines = value;
		}
	}

	public void enter(IFormatterNode node)
	{
		path.add(new PathEntry(node));
	}

	public void leave(IFormatterNode node)
	{
		final PathEntry entry = path.remove(path.size() - 1);
		if (entry.node != node)
		{
			throw new IllegalStateException("leave() - node mismatch"); //$NON-NLS-1$
		}
		if (!path.isEmpty() && isCountable(node))
		{
			final PathEntry parent = path.get(path.size() - 1);
			++parent.childIndex;
		}
	}

	protected boolean isCountable(IFormatterNode node)
	{
		return true;
	}

	public IFormatterNode getParent()
	{
		if (path.size() > 1)
		{
			final PathEntry entry = path.get(path.size() - 2);
			return entry.node;
		}
		else
		{
			return null;
		}
	}

	public int getChildIndex()
	{
		if (path.size() > 1)
		{
			final PathEntry entry = path.get(path.size() - 2);
			return entry.childIndex;
		}
		else
		{
			return -1;
		}
	}

	public boolean isWrapping()
	{
		return wrapping;
	}

	public void setWrapping(boolean value)
	{
		this.wrapping = value;
	}
}
