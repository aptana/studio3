/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

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
		this._indentText = indent;
		this._currentIndent = StringUtil.EMPTY;
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
		this.println(StringUtil.EMPTY);

		return this;
	}

	/**
	 * Add a line of text to the current source
	 * 
	 * @param text
	 *            The text to append to this buffer
	 * @return Returns self
	 */
	public SourcePrinter println(char text)
	{
		this._buffer.append(text).append(_newLine);

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
	public SourcePrinter printlnWithIndent(char text)
	{
		this._buffer.append(this._currentIndent).append(text).append(_newLine);

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
	public SourcePrinter printWithIndent(char text)
	{
		this._buffer.append(this._currentIndent).append(text);

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
