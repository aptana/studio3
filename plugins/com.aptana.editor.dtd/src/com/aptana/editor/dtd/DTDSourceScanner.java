/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
