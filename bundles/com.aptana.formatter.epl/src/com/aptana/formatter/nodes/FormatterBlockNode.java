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
package com.aptana.formatter.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

public class FormatterBlockNode extends AbstractFormatterNode implements IFormatterContainerNode
{

	/**
	 * @param document
	 */
	public FormatterBlockNode(IFormatterDocument document)
	{
		super(document);
	}

	private final List<IFormatterNode> body = new ArrayList<IFormatterNode>();

	protected void acceptNodes(final List<IFormatterNode> nodes, IFormatterContext context, IFormatterWriter visitor)
			throws Exception
	{
		for (IFormatterNode node : nodes)
		{
			context.enter(node);
			node.accept(context, visitor);
			context.leave(node);
		}
	}

	public void addChild(IFormatterNode child)
	{
		body.add(child);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		acceptBody(context, visitor);
	}

	protected void acceptBody(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		acceptNodes(body, context, visitor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getEndOffset()
	 */
	public int getEndOffset()
	{
		if (!body.isEmpty())
		{
			return body.get(body.size() - 1).getEndOffset();
		}
		else
		{
			return DEFAULT_OFFSET;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getStartOffset()
	 */
	public int getStartOffset()
	{
		if (!body.isEmpty())
		{
			return body.get(0).getStartOffset();
		}
		else
		{
			return DEFAULT_OFFSET;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#isEmpty()
	 */
	public boolean isEmpty()
	{
		for (IFormatterNode node : body)
		{
			if (!node.isEmpty())
			{
				return false;
			}
		}
		return true;
	}

	public List<IFormatterNode> getChildren()
	{
		return Collections.unmodifiableList(body);
	}

	@Override
	public String toString()
	{
		return body.toString();
	}

	public List<IFormatterNode> getBody()
	{
		return body;
	}

	/**
	 * Returns true if the node is indenting; False, otherwise.<br>
	 * The default implementation is false. Subclasses may override.
	 * 
	 * @return Returns true if the node is indenting; False, otherwise
	 */
	protected boolean isIndenting()
	{
		return false;
	}

	/**
	 * Returns true if the node suppose to add a new line at its beginning; False, otherwise.<br>
	 * The default implementation is false. Subclasses may override.
	 * 
	 * @return Returns true if the node should start with a new line; False, otherwise
	 */
	protected boolean isAddingBeginNewLine()
	{
		return false;
	}

	/**
	 * Returns true if the node suppose to add a new line at its ending; False, otherwise.<br>
	 * The default implementation is false. Subclasses may override.
	 * 
	 * @return Returns true if the node should end with a new line; False, otherwise
	 */
	protected boolean isAddingEndNewLine()
	{
		return false;
	}
}
