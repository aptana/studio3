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

import org.eclipse.jface.text.IRegion;

import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.IFormatterCallback;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterRawWriter;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterTextNode;

public class FormatterHereDocNode extends FormatterTextNode implements IFormatterCallback
{

	private final boolean indent;
	private IRegion contentRegion;
	private IRegion endMarkerRegion;

	/**
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 */
	public FormatterHereDocNode(IFormatterDocument document, int startOffset, int endOffset, boolean indent)
	{
		super(document, startOffset, endOffset);
		this.indent = indent;
	}

	/**
	 * @return the contentRegion
	 */
	public IRegion getContentRegion()
	{
		return contentRegion;
	}

	/**
	 * @param contentRegion
	 *            the contentRegion to set
	 */
	public void setContentRegion(IRegion contentRegion)
	{
		this.contentRegion = contentRegion;
	}

	/**
	 * @return the endMarkerRegion
	 */
	public IRegion getEndMarkerRegion()
	{
		return endMarkerRegion;
	}

	/**
	 * @param endMarkerRegion
	 *            the endMarkerRegion to set
	 */
	public void setEndMarkerRegion(IRegion endMarkerRegion)
	{
		this.endMarkerRegion = endMarkerRegion;
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		IFormatterContext heredocContext = context.copy();
		heredocContext.setIndenting(false);
		visitor.write(heredocContext, getStartOffset(), getEndOffset());
		if (contentRegion != null)
		{
			visitor.excludeRegion(contentRegion);
		}
		if (endMarkerRegion != null)
		{
			visitor.excludeRegion(endMarkerRegion);
		}
		visitor.addNewLineCallback(this);
	}

	public void call(IFormatterContext context, IFormatterRawWriter writer)
	{
		final IFormatterDocument doc = getDocument();
		if (contentRegion != null && contentRegion.getLength() > 0)
		{
			writer.writeText(context, doc.get(contentRegion));
		}
		if (endMarkerRegion != null)
		{
			final String endMarker = doc.get(endMarkerRegion);
			if (indent)
			{
				writer.writeIndent(context);
				int i = 0;
				while (i < endMarker.length() && FormatterUtils.isSpace(endMarker.charAt(i)))
				{
					++i;
				}
				writer.writeText(context, endMarker.substring(i));
			}
			else
			{
				writer.writeText(context, endMarker);
			}
		}
	}

	/**
	 * @return the indent
	 */
	public boolean isIndent()
	{
		return indent;
	}

}
