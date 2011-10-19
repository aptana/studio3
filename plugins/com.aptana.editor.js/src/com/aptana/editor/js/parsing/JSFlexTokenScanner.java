/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import beaver.Symbol;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.parsing.lexer.JSTokenType;

/**
 * An adapter class so JSFlexScanner can be used as an ITokenScanner
 */
public class JSFlexTokenScanner implements ITokenScanner
{
	private JSFlexScanner _scanner;
	private int _offset;

	/**
	 * JSFlexTokenScanner
	 */
	public JSFlexTokenScanner()
	{
		_scanner = new JSFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length)
	{
		String source;

		try
		{
			source = document.get(offset, length);
		}
		catch (BadLocationException e)
		{
			source = StringUtil.EMPTY;
		}

		_scanner.setSource(source);
		_offset = offset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
	 */
	public IToken nextToken()
	{
		IToken result = Token.EOF;

		try
		{
			Symbol token = _scanner.nextToken();

			if (token != null)
			{
				result = new Token(JSTokenType.get(token.getId()));
			}
		}
		catch (Throwable e)
		{
			// ignore error
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenOffset()
	 */
	public int getTokenOffset()
	{
		Symbol lastToken = _scanner.getLastToken();

		return (lastToken != null) ? lastToken.getStart() + _offset : _offset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenLength()
	 */
	public int getTokenLength()
	{
		Symbol lastToken = _scanner.getLastToken();

		return (lastToken != null) ? (lastToken.getEnd() - lastToken.getStart() + 1) : 0;
	}
}
