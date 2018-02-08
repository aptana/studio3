/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html.internal.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class DocTypeRule extends MultiLineRule {
	
    private int fEmbeddedStart= 0;

	public DocTypeRule(IToken token) {
        super("<!DOCTYPE", ">", token); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    protected boolean endSequenceDetected(ICharacterScanner scanner) {
        int c;
        while ((c = scanner.read()) != ICharacterScanner.EOF) {
            if (c == fEscapeCharacter) {
                // Skip the escaped character.
                scanner.read();
            } else if (c == '<') {
            	fEmbeddedStart++;
            } else if (c == '>') {
            	if (fEmbeddedStart == 0) {
            		return true;
            	}
            	fEmbeddedStart--;
            }
        }
        return true;
    }
}