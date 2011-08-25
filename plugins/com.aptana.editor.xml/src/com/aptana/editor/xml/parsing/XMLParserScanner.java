/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.editor.xml.parsing.lexer.XMLTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class XMLParserScanner implements ITokenScanner
{
	private XMLTokenScanner fTokenScanner;
	private IDocument fDocument;

	/**
	 * XMLParserScanner
	 */
	public XMLParserScanner()
	{
		fTokenScanner = new XMLTokenScanner();
	}

	/**
	 * createLexeme
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected Lexeme<XMLTokenType> createLexeme(Object data) throws Exception
	{
		int offset = fTokenScanner.getTokenOffset();
		int length = fTokenScanner.getTokenLength();
		XMLTokenType type = (data == null) ? XMLTokenType.EOF : (XMLTokenType) data;

		try
		{
			int totalLength = fDocument.getLength();

			if (offset > totalLength)
			{
				offset = totalLength;
			}
			if (length == -1)
			{
				length = 0;
			}

			return new Lexeme<XMLTokenType>(type, offset, offset + length - 1, fDocument.get(offset, length));
		}
		catch (BadLocationException e)
		{
			throw new Exception(e.getLocalizedMessage());
		}
	}

	/**
	 * getSource
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	public String getSource(int offset, int length)
	{
		String result = ""; //$NON-NLS-1$

		try
		{
			result = this.fDocument.get(offset, length);
		}
		catch (BadLocationException e)
		{
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenLength()
	 */
	public int getTokenLength()
	{
		return fTokenScanner.getTokenLength();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenOffset()
	 */
	public int getTokenOffset()
	{
		return fTokenScanner.getTokenOffset();
	}

	/**
	 * nextLexeme
	 */
	public Lexeme<XMLTokenType> nextLexeme() throws Exception
	{
		IToken token = fTokenScanner.nextToken();
		Object data = token.getData();

		while (token.isWhitespace())
		{
			token = fTokenScanner.nextToken();
			data = token.getData();
		}

		return createLexeme(data);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
	 */
	public IToken nextToken()
	{
		try
		{
			Lexeme<XMLTokenType> lexeme = this.nextLexeme();

			return new Token(lexeme.getType());
		}
		catch (Exception e)
		{
			IdeLog.logError(XMLPlugin.getDefault(), e.getMessage(), e);
		}

		return Token.UNDEFINED;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length)
	{
		fDocument = document;
		fTokenScanner.setRange(document, offset, length);
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
}
