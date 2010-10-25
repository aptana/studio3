/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.parsing.lexer;

public class Lexeme<T> implements ILexeme
{
	private String _text;
	private int _startingOffset;
	private int _endingOffset;
	private T _type;

	/**
	 * Lexeme
	 * 
	 * @param startingOffset
	 * @param endingOffset
	 * @param text
	 */
	public Lexeme(T type, int startingOffset, int endingOffset, String text)
	{
		this._type = type;
		this._startingOffset = startingOffset;
		this._endingOffset = endingOffset;
		this._text = text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#contains(int)
	 */
	public boolean contains(int offset)
	{
		return getStartingOffset() <= offset && offset <= getEndingOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		return this._endingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		if (this._text != null)
		{
			return this._text.length();
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return this._startingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ILexeme#getText()
	 */
	public String getText()
	{
		return this._text;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public T getType()
	{
		return this._type;
	}

	/**
	 * areContiguous
	 * 
	 * @param firstLexeme
	 * @param secondLexeme
	 * @return
	 */
	public boolean isContiguousWith(Lexeme<T> secondLexeme)
	{
		boolean result = true;

		if (secondLexeme != null)
		{
			result = this.getEndingOffset() + 1 == secondLexeme.getStartingOffset();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this._endingOffset < this._startingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		String type = this._type.toString();

		buffer.append(type);
		buffer.append(" ["); //$NON-NLS-1$
		buffer.append(this.getStartingOffset()).append("-").append(this.getEndingOffset()); //$NON-NLS-1$
		buffer.append(",").append(this.getText()); //$NON-NLS-1$
		buffer.append("]"); //$NON-NLS-1$

		return buffer.toString();
	}
}
