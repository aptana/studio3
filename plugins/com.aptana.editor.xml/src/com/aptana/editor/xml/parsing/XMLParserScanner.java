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
package com.aptana.editor.xml.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

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
	 * isComment
	 * 
	 * @param data
	 * @return
	 */
	private boolean isComment(Object data)
	{
		return (data != null && ((XMLTokenType) data) == XMLTokenType.COMMENT);
	}

	/**
	 * nextLexeme
	 */
	public Lexeme<XMLTokenType> nextLexeme() throws IOException, Exception
	{
		IToken token = fTokenScanner.nextToken();
		Object data = token.getData();

		while (token.isWhitespace() || isComment(data))
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
			e.printStackTrace();
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
