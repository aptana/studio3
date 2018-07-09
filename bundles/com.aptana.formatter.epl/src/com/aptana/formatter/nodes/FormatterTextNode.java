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

import java.io.StringWriter;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

public class FormatterTextNode extends AbstractFormatterNode implements IFormatterTextNode
{

	private final int startOffset;
	private final int endOffset;

	/**
	 * @param text
	 */
	public FormatterTextNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document);
		if (startOffset < 0)
		{
			throw new IllegalArgumentException("Formatter error: startOffset < 0: " + startOffset); //$NON-NLS-1$
		}
		if (startOffset > endOffset)
		{
			throw new IllegalArgumentException("Formatter error: startOffset > endOffset"); //$NON-NLS-1$
		}
		if (endOffset > document.getLength()) {
			throw new IllegalArgumentException("Formatter error: endOffset(" + endOffset + ") > document length (" + document.getLength() + ")"); //$NON-NLS-1$
		}
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	public String getText()
	{
		return getDocument().get(startOffset, endOffset);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		if (shouldConsumePreviousWhiteSpaces() && getSpacesCountBefore() > 0)
		{
			writeSpaces(visitor, context, getSpacesCountBefore());
		}
		visitor.write(context, getStartOffset(), getEndOffset());
		// Write any spaces after writing the text
		if (getSpacesCountAfter() > 0)
		{
			writeSpaces(visitor, context, getSpacesCountAfter());
		}
	}

	public boolean isEmpty()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getEndOffset()
	 */
	public int getEndOffset()
	{
		return endOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getStartOffset()
	 */
	public int getStartOffset()
	{
		return startOffset;
	}

	public String toString()
	{
		final StringWriter w = new StringWriter();
		escapeJavaStyleString(w, getText());
		return w.toString();
	}

	private static void escapeJavaStyleString(StringWriter out, String str)
	{
		if (str == null)
		{
			return;
		}
		int sz;
		sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			char ch = str.charAt(i);
			if (ch > 0xfff)
			{
				out.write("\\u" + hex(ch)); //$NON-NLS-1$
			}
			else if (ch > 0xff)
			{
				out.write("\\u0" + hex(ch)); //$NON-NLS-1$
			}
			else if (ch > 0x7f)
			{
				out.write("\\u00" + hex(ch)); //$NON-NLS-1$
			}
			else if (ch < 32)
			{
				switch (ch)
				{
					case '\b':
						out.write('\\');
						out.write('b');
						break;
					case '\n':
						out.write('\\');
						out.write('n');
						break;
					case '\t':
						out.write('\\');
						out.write('t');
						break;
					case '\f':
						out.write('\\');
						out.write('f');
						break;
					case '\r':
						out.write('\\');
						out.write('r');
						break;
					default:
						if (ch > 0xf)
						{
							out.write("\\u00" + hex(ch)); //$NON-NLS-1$
						}
						else
						{
							out.write("\\u000" + hex(ch)); //$NON-NLS-1$
						}
						break;
				}
			}
			else
			{
				switch (ch)
				{
					case '\\':
						out.write('\\');
						out.write('\\');
						break;
					default:
						out.write(ch);
						break;
				}
			}
		}
	}

	private static String hex(char ch)
	{
		return Integer.toHexString(ch).toUpperCase();
	}

}
