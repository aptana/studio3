/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class CSSScanner extends Scanner
{
	private CSSTokenScanner fTokenScanner;
	private IDocument fDocument;
	private List<IRange> fComments;

	/**
	 * CSSScanner
	 */
	public CSSScanner()
	{
		fTokenScanner = new CSSTokenScanner();
		fComments = new ArrayList<IRange>();
	}

	/**
	 * setSource
	 * 
	 * @param text
	 */
	public void setSource(String text)
	{
		setSource(new Document(text));
	}

	/**
	 * setSource
	 * 
	 * @param document
	 */
	public void setSource(IDocument document)
	{
		fDocument = document;
		fTokenScanner.setRange(fDocument, 0, fDocument.getLength());
		fComments.clear();
	}

	/**
	 * getComments
	 * 
	 * @return
	 */
	public IRange[] getComments()
	{
		return fComments.toArray(new IRange[fComments.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Scanner#nextToken()
	 */
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = fTokenScanner.nextToken();
		Object data = token.getData();

		while (token.isWhitespace() || (data != null && data.equals(CSSTokenType.COMMENT)))
		{
			// ignores whitespace and keeps a record of the comments
			if (CSSTokenType.COMMENT.equals(data))
			{
				int offset = fTokenScanner.getTokenOffset();
				int length = fTokenScanner.getTokenLength();

				fComments.add(new Range(offset, offset + length - 1));
			}

			token = fTokenScanner.nextToken();
			data = token.getData();
		}

		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();
		short type = (data != null) ? ((CSSTokenType) data).getShort() : CSSTokenType.EOF.getShort();
		String text = null;

		try
		{
			text = fDocument.get(offset, length);
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		return new Symbol(type, offset, offset + length - 1, text);
	}
}
