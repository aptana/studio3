/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.editor.dtd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.dtd.parsing.lexer.DTDTokenType;
import com.aptana.editor.dtd.text.rules.DTDEntityRule;
import com.aptana.editor.dtd.text.rules.DTDNameDetector;
import com.aptana.editor.dtd.text.rules.DTDNmtokenDetector;
import com.aptana.editor.dtd.text.rules.DTDOperatorDetector;

@SuppressWarnings("nls")
public class DTDSourceScanner extends RuleBasedScanner
{
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
			return Character.isLetter(c) || c == '<' || c == '#';
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordPart(char c)
		{
			return Character.isLetter(c) || c == '!';
		}
	}

	/**
	 * DTDScanner
	 */
	public DTDSourceScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Already handled by partitioning, but we need this for the parser
		rules.add(new MultiLineRule("<!--", "-->", createToken(DTDTokenType.COMMENT), '\0', true));

		// TODO: This should require Name directly after the opening <? and it
		// should reject <?xml
		rules.add(new MultiLineRule("<?", "?>", createToken(DTDTokenType.PI), '\0', true));

		// NOTE: There is no String, but we're using this to generalize
		// pubid, att value, entity value
		rules.add(new MultiLineRule("\"", "\"", createToken(DTDTokenType.STRING), '\0', true));
		rules.add(new MultiLineRule("'", "'", createToken(DTDTokenType.STRING), '\0', true));

		WordRule operatorRule = new WordRule(new DTDOperatorDetector(), Token.UNDEFINED);
		operatorRule.addWord("<![", createToken(DTDTokenType.SECTION_START));
		operatorRule.addWord("]]>", createToken(DTDTokenType.SECTION_END));
		rules.add(operatorRule);

		WordRule wordRule = new WordRule(new WordDetector(), Token.UNDEFINED);
		wordRule.addWord("<!ATTLIST", createToken(DTDTokenType.ATTLIST));
		wordRule.addWord("<!ELEMENT", createToken(DTDTokenType.ELEMENT));
		wordRule.addWord("<!ENTITY", createToken(DTDTokenType.ENTITY));
		wordRule.addWord("<!NOTATION", createToken(DTDTokenType.NOTATION));
		wordRule.addWord("#FIXED", createToken(DTDTokenType.FIXED));
		wordRule.addWord("#IMPLIED", createToken(DTDTokenType.IMPLIED));
		wordRule.addWord("#PCDATA", createToken(DTDTokenType.PCDATA));
		wordRule.addWord("#REQUIRED", createToken(DTDTokenType.REQUIRED));
		wordRule.addWord("ANY", createToken(DTDTokenType.ANY));
		wordRule.addWord("CDATA", createToken(DTDTokenType.CDATA_TYPE));
		wordRule.addWord("EMPTY", createToken(DTDTokenType.EMPTY));
		wordRule.addWord("ENTITY", createToken(DTDTokenType.ENTITY_TYPE));
		wordRule.addWord("ENTITIES", createToken(DTDTokenType.ENTITIES_TYPE));
		wordRule.addWord("ID", createToken(DTDTokenType.ID_TYPE));
		wordRule.addWord("IDREF", createToken(DTDTokenType.IDREF_TYPE));
		wordRule.addWord("IDREFS", createToken(DTDTokenType.IDREFS_TYPE));
		wordRule.addWord("IGNORE", createToken(DTDTokenType.IGNORE));
		wordRule.addWord("INCLUDE", createToken(DTDTokenType.INCLUDE));
		wordRule.addWord("NDATA", createToken(DTDTokenType.NDATA));
		wordRule.addWord("NMTOKEN", createToken(DTDTokenType.NMTOKEN_TYPE));
		wordRule.addWord("NMTOKENS", createToken(DTDTokenType.NMTOKENS_TYPE));
		wordRule.addWord("NOTATION", createToken(DTDTokenType.NOTATION_TYPE));
		wordRule.addWord("PUBLIC", createToken(DTDTokenType.PUBLIC));
		wordRule.addWord("SYSTEM", createToken(DTDTokenType.SYSTEM));
		rules.add(wordRule);

		// PERef
		rules.add(new DTDEntityRule('%', createToken(DTDTokenType.PE_REF)));

		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('>', createToken(DTDTokenType.GREATER_THAN));
		cmRule.add('(', createToken(DTDTokenType.LPAREN));
		cmRule.add('|', createToken(DTDTokenType.PIPE));
		cmRule.add(')', createToken(DTDTokenType.RPAREN));
		cmRule.add('?', createToken(DTDTokenType.QUESTION));
		cmRule.add('*', createToken(DTDTokenType.STAR));
		cmRule.add('+', createToken(DTDTokenType.PLUS));
		cmRule.add(',', createToken(DTDTokenType.COMMA));
		cmRule.add('%', createToken(DTDTokenType.PERCENT));
		cmRule.add('[', createToken(DTDTokenType.LBRACKET));
		rules.add(cmRule);

		// Name
		rules.add(new WordRule(new DTDNameDetector(), createToken(DTDTokenType.NAME)));

		// Nmtoken
		rules.add(new WordRule(new DTDNmtokenDetector(), createToken(DTDTokenType.NMTOKEN)));

		// EntityRef
		// rules.add(new DTDEntityRule('&', createToken(DTDTokenType.PE_REF)));

		this.setRules(rules.toArray(new IRule[rules.size()]));
		// this.setDefaultReturnToken(this.createToken("text"));
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(DTDTokenType type)
	{
		return new Token(type.getScope());
	}
}
