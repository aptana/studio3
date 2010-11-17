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
import java.util.List;

import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

public abstract class FormatterBlockWithBeginEndNode extends FormatterBlockNode
{

	/**
	 * @param document
	 */
	public FormatterBlockWithBeginEndNode(IFormatterDocument document)
	{
		super(document);
	}

	protected List<IFormatterNode> begin = null;
	protected IFormatterTextNode end;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#accept(com.aptana.formatter.IFormatterContext,
	 * com.aptana.formatter.IFormatterWriter)
	 */
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		if (shouldConsumePreviousWhiteSpaces() && getSpacesCountBefore() > 0)
		{
			writeSpaces(visitor, context, getSpacesCountBefore());
		}
		context.setBlankLines(getBlankLinesBefore(context));
		boolean beginWithNewLine = isAddingBeginNewLine();
		if (beginWithNewLine && !visitor.endsWithNewLine() && !shouldConsumePreviousWhiteSpaces())
		{
			// Add a new line in case the end should be pre-pended with a new line and the previous node did not add
			// a new-line.
			visitor.writeLineBreak(context);
		}
		if (begin != null)
		{
			for (IFormatterNode element : begin)
			{
				element.accept(context, visitor);
			}
		}
		context.resetBlankLines();
		final boolean indenting = isIndenting();
		if (indenting)
		{
			context.incIndent();
		}

		if (beginWithNewLine)
		{
			visitor.writeLineBreak(context);
		}
		super.accept(context, visitor);
		if (indenting)
		{
			context.decIndent();
		}
		boolean endWithNewLine = isAddingEndNewLine();
		if (endWithNewLine && !visitor.endsWithNewLine())
		{
			// Add a new line in case the end should be pre-pended with a new line and the previous node did not add
			// a new-line.
			visitor.writeLineBreak(context);
		}
		if (end != null)
		{
			visitor.write(context, end.getStartOffset(), end.getEndOffset());
		}
		// For this node, we write the spaces after the 'end' node.
		if (getSpacesCountAfter() > 0)
		{
			writeSpaces(visitor, context, getSpacesCountAfter());
		}
		context.setBlankLines(getBlankLinesAfter(context));
	}

	protected int getBlankLinesBefore(IFormatterContext context)
	{
		return -1;
	}

	protected int getBlankLinesAfter(IFormatterContext context)
	{
		return -1;
	}

	/**
	 * @return the begin
	 */
	public IFormatterNode[] getBegin()
	{
		return FormatterUtils.toTextNodeArray(begin);
	}

	/**
	 * @param begin
	 *            the begin to set
	 */
	public void setBegin(IFormatterTextNode begin)
	{
		if (this.begin == null)
		{
			this.begin = new ArrayList<IFormatterNode>();
		}
		this.begin.add(begin);
	}

	public void insertBefore(List<IFormatterNode> nodes)
	{
		if (this.begin == null)
		{
			this.begin = new ArrayList<IFormatterNode>();
		}
		this.begin.addAll(0, nodes);
	}

	/**
	 * @return the end
	 */
	public IFormatterTextNode getEnd()
	{
		return end;
	}

	/**
	 * @param node
	 */
	public void setEnd(IFormatterTextNode node)
	{
		this.end = node;
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.FormatterBlockNode#getStartOffset()
	 */
	public int getStartOffset()
	{
		if (begin != null)
		{
			return ((IFormatterTextNode) begin.get(0)).getStartOffset();
		}
		return super.getStartOffset();
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.FormatterBlockNode#getEndOffset()
	 */
	public int getEndOffset()
	{
		if (end != null)
		{
			return end.getEndOffset();
		}
		if (!super.isEmpty())
		{
			return super.getEndOffset();
		}
		if (begin != null)
		{
			return ((IFormatterTextNode) begin.get(begin.size() - 1)).getEndOffset();
		}
		return DEFAULT_OFFSET;
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.FormatterBlockNode#isEmpty()
	 */
	public boolean isEmpty()
	{
		return begin == null && end == null && super.isEmpty();
	}

	/*
	 * @see org.eclipse.dltk.formatter.nodes.FormatterBlockNode#getChildren()
	 */
	public List<IFormatterNode> getChildren()
	{
		if (begin == null && end == null)
		{
			return super.getChildren();
		}
		else
		{
			List<IFormatterNode> result = new ArrayList<IFormatterNode>();
			if (begin != null)
			{
				result.addAll(begin);
			}
			result.addAll(super.getChildren());
			if (end != null)
			{
				result.add(end);
			}
			return result;
		}
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.FormatterBlockNode#toString()
	 */
	public String toString()
	{
		return begin + "\n" + super.toString() + "\n" + end; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
