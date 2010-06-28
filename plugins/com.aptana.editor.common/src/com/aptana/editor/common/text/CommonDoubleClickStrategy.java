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
package com.aptana.editor.common.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

public class CommonDoubleClickStrategy implements ITextDoubleClickStrategy
{

	protected ITextViewer fText;

	private boolean fCtrlDown;

	public void doubleClicked(ITextViewer part)
	{
		int pos = part.getSelectedRange().x;
		if (pos < 0)
			return;
		fText = part;
		if (fCtrlDown)
		{
			if (!selectComment(pos))
			{
				selectWord(pos);
			}
		}
		else
		{
			selectWord(pos);
		}
	}

	protected boolean selectComment(int caretPos)
	{
		IDocument doc = fText.getDocument();
		int startPos, endPos;

		try
		{
			int pos = caretPos;
			char c = ' ';
			while (pos >= 0)
			{
				c = doc.getChar(pos);
				if (c == '\\')
				{
					pos -= 2;
					continue;
				}
				if (c == Character.LINE_SEPARATOR || c == '\"')
					break;
				--pos;
			}
			if (c != '\"')
				return false;

			startPos = pos;
			pos = caretPos;
			int length = doc.getLength();
			c = ' ';
			while (pos < length)
			{
				c = doc.getChar(pos);
				if (c == Character.LINE_SEPARATOR || c == '\"')
					break;
				++pos;
			}
			if (c != '\"')
				return false;

			endPos = pos;
			int offset = startPos + 1;
			int len = endPos - offset;
			fText.setSelectedRange(offset, len);
			return true;
		}
		catch (BadLocationException x)
		{
		}
		return false;
	}

	protected boolean selectWord(int caretPos)
	{
		IDocument doc = fText.getDocument();
		int startPos, endPos;
		try
		{
			int pos = caretPos;
			char c;
			while (pos >= 0)
			{
				c = doc.getChar(pos);
				if (!isIdentifierPart(c))
					break;
				--pos;
			}

			startPos = pos;
			pos = caretPos;
			int length = doc.getLength();
			while (pos < length)
			{
				c = doc.getChar(pos);
				if (!isIdentifierPart(c))
					break;
				++pos;
			}
			endPos = pos;
			selectRange(startPos, endPos);
			return true;

		}
		catch (BadLocationException x)
		{
		}
		return false;
	}

	protected boolean isIdentifierPart(char c)
	{
		return Character.isJavaIdentifierPart(c);
	}

	private void selectRange(int startPos, int stopPos)
	{
		int offset = startPos + 1;
		int length = stopPos - offset;
		fText.setSelectedRange(offset, length);
	}
}
