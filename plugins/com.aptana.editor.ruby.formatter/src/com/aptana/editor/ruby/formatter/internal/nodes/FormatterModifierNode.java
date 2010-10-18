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
import com.aptana.formatter.nodes.FormatterBlockNode;

public class FormatterModifierNode extends FormatterBlockNode
{

	/**
	 * @param document
	 */
	public FormatterModifierNode(IFormatterDocument document)
	{
		super(document);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		final boolean indenting = isIndenting();
		if (indenting)
		{
			context.incIndent();
		}
		super.accept(context, visitor);
		if (indenting)
		{
			context.decIndent();
		}
	}

	protected boolean isIndenting()
	{
		return getDocument().getBoolean(RubyFormatterConstants.INDENT_BLOCKS);
	}

}
