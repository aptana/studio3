/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.xml.internal.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

import com.aptana.editor.common.text.rules.CollectingCharacterScanner;
import com.aptana.editor.common.text.rules.ExtendedToken;

/**
 * 
 * @author Michael Xia
 * @author Max Stepanov
 *
 */
public class DocTypeRule extends MultiLineRule {

	private final boolean breakOnDTD;

	public DocTypeRule(IToken token, boolean breakOnDTD) {
		super("<!DOCTYPE", ">", token); //$NON-NLS-1$ //$NON-NLS-2$
		this.breakOnDTD = breakOnDTD;
	}

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		int c;
		int embeddedDTD = 0;
		CollectingCharacterScanner collectingCharacterScanner = new CollectingCharacterScanner(scanner, String.valueOf(fStartSequence));
		while ((c = collectingCharacterScanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip the escaped character.
				collectingCharacterScanner.read();
			} else if (c == '[') {
				if (breakOnDTD) {
					break;
				}
				++embeddedDTD;
			} else if (c == ']') {
				--embeddedDTD;
			} else if (c == '>' && embeddedDTD <= 0) {
				break;
			}
		}
		if (fToken instanceof ExtendedToken) {
			((ExtendedToken) fToken).setContents(collectingCharacterScanner.getContents());
		}
		return true;
	}
}
