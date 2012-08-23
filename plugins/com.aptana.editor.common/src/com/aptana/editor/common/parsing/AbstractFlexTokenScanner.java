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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import beaver.Scanner;
import beaver.Symbol;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;

/**
 * Abstract implementation of a JFlex scanner. Deals with generating whitespace tokens (which are usually not generated
 * by JFlex). Also provides facilities for doing lookaheads and a common approach for mapping the tokens returned by
 * JFlex.
 */
public abstract class AbstractFlexTokenScanner implements ITokenScanner
{

	/**
	 * Queue used to put symbols we look-ahead
	 */
	protected final Queue<Symbol> fLookAheadQueue = new LinkedList<Symbol>();

	/**
	 * Offset set (needed to properly return the ranges as our offset will be relative to this position).
	 */
	protected int fOffset;

	/**
	 * Start with -1 offset so that whitespace token emulation works when document starts with spaces.
	 */
	protected Symbol fLastSymbol = new Symbol((short) -1, -1, -1);

	/**
	 * Offset of the token found (relative to fOffset).
	 */
	protected int fTokenOffset;

	/**
	 * Length of the token found.
	 */
	protected int fTokenLen;

	/**
	 * Whether the last returned token was a 'generated' whitespace token.
	 */
	protected boolean fLastWasWhitespace = false;

	protected final Scanner fScanner;

	protected AbstractFlexTokenScanner(Scanner scanner)
	{
		fScanner = scanner;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length)
	{
		fLookAheadQueue.clear();
		fLastWasWhitespace = false;
		Assert.isLegal(document != null);
		final int documentLength = document.getLength();

		// Check the range
		Assert.isLegal(offset > -1);
		Assert.isLegal(length > -1);
		Assert.isLegal(offset + length <= documentLength);

		this.fOffset = offset;

		try
		{
			setSource(document.get(offset, length));
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	/**
	 * Subclasses must override to configure the actual source in the scanner.
	 */
	protected abstract void setSource(String source);

	/**
	 * Gathers the next token based on the Scanner. Note that it does some manipulations to create whitespace tokens
	 * (which the jflex scanner does not return).
	 * 
	 * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
	 */
	public IToken nextToken()
	{
		try
		{
			Symbol symbol;
			if (fLastWasWhitespace)
			{
				symbol = fLastSymbol;
				fLastWasWhitespace = false;
			}
			else
			{

				symbol = fLookAheadQueue.poll();
				if (symbol == null)
				{
					symbol = fScanner.nextToken();
				}
				// Emulate whitespace token creation.
				if (symbol.getStart() > fLastSymbol.getEnd() + 1)
				{
					fTokenOffset = fLastSymbol.getEnd() + 1;
					fTokenLen = symbol.getStart() - fLastSymbol.getEnd() - 1;
					fLastSymbol = symbol;
					fLastWasWhitespace = true;
					return getWhitespace();
				}
				else
				{
					fLastWasWhitespace = false;
				}
			}

			// Note: the mapToken may update the fTokenLen, fTokenOffset internally, so, set
			// the defaults now.
			fTokenOffset = symbol.getStart();
			fTokenLen = symbol.getEnd() - symbol.getStart() + 1;
			IToken ret = mapToken(symbol);
			// Only set the last symbol after mapping the token as the last symbol could be used during the mapping.
			fLastSymbol = symbol;
			return ret;
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			return getUndefinedToken();
		}
	}

	/**
	 * @return the whitespace token to be used (i.e.: css may change it to use the proper context).
	 */
	protected IToken getWhitespace()
	{
		return Token.WHITESPACE;
	}

	/**
	 * @return the token to be used for undefined.
	 */
	protected abstract IToken getUndefinedToken();

	/**
	 * Subclasses should override to map the JFlex symbol to the token expected.
	 */
	protected abstract IToken mapToken(Symbol token) throws IOException, beaver.Scanner.Exception;

	//@formatter:off
	/**
	 * Helper to do a look ahead. To use a temporary queue must be created from the current queue of 'looked ahead
	 * items': 
	 * 
	 * I.e.:
	 *   Queue<Symbol> tempQueue = fLookAheadQueue.peek() != null ? new LinkedList<Symbol>(fLookAheadQueue) : null;
	 * 
	 *   Symbol nextToken = lookAhead(tempQueue);
	 * 
	 * @throws IOException
	 * @throws beaver.Scanner.Exception
	 */
	//@formatter:on
	protected Symbol lookAhead(Queue<Symbol> tempQueue) throws IOException, beaver.Scanner.Exception
	{
		Symbol nextToken;
		if (tempQueue == null)
		{
			nextToken = fScanner.nextToken();
			fLookAheadQueue.add(nextToken);
			return nextToken;
		}

		nextToken = tempQueue.poll();
		if (nextToken != null)
		{
			return nextToken;
		}
		nextToken = fScanner.nextToken();
		fLookAheadQueue.add(nextToken);
		return nextToken;
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
}
