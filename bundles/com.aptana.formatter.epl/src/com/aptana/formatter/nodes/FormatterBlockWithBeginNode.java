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

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

public abstract class FormatterBlockWithBeginNode extends FormatterBlockNode
{

	/**
	 * @param document
	 */
	public FormatterBlockWithBeginNode(IFormatterDocument document)
	{
		super(document);
	}

	private IFormatterTextNode begin;

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		if (shouldConsumePreviousWhiteSpaces() && getSpacesCountBefore() > 0)
		{
			writeSpaces(visitor, context, getSpacesCountBefore());
		}
		int blankLines = context.getBlankLines();
		if (blankLines > 0)
		{
			visitor.ensureLineStarted(context);
		}
		context.setBlankLines(getBlankLinesBefore(context));
		boolean addingNewLine = isAddingBeginNewLine();
		if (addingNewLine && !visitor.isInBlankLine())
		{
			// Add a new line in case the end should be pre-pended with a new line and the previous node did not add
			// a new-line.
			visitor.writeLineBreak(context);
		}
		if (begin != null)
		{
			begin.accept(context, visitor);
		}
		context.resetBlankLines();
		final boolean indenting = isIndenting();
		if (indenting)
		{
			context.incIndent();
		}
		boolean endWithNewLine = isAddingEndNewLine();
		if (endWithNewLine && !visitor.endsWithNewLine())
		{
			// Add a new line in case the end should be pre-pended with a new line and the previous node did not add
			// a new-line.
			visitor.writeLineBreak(context);
		}
		super.accept(context, visitor);

		// Write any spaces after visiting the body
		if (getSpacesCountAfter() > 0)
		{
			writeSpaces(visitor, context, getSpacesCountAfter());
		}

		// de-dent if needed
		if (indenting)
		{
			context.decIndent();
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
	public IFormatterTextNode getBegin()
	{
		return begin;
	}

	/**
	 * @param begin
	 *            the begin to set
	 */
	public void setBegin(IFormatterTextNode begin)
	{
		this.begin = begin;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#getStartOffset()
	 */
	public int getStartOffset()
	{
		if (begin != null)
		{
			return begin.getStartOffset();
		}
		return super.getStartOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#getEndOffset()
	 */
	public int getEndOffset()
	{
		if (!super.isEmpty())
		{
			return super.getEndOffset();
		}
		if (begin != null)
		{
			return begin.getEndOffset();
		}
		return DEFAULT_OFFSET;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isEmpty()
	 */
	public boolean isEmpty()
	{
		return begin == null && super.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#getChildren()
	 */
	public List<IFormatterNode> getChildren()
	{
		if (begin == null)
		{
			return super.getChildren();
		}
		else
		{
			List<IFormatterNode> result = new ArrayList<IFormatterNode>();
			if (begin != null)
			{
				result.add(begin);
			}
			result.addAll(super.getChildren());
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#toString()
	 */
	public String toString()
	{
		return begin + "\n" + super.toString(); //$NON-NLS-1$
	}

}
