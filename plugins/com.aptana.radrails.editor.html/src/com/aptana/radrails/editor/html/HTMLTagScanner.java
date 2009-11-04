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
package com.aptana.radrails.editor.html;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.radrails.editor.common.RegexpRule;
import com.aptana.radrails.editor.common.WhitespaceDetector;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

public class HTMLTagScanner extends RuleBasedScanner {
	private static String[] STRUCTURE_DOT_ANY = {
		"html"
		,"head"
		,"body"
	};
	
	private static String[] BLOCK_DOT_ANY = {
		"div"
		,"form"
		,"ul"
		,"ol"
		,"p"
		,"table"
		,"h1"
		,"h2"
		,"h3"
		,"h4"
		,"h5"
		,"h6"
		,"hr"
	};
	
	public HTMLTagScanner() {
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add rule for double quotes
		rules.add(new SingleLineRule("\"", "\"", ThemeUtil.getToken("string.quoted.double.html"), '\\'));

		// Add a rule for single quotes
		rules.add(new SingleLineRule("'", "'", ThemeUtil.getToken("string.quoted.single.html"), '\\'));

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		
		rules.add(new RegexpRule("[a-zA-Z0-9]+=", ThemeUtil.getToken("entity.other.attribute-name.html")));
		WordRule wordRule = new WordRule(new WordDetector(), ThemeUtil.getToken("entity.name.tag.inline.any.html"));

		// script tag
		wordRule.addWord("script", ThemeUtil.getToken("entity.name.tag.script.html"));
		wordRule.addWord("style", ThemeUtil.getToken("entity.name.tag.style.html"));
		wordRule.addWord("body", ThemeUtil.getToken("entity.name.tag.structure.any.html"));
		wordRule.addWord("table", ThemeUtil.getToken("entity.name.tag.style.html"));

		IToken structureDotAnyToken = ThemeUtil.getToken("entity.name.tag.structure.any.html");		
		for (String structureDotAnyTag : STRUCTURE_DOT_ANY) {
			wordRule.addWord(structureDotAnyTag, structureDotAnyToken);
		}
		
		IToken blockDotAnyToken = ThemeUtil.getToken("entity.name.tag.block.any.html");		
		for (String blockDotAnyTag : STRUCTURE_DOT_ANY) {
			wordRule.addWord(blockDotAnyTag, blockDotAnyToken);
		}
		
		rules.add(wordRule);
		
		rules.add(new RegexpRule("</|<", ThemeUtil.getToken("punctuation.definition.tag.begin.html")));
		rules.add(new RegexpRule(">", ThemeUtil.getToken("punctuation.definition.tag.end.html")));
		
		setRules(rules.toArray(new IRule[rules.size()]));
		
		setDefaultReturnToken(ThemeUtil.getToken("text"));
	}
	
	/**
	 * A key word detector.
	 */
	static class WordDetector implements IWordDetector
	{
		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c);
		}
	}

}
