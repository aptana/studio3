/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Symbol;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.AbstractFlexTokenScanner;
import com.aptana.editor.js.parsing.JSPartitioningFlexScanner;
import com.aptana.editor.js.parsing.JSTokenTypeSymbol;

/**
 * A partition scanner for Javascript code.
 */
public class JSSourcePartitionScannerJFlex extends AbstractFlexTokenScanner implements IPartitionTokenScanner
{

	private static final Token JS_DOC_TOKEN = new Token(JSSourceConfiguration.JS_DOC);
	private static final Token STRING_DOUBLE_TOKEN = new Token(JSSourceConfiguration.STRING_DOUBLE);
	private static final Token STRING_SINGLE_TOKEN = new Token(JSSourceConfiguration.STRING_SINGLE);
	private static final Token REGEXP_TOKEN = new Token(JSSourceConfiguration.JS_REGEXP);
	private static final Token MULTILINE_COMMENT_TOKEN = new Token(JSSourceConfiguration.JS_MULTILINE_COMMENT);
	private static final Token SINGLELINE_COMMENT_TOKEN = new Token(JSSourceConfiguration.JS_SINGLELINE_COMMENT);
	private final Queue<Object[]> preCalculatedTokenOffsetAndLen = new LinkedList<Object[]>();

	public JSSourcePartitionScannerJFlex()
	{
		super(new JSPartitioningFlexScanner());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPartitionTokenScanner#setPartialRange(org.eclipse.jface.text.IDocument, int,
	 * int, java.lang.String, int)
	 */
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset)
	{
		if (partitionOffset > -1)
		{
			int delta = offset - partitionOffset;
			if (delta > 0)
			{
				// We want to do the parse from where the partition started and not based
				// on where the line starts.
				setRange(document, partitionOffset, length + delta);
				fOffset = partitionOffset;
				return;
			}
		}
		setRange(document, offset, length);
	}

	@Override
	protected void setSource(String source)
	{
		((JSPartitioningFlexScanner) fScanner).setSource(source);
	}

	@Override
	public IToken nextToken()
	{
		Object[] next = preCalculatedTokenOffsetAndLen.poll();
		if (next != null)
		{
			fTokenOffset = (Integer) next[1];
			fTokenLen = (Integer) next[2];
			return (IToken) next[0];
		}
		try
		{
			JSTokenTypeSymbol symbol;
			symbol = (JSTokenTypeSymbol) fLookAheadQueue.poll();
			if (symbol == null)
			{
				symbol = (JSTokenTypeSymbol) fScanner.nextToken();
			}

			JSTokenTypeSymbol symbolStart = symbol;
			fTokenOffset = symbol.getStart();

			Token token = null;
			while (true)
			{
				// System.out.println(symbol.token);

				switch (symbol.token)
				{
					case SDOC:
						return returnToken(symbol, symbolStart, token, symbol.getEnd() - symbol.getStart() + 1,
								JS_DOC_TOKEN);

					case STRING_DOUBLE:
						return returnToken(symbol, symbolStart, token, symbol.getEnd() - symbol.getStart() + 1,
								STRING_DOUBLE_TOKEN);

					case STRING_SINGLE:
						return returnToken(symbol, symbolStart, token, symbol.getEnd() - symbol.getStart() + 1,
								STRING_SINGLE_TOKEN);

					case REGEX:
						return returnToken(symbol, symbolStart, token, symbol.getEnd() - symbol.getStart() + 1,
								REGEXP_TOKEN);

					case MULTILINE_COMMENT:
						return returnToken(symbol, symbolStart, token, symbol.getEnd() - symbol.getStart() + 1,
								MULTILINE_COMMENT_TOKEN);

					case SINGLELINE_COMMENT:
						return returnToken(symbol, symbolStart, token, symbol.getEnd() - symbol.getStart() + 1,
								SINGLELINE_COMMENT_TOKEN);

					case EOF:
						return returnToken(symbol, symbolStart, token, 0, Token.EOF);
				}

				// Ok, we haven't returned, so, let's get the next symbol and mark that the return token should be the
				// default.
				token = new Token(IDocument.DEFAULT_CONTENT_TYPE);
				symbol = (JSTokenTypeSymbol) fLookAheadQueue.poll();
				if (symbol == null)
				{
					symbol = (JSTokenTypeSymbol) fScanner.nextToken();
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			return new Token(IDocument.DEFAULT_CONTENT_TYPE);
		}
	}

	//@formatter:off
	/**
	 * Updates the token len and returns the proper content based on the first found token type.
	 * 
	 * I.e.: 
	 * Use-case:
	 * var a = 10; //comment
	 * 
	 * we'll match many "default" tokens at "var a = 10; " but won't return anything until the "//comment"
	 * is found, at which point we'll mark the next token to be returned as the comment token but
	 * we actually return the "default" token at this point. 
	 */
	//@formatter:on
	private IToken returnToken(JSTokenTypeSymbol symbol, JSTokenTypeSymbol symbolStart, IToken token, int tokenLen,
			IToken matchedToken)
	{
		if (token != null)
		{
			preCalculatedTokenOffsetAndLen.add(new Object[] { matchedToken, symbol.getStart(), tokenLen });
			fTokenLen = symbol.getStart() - symbolStart.getStart();
			return token;
		}
		else
		{
			fTokenLen = tokenLen;
			return matchedToken;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenOffset()
	 */
	public int getTokenOffset()
	{
		return fOffset + fTokenOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenLength()
	 */
	public int getTokenLength()
	{
		return fTokenLen;
	}

	@Override
	protected IToken getUndefinedToken()
	{
		return new Token(IDocument.DEFAULT_CONTENT_TYPE);
	}

	@Override
	protected IToken mapToken(Symbol token) throws IOException, beaver.Scanner.Exception
	{
		throw new AssertionError("Should not be called in this class."); //$NON-NLS-1$
	}

}
