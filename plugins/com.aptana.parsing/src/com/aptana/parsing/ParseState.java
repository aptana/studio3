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
package com.aptana.parsing;

import com.aptana.parsing.ast.IParseNode;

public class ParseState implements IParseState
{

	private static final char[] NO_CHARS = new char[0];

	private char[] fSource;
	private char[] fInsertedText;
	private int fStartingOffset;
	private int fRemovedLength;

	// represents the root node of the parsing result
	private IParseNode fParseResult;

	public ParseState()
	{
		fSource = NO_CHARS;
		fInsertedText = NO_CHARS;
	}

	public void clearEditState()
	{
		fInsertedText = NO_CHARS;
		fRemovedLength = 0;
	}

	public IParseNode getParseResult()
	{
		return fParseResult;
	}

	public char[] getInsertedText()
	{
		return fInsertedText;
	}

	public int getRemovedLength()
	{
		return fRemovedLength;
	}

	public char[] getSource()
	{
		return fSource;
	}

	public int getStartingOffset()
	{
		return fStartingOffset;
	}

	public void setEditState(String source, String insertedText, int startingOffset, int removedLength)
	{
		fSource = (source != null) ? source.toCharArray() : NO_CHARS;
		fInsertedText = (insertedText != null) ? insertedText.toCharArray() : NO_CHARS;
		fStartingOffset = startingOffset;
		fRemovedLength = removedLength;
	}

	public void setParseResult(IParseNode result)
	{
		fParseResult = result;
	}

	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("@").append(fStartingOffset); //$NON-NLS-1$

		if (fRemovedLength > 0)
		{
			text.append(":r").append(fRemovedLength); //$NON-NLS-1$
		}

		int insertedLength = fInsertedText.length;
		if (insertedLength > 0)
		{
			text.append(":i").append(insertedLength).append(":").append(fInsertedText); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			// outputs closing delimiter for proper parsing of the offset
			text.append(":"); //$NON-NLS-1$
		}

		return text.toString();
	}
}
