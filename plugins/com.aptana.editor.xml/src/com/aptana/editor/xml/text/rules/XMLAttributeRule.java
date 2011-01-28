/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.dtd.text.rules.DTDNameDetector;

/**
 * XMLAttributeRule
 */
public class XMLAttributeRule extends ExtendedWordRule
{
	/**
	 * XMLAttributeRule
	 * 
	 * @param defaultToken
	 */
	public XMLAttributeRule(IToken defaultToken)
	{
		super(new DTDNameDetector(), defaultToken, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		char c = (char) scanner.read();
		
		// rewind since we're only performing lookahead
		scanner.unread();
		
		return c == '=';
	}

}
