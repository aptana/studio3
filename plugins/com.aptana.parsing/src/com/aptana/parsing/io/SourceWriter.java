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

import java.security.InvalidParameterException;

/**
 * @author Kevin Lindsey
 * @author Pavel Petrochenko
 */
public class SourceWriter
{
	/*
	 * Fields
	 */
	private StringBuffer _buffer;
	private String _indentText;
	private String _currentIndent;
	private static final String DEFAULT_NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$

	private String newLine = DEFAULT_NEWLINE;

	/**
	 * Create a new instance of SourceWriter
	 */
	public SourceWriter()
	{
		this._buffer = new StringBuffer();
		this._indentText = "    "; //$NON-NLS-1$
		this._currentIndent = ""; //$NON-NLS-1$		
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
	 * Gets the current indent text
	 * 
	 * @return - current indent text
	 */
	public String getIndentText()
	{
		return this._currentIndent;
	}

	/**
	 * Create a new instance of SourceWriter
	 * 
	 * @param initialIndent
	 * @param indent
	 * @param tabSize
	 */
	public SourceWriter(int initialIndent, String indent, int tabSize)
	{
		this._buffer = new StringBuffer();
		this._indentText = indent;
		StringBuffer bf = new StringBuffer();
		if (indent.length() == 1 && indent.charAt(0) == '\t')
		{
			if (tabSize == 0)
			{
				tabSize = 1;
			}
			int tabCount = initialIndent / tabSize;
			for (int a = 0; a < tabCount; a++)
			{
				bf.append('\t');
			}
			for (int a = 0; a < initialIndent % tabSize; a++)
			{
				bf.append(' ');
			}
		}
		else
		{
			for (int a = 0; a < initialIndent; a++)
			{
				bf.append(' ');
			}
		}
		this._currentIndent = bf.toString();
	}

	/*
	 * Methods
	 */

	/**
	 * Decrease the current line indent count
	 * 
	 * @return SourceWriter
	 */
	public SourceWriter decreaseIndent()
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
	 * Increase the current line indent count
	 * 
	 * @return SourceWriter
	 */
	public SourceWriter increaseIndent()
	{
		this._currentIndent += this._indentText;

		return this;
	}

	/**
	 * Sets the current indent level. Resets the indent and then increases it once for each level
	 * 
	 * @param level
	 */
	public void setCurrentIndentLevel(int level)
	{
		this._currentIndent = ""; //$NON-NLS-1$
		for (int i = 0; i < level; i++)
		{
			increaseIndent();
		}
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
	 * Create a string by concatenating the elements of a string array using a delimited between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of items to join
	 * @return The resulting string
	 */
	public static String join(String delimiter, String[] items)
	{
		int length = items.length;
		String result = ""; //$NON-NLS-1$

		if (length > 0)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < length - 1; i++)
			{
				sb.append(items[i]).append(delimiter);
			}

			sb.append(items[length - 1]);

			result = sb.toString();
		}

		return result;
	}

	/**
	 * Add some text to the current source
	 * 
	 * @param c
	 * @return Returns self
	 */
	public SourceWriter print(char c)
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
	public SourceWriter print(String text)
	{
		this._buffer.append(text);

		return this;
	}

	/**
	 * @return current offset from nearest new line character
	 */
	public int getCurrentIndentationLevel()
	{
		int pos = 0;
		for (int a = this._buffer.length() - 1; a >= 0; a--)
		{

			char charAt = this._buffer.charAt(a);
			if (charAt == '\n' || charAt == '\r')
			{
				break;
			}
			pos++;
			if (!Character.isWhitespace(charAt))
			{
				pos = 0;
			}
		}
		return pos;
	}

	/**
	 * @return indentation string as it is on previous line;
	 */
	public String getCurrentIndentationString()
	{
		int pos = this._buffer.length();
		int startLine = 0;
		for (int a = this._buffer.length() - 1; a >= 0; a--)
		{

			char charAt = this._buffer.charAt(a);
			if (charAt == '\n' || charAt == '\r')
			{
				startLine = a + 1;
				break;
			}
			if (!Character.isWhitespace(charAt))
			{
				pos = a;
			}
		}
		if (_buffer.length() == 0)
		{
			return ""; //$NON-NLS-1$
		}
		return _buffer.substring(startLine, pos);
	}

	/**
	 * @return current offset from nearest new line character
	 */
	public int getCurrentIndentLevel()
	{
		int pos = 0;
		for (int a = this._buffer.length() - 1; a >= 0; a--)
		{

			char charAt = this._buffer.charAt(a);
			if (charAt == '\n' || charAt == '\r')
			{
				break;
			}
			pos++;
		}
		return pos;
	}

	/**
	 * Output the current indent text and then the specified text
	 * 
	 * @param text
	 * @return Returns self
	 */
	public SourceWriter printWithIndent(String text)
	{
		this._buffer.append(this._currentIndent).append(text);

		return this;
	}

	/**
	 * Output the current indent text
	 * 
	 * @return Returns self
	 */
	public SourceWriter printIndent()
	{
		this._buffer.append(this._currentIndent);

		return this;
	}

	/**
	 * Add an empty line to the current source
	 * 
	 * @return Returns self
	 */
	public SourceWriter println()
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
	public SourceWriter println(String text)
	{
		this._buffer.append(text).append(newLine);

		return this;
	}

	/**
	 * Add the current indent text and then a line of text to the current source
	 * 
	 * @param text
	 * @return Returns self
	 */
	public SourceWriter printlnWithIndent(String text)
	{
		this._buffer.append(this._currentIndent).append(text).append(newLine);

		return this;
	}

	/**
	 * Splice new text into the source array while deleting at the same time, if necessary
	 * 
	 * @param source
	 *            The current source array
	 * @param insertText
	 *            The new character data to insert
	 * @param insertOffset
	 *            The offset within the source where the new data is to be inserted
	 * @param removeLength
	 *            The length of character data that is to be removed from the source before inserting the new data
	 * @return Returns the resulting character array after the splice operation has been performed
	 */
	public static char[] splice(char[] source, char[] insertText, int insertOffset, int removeLength)
	{
		// make sure insert offset is positive
		if (insertOffset < 0)
		{
			throw new InvalidParameterException(Messages.SourceWriter_Offset_Below_Zero + insertOffset);
		}

		// make sure remove length is positive
		if (removeLength < 0)
		{
			throw new InvalidParameterException(Messages.SourceWriter_Remove_Length_Below_Zero + removeLength);
		}

		// make sure we're removing a valid range from the source
		int sourceLength = (source != null) ? source.length : 0;
		int postRemoveIndex = insertOffset + removeLength;

		if (postRemoveIndex > sourceLength)
		{
			throw new InvalidParameterException(Messages.SourceWriter_Remove_Beyond_Length);
		}

		// get insertion text's length
		int insertLength = (insertText != null) ? insertText.length : 0;

		// create resulting character array
		char[] result = new char[sourceLength - removeLength + insertLength];

		// copy front part - everything before insertion point
		if (insertOffset > 0)
		{
			System.arraycopy(source, 0, result, 0, insertOffset);
		}

		// copy middle part
		if (insertLength > 0)
		{
			System.arraycopy(insertText, 0, result, insertOffset, insertLength);
		}

		// copy end part
		if (insertOffset + removeLength < sourceLength)
		{
			System.arraycopy(source, postRemoveIndex, result, insertOffset + insertLength, sourceLength
					- postRemoveIndex);
		}

		return result;
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

	/**
	 * @param separator
	 */
	public void setLineDelimeter(String separator)
	{
		this.newLine = separator;
	}

	/**
	 * @return current line delimeter
	 */
	public String getLineDelimeter()
	{
		return this.newLine;
	}

	/**
	 * @return current indent
	 */
	public String getIndentString()
	{
		return this._currentIndent;
	}

	/**
	 * Gets the length of the buffer
	 * 
	 * @return - length
	 */
	public int getLength()
	{
		return _buffer.length();
	}
}
