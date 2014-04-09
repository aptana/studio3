/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

	private boolean selectComment(int caretPos)
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

	protected void selectRange(int startPos, int stopPos)
	{
		int offset = startPos + 1;
		int length = stopPos - offset;
		fText.setSelectedRange(offset, length);
	}
}
