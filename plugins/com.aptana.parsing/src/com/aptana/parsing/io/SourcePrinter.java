/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.io;

/**
 * @author Kevin Lindsey
 */
public class SourcePrinter
{
	private static final String DEFAULT_NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$

	private StringBuffer _buffer;
	private String _indentText;
	private String _currentIndent;
	private String _newLine;

	/**
	 * SourcePrinter
	 */
	public SourcePrinter()
	{
		this("  "); //$NON-NLS-1$
	}

	/**
	 * SourcePrinter
	 * 
	 * @param indent
	 */
	public SourcePrinter(String indent)
	{
		this._buffer = new StringBuffer();
		this._indentText = indent; //$NON-NLS-1$
		this._currentIndent = ""; //$NON-NLS-1$
		this._newLine = DEFAULT_NEWLINE;
	}
	
	/**
	 * Decrease the current line indent count
	 * 
	 * @return SourceWriter
	 */
	public SourcePrinter decreaseIndent()
	{
		if (this._currentIndent.length() > 0)
		{
			int currentLength = this._currentIndent.length();
			int indentTextLength = this._indentText.length();

			this._currentIndent = this._currentIndent.substring(0, currentLength - indentTextLength);
		}

		return this;
	}

	/**
	 * Gets the buffer of this source writer
	 * 
	 * @return - string buffer
	 */
	public StringBuffer getBuffer()
	{
		return this._buffer;
	}

	/**
	 * Gets the current indent level
	 * 
	 * @return - int indent level
	 */
	public int getIndentLevel()
	{
		if (this._indentText.length() == 0)
		{
			return 0;
		}
		else
		{
			return this._currentIndent.length() / this._indentText.length();
		}
	}

	/**
	 * @return current indent
	 */
	public String getIndentString()
	{
		return this._currentIndent;
	}

	/**
	 * @return current line delimeter
	 */
	public String getLineDelimeter()
	{
		return this._newLine;
	}

	/**
	 * Increase the current line indent count
	 * 
	 * @return SourceWriter
	 */
	public SourcePrinter increaseIndent()
	{
		this._currentIndent += this._indentText;

		return this;
	}

	/**
	 * Add some text to the current source
	 * 
	 * @param c
	 * @return Returns self
	 */
	public SourcePrinter print(char c)
	{
		this._buffer.append(c);

		return this;
	}

	/**
	 * Add some text to the current source
	 * 
	 * @param text
	 * @return Returns self
	 */
	public SourcePrinter print(String text)
	{
		this._buffer.append(text);

		return this;
	}

	/**
	 * print
	 * 
	 * @param object
	 * @return
	 */
	public SourcePrinter print(Object object)
	{
		this._buffer.append(object);

		return this;
	}

	/**
	 * Output the current indent text
	 * 
	 * @return Returns self
	 */
	public SourcePrinter printIndent()
	{
		this._buffer.append(this._currentIndent);

		return this;
	}

	/**
	 * Add an empty line to the current source
	 * 
	 * @return Returns self
	 */
	public SourcePrinter println()
	{
		this.println(""); //$NON-NLS-1$

		return this;
	}

	/**
	 * Add a line of text to the current source
	 * 
	 * @param text
	 *            The text to append to this buffer
	 * @return Returns self
	 */
	public SourcePrinter println(String text)
	{
		this._buffer.append(text).append(_newLine);

		return this;
	}

	/**
	 * Add the current indent text and then a line of text to the current source
	 * 
	 * @param text
	 * @return Returns self
	 */
	public SourcePrinter printlnWithIndent(String text)
	{
		this._buffer.append(this._currentIndent).append(text).append(_newLine);

		return this;
	}

	/**
	 * Output the current indent text and then the specified text
	 * 
	 * @param text
	 * @return Returns self
	 */
	public SourcePrinter printWithIndent(String text)
	{
		this._buffer.append(this._currentIndent).append(text);

		return this;
	}

	/**
	 * @param separator
	 */
	public void setLineDelimeter(String separator)
	{
		this._newLine = separator;
	}

	/**
	 * Return the accumulated source from this source writer
	 * 
	 * @return Returns a string representation of this object
	 */
	public String toString()
	{
		return this._buffer.toString();
	}
}
