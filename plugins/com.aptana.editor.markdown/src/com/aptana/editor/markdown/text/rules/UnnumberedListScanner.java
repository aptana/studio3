/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.markdown.text.rules;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class UnnumberedListScanner extends MarkdownScanner
{
	private boolean fFirstToken;

	@Override
	public IToken nextToken()
	{
		// HACK to avoid matching italics on un-numbered lists
		if (fFirstToken)
		{
			fTokenOffset = fOffset;
			read();
			fFirstToken = false;
			return getToken(""); //$NON-NLS-1$
		}

		IToken returnToken = super.nextToken();
		if (returnToken != null && returnToken.equals(Token.WHITESPACE))
		{
			// Check if it's a newline
			try
			{
				String src = fDocument.get(getTokenOffset(), getTokenLength());
				if (src.endsWith("\n") || src.endsWith("\r")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					fFirstToken = true;
				}
			}
			catch (BadLocationException e)
			{
				// ignore
			}
		}
		return returnToken;
	}

	@Override
	protected void reset()
	{
		fFirstToken = true;
		super.reset();
	}
}