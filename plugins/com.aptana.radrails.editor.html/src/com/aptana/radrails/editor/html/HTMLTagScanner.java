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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.radrails.editor.common.CommonEditorPlugin;
import com.aptana.radrails.editor.common.WhitespaceDetector;
import com.aptana.radrails.editor.common.theme.ThemeUtil;
import com.aptana.radrails.editor.js.WordDetector;

public class HTMLTagScanner extends RuleBasedScanner {

	private static final String[] EVENT_HANDLERS = new String[] {
		 "onload"
		,"onunload"
		,"onclick"
		,"ondblclick"
		,"onmousedown"
		,"onmouseup"
		,"onmouseover"
		,"onmousemove"
		,"onmouseout"
		,"onfocus"
		,"onblur"
		,"onkeypress"
		,"onkeydown"
		,"onkeyup"
		,"onsubmit"
		,"onreset"
		,"onselect"
		,"onchange"
		};
	
	public HTMLTagScanner() {
		IToken string =
			new Token(
				new TextAttribute(CommonEditorPlugin.getDefault().getColorManager().getColor(IHTMLColorConstants.STRING)));

		List<IRule> rules = new ArrayList<IRule>();
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		
		// Add rule for double quotes
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		// Add a rule for single quotes
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		
		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new WordDetector(), Token.UNDEFINED);
		
		IToken eventHandler = ThemeUtil.getToken("keyword.control.js");
		for (String word : EVENT_HANDLERS)
		{
			wordRule.addWord(word, eventHandler);
		}
		
		rules.add(wordRule);

		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
