/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Max Stepanov
 */
public abstract class QueuedRuleBasedScanner extends RuleBasedScanner
{

	protected static class Entry
	{
		private final ITokenScanner tokenScanner;
		private IToken token;
		private final int offset;
		private final int length;

		public Entry(ITokenScanner tokenScanner, IToken token, int offset, int length)
		{
			this.tokenScanner = tokenScanner;
			this.token = token;
			this.offset = offset;
			this.length = length;
		}

		public IToken nextToken()
		{
			if (tokenScanner != null)
			{
				return tokenScanner.nextToken();
			}

			// return value then reset to EOF
			try
			{
				return token;
			}
			finally
			{
				token = Token.EOF;
			}
		}

		public int getTokenOffset()
		{
			if (tokenScanner != null)
			{
				return tokenScanner.getTokenOffset();
			}
			return offset;
		}

		public int getTokenLength()
		{
			if (tokenScanner != null)
			{
				return tokenScanner.getTokenLength();
			}
			return length;
		}

		@Override
		public String toString()
		{
			return MessageFormat.format("[{0}, {1}]: {2}", offset, length, token.getData()); //$NON-NLS-1$
		}
	}

	protected final Queue<Entry> queue = new LinkedList<Entry>();
	private Entry fCurrentEntry;

	/**
	 * Queue the specified token with offset:length for return by nextToken()
	 * 
	 * @param token
	 * @param offset
	 * @param length
	 */
	public void queueToken(IToken token, int offset, int length)
	{
		queue.add(new Entry(null, token, offset, length));
	}

	/**
	 * Queue the specified delegate token scanner with offset:length range for return by nextToken() The given delegate
	 * will be used until EOF token returned.
	 * 
	 * @param tokenScanner
	 * @param offset
	 * @param length
	 */
	public void queueDelegate(ITokenScanner tokenScanner, int offset, int length)
	{
		tokenScanner.setRange(fDocument, offset, length);
		queue.add(new Entry(tokenScanner, null, offset, length));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		queue.clear();
		super.setRange(document, offset, length);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken()
	{
		while (!queue.isEmpty())
		{
			fCurrentEntry = queue.element();
			IToken token = fCurrentEntry.nextToken();
			if (token != Token.EOF)
			{
				return token;
			}
			queue.remove();
		}
		fCurrentEntry = null;
		return super.nextToken();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenOffset()
	 */
	@Override
	public int getTokenOffset()
	{
		if (fCurrentEntry != null)
		{
			return fCurrentEntry.getTokenOffset();
		}
		if (!queue.isEmpty())
		{
			return queue.element().getTokenOffset();
		}
		return super.getTokenOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenLength()
	 */
	@Override
	public int getTokenLength()
	{
		if (fCurrentEntry != null)
		{
			return fCurrentEntry.getTokenLength();
		}
		if (!queue.isEmpty())
		{
			return queue.element().getTokenLength();
		}
		return super.getTokenLength();
	}

}
