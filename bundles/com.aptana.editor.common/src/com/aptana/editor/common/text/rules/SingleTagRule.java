/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;

/**
 * This rule is a special case of SingleLineRule. It matches an exact sequence,
 * much like a single-word word-rule
 * without the need for creating a special subclass of IWordDetector.
 * 
 * @author Max Stepanov
 */
public class SingleTagRule extends SingleLineRule {

	/**
	 * @param startSequence
	 * @param token
	 */
	public SingleTagRule(String startSequence, IToken token) {
		super(startSequence, "", token); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse
	 * .jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		return true;
	}

}
