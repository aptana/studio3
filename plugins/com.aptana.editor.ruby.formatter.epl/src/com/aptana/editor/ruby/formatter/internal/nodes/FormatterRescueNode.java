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
package com.aptana.editor.ruby.formatter.internal.nodes;

import com.aptana.editor.ruby.formatter.RubyFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.IFormatterTextNode;

public class FormatterRescueNode extends FormatterBlockWithBeginNode
{

	/**
	 * @param document
	 */
	public FormatterRescueNode(IFormatterDocument document)
	{
		super(document);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		IFormatterTextNode begin = getBegin();
		if (begin != null)
		{
			final boolean indenting = isIndenting();
			if (indenting)
			{
				context.decIndent();
			}
			visitor.write(context, begin.getStartOffset(), begin.getEndOffset());
			if (indenting)
			{
				context.incIndent();
			}
		}
		acceptBody(context, visitor);
	}

	protected boolean isIndenting()
	{
		return getDocument().getBoolean(RubyFormatterConstants.INDENT_BLOCKS);
	}

}
