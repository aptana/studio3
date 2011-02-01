/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
