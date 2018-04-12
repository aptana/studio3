/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

public class CompositeParserScanner extends Scanner
{

	private CompositeTokenScanner fTokenScanner;
	private IDocument fDocument;

	public CompositeParserScanner(CompositeTokenScanner tokenScanner)
	{
		fTokenScanner = tokenScanner;
	}

	public CompositeTokenScanner getTokenScanner()
	{
		return fTokenScanner;
	}

	public IDocument getSource()
	{
		return fDocument;
	}

	public void setSource(String text)
	{
		setSource(new Document(text));
	}

	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());
	}

	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = fTokenScanner.nextToken();
		while (isIgnored(token))
		{
			token = fTokenScanner.nextToken();
		}

		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();
		if (token.isEOF())
		{
			return createSymbol(offset, offset, "", token); //$NON-NLS-1$
		}

		try
		{
			String text = fDocument.get(offset, length);
			return createSymbol(offset, offset + length - 1, text, token);
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	protected Symbol createSymbol(int start, int end, String text, IToken token)
	{
		return new Symbol((short) 0, start, end, text);
	}

	protected boolean isIgnored(IToken token)
	{
		// by default ignores whitespace
		return token.isWhitespace();
	}
}
