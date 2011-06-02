/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Max Stepanov
 *
 */
public class DelegatingRuleBasedScanner extends RuleBasedScanner {

	private ITokenScanner delegateTokenScanner;

	protected void initDelegation(ITokenScanner tokenScanner, int offset, int length) {
		delegateTokenScanner = tokenScanner;
		delegateTokenScanner.setRange(fDocument, offset, length);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken() {
		if (delegateTokenScanner != null) {
			IToken token = delegateTokenScanner.nextToken();
			if (token != Token.EOF) {
				return token;
			}
			delegateTokenScanner = null;
		}
		return super.nextToken();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public void setRange(IDocument document, int offset, int length) {
		delegateTokenScanner = null;
		super.setRange(document, offset, length);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenOffset()
	 */
	@Override
	public int getTokenOffset() {
		if (delegateTokenScanner != null) {
			return delegateTokenScanner.getTokenOffset();
		}
		return super.getTokenOffset();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenLength()
	 */
	@Override
	public int getTokenLength() {
		if (delegateTokenScanner != null) {
			return delegateTokenScanner.getTokenLength();
		}
		return super.getTokenLength();
	}

}
