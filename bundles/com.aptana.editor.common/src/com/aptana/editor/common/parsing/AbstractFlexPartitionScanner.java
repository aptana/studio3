/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;

/**
 * Abstract class for JFlex-based partitioners.
 */
public abstract class AbstractFlexPartitionScanner extends AbstractFlexTokenScanner implements IPartitionTokenScanner
{

	private final static class TokenOffsetAndLen
	{
		public final int offset;
		public final int len;
		public final IToken token;

		public TokenOffsetAndLen(IToken token, int offset, int len)
		{
			this.token = token;
			this.offset = offset;
			this.len = len;
		}

	}

	private static final Token DEFAULT_CONTENT_TYPE_TOKEN = new Token(IDocument.DEFAULT_CONTENT_TYPE);
	protected final Queue<TokenOffsetAndLen> preCalculatedTokenOffsetAndLen = new LinkedList<TokenOffsetAndLen>();

	protected AbstractFlexPartitionScanner(Scanner scanner)
	{
		super(scanner);
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
	public IToken nextToken()
	{
		TokenOffsetAndLen next = preCalculatedTokenOffsetAndLen.poll();
		if (next != null)
		{
			fTokenOffset = next.offset;
			fTokenLen = next.len;
			return next.token;
		}
		try
		{
			Symbol symbol;
			symbol = fLookAheadQueue.poll();
			if (symbol == null)
			{
				symbol = fScanner.nextToken();
			}

			Symbol symbolStart = symbol;
			fTokenOffset = symbol.getStart();

			boolean returnDefaultContentType = false;
			while (true)
			{
				// System.out.println(symbol.token);
				IToken token = mapToken(symbol, symbolStart, returnDefaultContentType);
				if (token != null)
				{
					return token;
				}

				// Ok, we haven't returned, so, let's get the next symbol and mark that the return token should be the
				// default.
				returnDefaultContentType = true;
				symbol = fLookAheadQueue.poll();
				if (symbol == null)
				{
					symbol = fScanner.nextToken();
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			return DEFAULT_CONTENT_TYPE_TOKEN;
		}
	}

	/**
	 * Subclasses must override to map a symbol to a token. A returned null value means that the default content type
	 * was matched (in which case, in the next call, returnDefaultContentType will be true, to signal that in a match of
	 * another content type, it should first return the default content type and only in the next call the actual
	 * different partition found).
	 */
	protected abstract IToken mapToken(Symbol symbol, Symbol symbolStart, boolean returnDefaultContentType);

	/**
	 * We have a different way of mapping tokens in the partitioner.
	 */
	@Override
	protected IToken mapToken(Symbol token) throws IOException, beaver.Scanner.Exception
	{
		throw new AssertionError("Should not be called in this class."); //$NON-NLS-1$
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
	protected IToken returnToken(Symbol symbol, Symbol symbolStart, boolean returnDefaultContentType, int tokenLen,
			IToken matchedToken)
	{
		if (returnDefaultContentType)
		{
			preCalculatedTokenOffsetAndLen.add(new TokenOffsetAndLen(matchedToken, symbol.getStart(), tokenLen));
			fTokenLen = symbol.getStart() - symbolStart.getStart();
			return DEFAULT_CONTENT_TYPE_TOKEN;
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
		return DEFAULT_CONTENT_TYPE_TOKEN;
	}

}
