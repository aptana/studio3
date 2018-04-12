/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.logging.IdeLog;
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
			// Don't try to match at end of document
			if (caretPos == doc.getLength())
			{
				return false;
			}

			current = doc.getChar(caretPos);

			if (caretPos < doc.getLength() - 1)
			{
				after = doc.getChar(caretPos + 1);
				if (current == '<' && (after == '/' || after == '!'))
				{
					selectRange(caretPos - 1, caretPos + 2);
					return true;
				}
			}

			if (caretPos > 0)
			{
				before = doc.getChar(caretPos - 1);
				if ((current == '/' || current == '!') && before == '<')
				{
					selectRange(caretPos - 2, caretPos + 1);
					return true;
				}
			}

		}
		catch (BadLocationException x)
		{
			if (Platform.inDebugMode())
			{
				IdeLog.logError(HTMLPlugin.getDefault(), x);
			}
		}
		return false;
	}
}
