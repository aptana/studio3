/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Max Stepanov
 *
 */
public abstract class QueuedTokenScanner implements ITokenScanner {

	private static class Entry {
		private IToken token;
		private final int offset;
		private final int length;

		public Entry(IToken token, int offset, int length) {
			this.token = token;
			this.offset = offset;
			this.length = length;
		}
	}

	private final Queue<Entry> queue = new LinkedList<Entry>();
	private Entry current;

	/**
	 * 
	 */
	protected QueuedTokenScanner() {
	}

	/**
	 * Queue the specified token with offset:length for return by nextToken()
	 * @param token
	 * @param offset
	 * @param length
	 */
	public final void queueToken(IToken token, int offset, int length) {
		queue.add(new Entry(token, offset, length));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length) {
		queue.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
	 */
	public IToken nextToken() {
		current = queue.poll();
		if (current != null) {
			return current.token;
		}
		return Token.EOF;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenOffset()
	 */
	public final int getTokenOffset() {
		return (current != null) ? current.offset : 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenLength()
	 */
	public final int getTokenLength() {
		return (current != null) ? current.length : 0;
	}

}
