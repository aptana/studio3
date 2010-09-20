/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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
import com.aptana.ui.util.Util;

public class FormatterEmptyNode extends AbstractFormatterNode implements IFormatterTextNode
{

	private final int offset;

	/**
	 * @param text
	 */
	public FormatterEmptyNode(IFormatterDocument document, int offset)
	{
		super(document);
		this.offset = offset;
	}

	public String getText()
	{
		return Util.EMPTY_STRING;
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		visitor.write(context, offset, offset);
	}

	public boolean isEmpty()
	{
		return false;
	}

	public int getEndOffset()
	{
		return offset;
	}

	public int getStartOffset()
	{
		return offset;
	}

	@Override
	public String toString()
	{
		return Util.EMPTY_STRING;
	}

}
