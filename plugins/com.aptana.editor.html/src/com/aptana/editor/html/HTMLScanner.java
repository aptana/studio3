/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.ExtendedWordRule;
import com.aptana.editor.common.SingleCharacterRule;
import com.aptana.editor.common.WhitespaceDetector;
import com.aptana.editor.common.WordDetector;
import com.aptana.editor.common.theme.ThemeUtil;

public class HTMLScanner extends RuleBasedScanner
{

	private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&([a-zA-Z0-9]+|#[0-9]+|#x[0-9a-fA-F]+);"); //$NON-NLS-1$

	public HTMLScanner()
	{
		IRule[] rules = new IRule[4];
		rules[0] = new WhitespaceRule(new WhitespaceDetector());
		rules[1] = new ExtendedWordRule(new IWordDetector()
		{

			@Override
			public boolean isWordStart(char c)
			{
				return c == '&';
			}

			@Override
			public boolean isWordPart(char c)
			{
				return Character.isLetter(c) || Character.isDigit(c) || c == ';' || c == '#';
			}
		}, createToken("constant.character.entity.html"), true) //$NON-NLS-1$
		{
			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				return word.length() > 2 && word.endsWith(";") && HTML_ENTITY_PATTERN.matcher(word).find(); //$NON-NLS-1$
			}
		};
		// non-entity ampersands should be marked as invalid
		rules[2] = new SingleCharacterRule('&', createToken("invalid.illegal.bad-ampersand.html")); //$NON-NLS-1$
		rules[3] = new WordRule(new WordDetector(), createToken("text")); //$NON-NLS-1$
		setRules(rules);
		setDefaultReturnToken(createToken("text")); //$NON-NLS-1$
	}

	protected IToken createToken(String string)
	{
		return ThemeUtil.getToken(string);
	}
}
