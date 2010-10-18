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

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterTextNode;

public class FormatterStringNode extends FormatterTextNode
{

	/**
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 */
	public FormatterStringNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		visitor.ensureLineStarted(context);
		IFormatterContext strContext = context.copy();
		strContext.setIndenting(false);
		visitor.write(strContext, getStartOffset(), getEndOffset());
	}

}
