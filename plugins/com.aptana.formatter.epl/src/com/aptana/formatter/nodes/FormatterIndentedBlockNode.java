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

public class FormatterIndentedBlockNode extends FormatterBlockNode
{

	private final boolean indenting;

	public FormatterIndentedBlockNode(IFormatterDocument document, boolean indenting)
	{
		super(document);
		this.indenting = indenting;
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		if (isIndenting())
		{
			context.incIndent();
		}
		super.accept(context, visitor);
		if (isIndenting())
		{
			context.decIndent();
		}
	}

	protected boolean isIndenting()
	{
		return indenting;
	}

}
