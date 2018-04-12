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

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

public abstract class AbstractFormatterNode implements IFormatterNode
{

	private static final String SPACES = "                                                    "; //$NON-NLS-1$
	private final IFormatterDocument document;

	/**
	 * @param document
	 */
	public AbstractFormatterNode(IFormatterDocument document)
	{
		this.document = document;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getDocument()
	 */
	public IFormatterDocument getDocument()
	{
		return document;
	}

	protected String getShortClassName()
	{
		final String name = getClass().getName();
		int index = name.lastIndexOf('.');
		return index > 0 ? name.substring(index + 1) : name;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getShortClassName();
	}

	protected int getInt(String key)
	{
		return document.getInt(key);
	}

	/**
	 * Write spaces.
	 * 
	 * @param visitor
	 * @param context
	 * @param count
	 */
	protected void writeSpaces(IFormatterWriter visitor, IFormatterContext context, int count)
	{
		if (count > 0)
		{
			if (SPACES.length() > count)
			{
				visitor.writeText(context, SPACES.substring(0, count));
			}
			else
			{
				StringBuilder builder = new StringBuilder(SPACES.length() * 2);
				while (builder.length() < count)
				{
					builder.append(SPACES);
				}
				visitor.writeText(context, builder.substring(0, count));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getSpacesCountBefore()
	 */
	public int getSpacesCountBefore()
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getSpacesCountAfter()
	 */
	public int getSpacesCountAfter()
	{
		return 0;
	}
}
