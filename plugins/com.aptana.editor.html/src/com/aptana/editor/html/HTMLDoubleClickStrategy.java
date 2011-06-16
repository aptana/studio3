/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.text.CommonDoubleClickStrategy;

public class HTMLDoubleClickStrategy extends CommonDoubleClickStrategy
{

	@Override
	protected boolean selectWord(int caretPos)
	{
		return selectSpecialOpenTags(caretPos) ? true : super.selectWord(caretPos);
	}

	// Selects '</' and '<!' when they are double clicked
	private boolean selectSpecialOpenTags(int caretPos)
	{
		IDocument doc = fText.getDocument();
		char current, before, after;
		try
		{
			current = doc.getChar(caretPos);
			before = doc.getChar(caretPos - 1);
			after = doc.getChar(caretPos + 1);

			if ((current == '/' || current == '!') && before == '<')
			{
				selectRange(caretPos - 2, caretPos + 1);
				return true;
			}
			else if (current == '<' && (after == '/' || after == '!'))
			{
				selectRange(caretPos - 1, caretPos + 2);
				return true;
			}

		}
		catch (BadLocationException x)
		{
		}
		return false;
	}

}
