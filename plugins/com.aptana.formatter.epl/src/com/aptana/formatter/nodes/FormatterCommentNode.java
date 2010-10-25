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

/**
 * Base class for formatter comment nodes.
 */
public abstract class FormatterCommentNode extends FormatterTextNode
{

	public FormatterCommentNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		boolean currentCommentState = context.isComment();
		context.setComment(true);
		if (getDocument().getBoolean(getWrappingKey()))
		{
			final boolean savedWrapping = context.isWrapping();
			context.setWrapping(true);
			visitor.write(context, getStartOffset(), getEndOffset());
			context.setWrapping(savedWrapping);
		}
		else
		{
			visitor.write(context, getStartOffset(), getEndOffset());
		}
		context.setComment(currentCommentState);
	}

	/**
	 * Returns the key for the boolean 'Wrapping' value that is stored in the document.
	 * 
	 * @return A key that will be used to retrieve the boolean value for the wrapping
	 */
	public abstract String getWrappingKey();
}
