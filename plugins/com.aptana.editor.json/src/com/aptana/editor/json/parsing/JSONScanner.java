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
package com.aptana.editor.json.parsing;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.json.JSONSourceScanner;
import com.aptana.editor.json.parsing.lexer.JSONTokenType;

public class JSONScanner extends Scanner
{
	private static class JSONParserScanner extends JSONSourceScanner
	{
		protected IToken createToken(JSONTokenType type)
		{
			return new Token(type);
		}
	}

	private JSONParserScanner _sourceScanner;
	private IDocument _document;

	/**
	 * JSONScanner
	 */
	public JSONScanner()
	{
		this._sourceScanner = new JSONParserScanner();
	}

	/**
	 * createSymbol
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected Symbol createSymbol(Object data) throws Exception
	{
		int offset = this._sourceScanner.getTokenOffset();
		int length = this._sourceScanner.getTokenLength();
		JSONTokenType type = (data == null) ? JSONTokenType.EOF : (JSONTokenType) data;

		try
		{
			int totalLength = this._document.getLength();

			if (offset > totalLength)
			{
				offset = totalLength;
			}
			if (length == -1)
			{
				length = 0;
			}

			return new Symbol(type.getIndex(), offset, offset + length - 1, this._document.get(offset, length));
		}
		catch (BadLocationException e)
		{
			throw new Scanner.Exception(e.getLocalizedMessage());
		}
	}

	/**
	 * isComment
	 * 
	 * @param data
	 * @return
	 */
	protected boolean isComment(Object data)
	{
		return (data != null && ((JSONTokenType) data) == JSONTokenType.COMMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Scanner#nextToken()
	 */
	@Override
	public Symbol nextToken() throws IOException, Exception
	{
		IToken token = this._sourceScanner.nextToken();
		Object data = token.getData();

		while (token.isWhitespace() || isComment(data))
		{
			token = this._sourceScanner.nextToken();
			data = token.getData();
		}

		return this.createSymbol(data);
	}

	/**
	 * setSource
	 * 
	 * @param document
	 */
	public void setSource(IDocument document)
	{
		this._document = document;
		this._sourceScanner.setRange(document, 0, document.getLength());
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
