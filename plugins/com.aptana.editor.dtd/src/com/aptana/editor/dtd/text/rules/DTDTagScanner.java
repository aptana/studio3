/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.dtd.text.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.dtd.core.parsing.DTDTokenType;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;

/**
 * @author Kevin Lindsey
 * @author Max Stepanov
 */
public class DTDTagScanner extends RuleBasedScanner
{

	/**
	 * 
	 */
	public DTDTagScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// NOTE: There is no String, but we're using this to generalize pubid,
		// att value, entity value
		rules.add(new MultiLineRule("\"", "\"", createToken(DTDTokenType.STRING), '\0', true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("'", "'", createToken(DTDTokenType.STRING), '\0', true)); //$NON-NLS-1$ //$NON-NLS-2$

		WordRule operatorRule = new WordRule(new DTDOperatorWordDetector(), Token.UNDEFINED);
		operatorRule.addWord("<![", createToken(DTDTokenType.SECTION_START)); //$NON-NLS-1$
		operatorRule.addWord("]]>", createToken(DTDTokenType.SECTION_END)); //$NON-NLS-1$
		rules.add(operatorRule);

		WordRule wordRule = new WordRule(new DTDWordDetector(), Token.UNDEFINED);
		wordRule.addWord("<!ATTLIST", createToken(DTDTokenType.ATTLIST)); //$NON-NLS-1$
		wordRule.addWord("<!ELEMENT", createToken(DTDTokenType.ELEMENT)); //$NON-NLS-1$
		wordRule.addWord("<!ENTITY", createToken(DTDTokenType.ENTITY)); //$NON-NLS-1$
		wordRule.addWord("<!NOTATION", createToken(DTDTokenType.NOTATION)); //$NON-NLS-1$
		wordRule.addWord("#FIXED", createToken(DTDTokenType.FIXED)); //$NON-NLS-1$
		wordRule.addWord("#IMPLIED", createToken(DTDTokenType.IMPLIED)); //$NON-NLS-1$
		wordRule.addWord("#PCDATA", createToken(DTDTokenType.PCDATA)); //$NON-NLS-1$
		wordRule.addWord("#REQUIRED", createToken(DTDTokenType.REQUIRED)); //$NON-NLS-1$
		wordRule.addWord("ANY", createToken(DTDTokenType.ANY)); //$NON-NLS-1$
		wordRule.addWord("CDATA", createToken(DTDTokenType.CDATA_TYPE)); //$NON-NLS-1$
		wordRule.addWord("EMPTY", createToken(DTDTokenType.EMPTY)); //$NON-NLS-1$
		wordRule.addWord("ENTITY", createToken(DTDTokenType.ENTITY_TYPE)); //$NON-NLS-1$
		wordRule.addWord("ENTITIES", createToken(DTDTokenType.ENTITIES_TYPE)); //$NON-NLS-1$
		wordRule.addWord("ID", createToken(DTDTokenType.ID_TYPE)); //$NON-NLS-1$
		wordRule.addWord("IDREF", createToken(DTDTokenType.IDREF_TYPE)); //$NON-NLS-1$
		wordRule.addWord("IDREFS", createToken(DTDTokenType.IDREFS_TYPE)); //$NON-NLS-1$
		wordRule.addWord("IGNORE", createToken(DTDTokenType.IGNORE)); //$NON-NLS-1$
		wordRule.addWord("INCLUDE", createToken(DTDTokenType.INCLUDE)); //$NON-NLS-1$
		wordRule.addWord("NDATA", createToken(DTDTokenType.NDATA)); //$NON-NLS-1$
		wordRule.addWord("NMTOKEN", createToken(DTDTokenType.NMTOKEN_TYPE)); //$NON-NLS-1$
		wordRule.addWord("NMTOKENS", createToken(DTDTokenType.NMTOKENS_TYPE)); //$NON-NLS-1$
		wordRule.addWord("NOTATION", createToken(DTDTokenType.NOTATION_TYPE)); //$NON-NLS-1$
		wordRule.addWord("PUBLIC", createToken(DTDTokenType.PUBLIC)); //$NON-NLS-1$
		wordRule.addWord("SYSTEM", createToken(DTDTokenType.SYSTEM)); //$NON-NLS-1$
		rules.add(wordRule);

		rules.add(new DTDEntityRule('%', createToken(DTDTokenType.PE_REF)));
		rules.add(new DTDEntityRule('&', createToken(DTDTokenType.PE_REF)));

		CharacterMapRule charsRule = new CharacterMapRule();
		charsRule.add('>', createToken(DTDTokenType.GREATER_THAN));
		charsRule.add('(', createToken(DTDTokenType.LPAREN));
		charsRule.add('|', createToken(DTDTokenType.PIPE));
		charsRule.add(')', createToken(DTDTokenType.RPAREN));
		charsRule.add('?', createToken(DTDTokenType.QUESTION));
		charsRule.add('*', createToken(DTDTokenType.STAR));
		charsRule.add('+', createToken(DTDTokenType.PLUS));
		charsRule.add(',', createToken(DTDTokenType.COMMA));
		charsRule.add('%', createToken(DTDTokenType.PERCENT));
		charsRule.add('[', createToken(DTDTokenType.LBRACKET));
		rules.add(charsRule);

		rules.add(new WordRule(new DTDNameDetector(), createToken(DTDTokenType.NAME)));
		rules.add(new WordRule(new DTDNmtokenWordDetector(), createToken(DTDTokenType.NMTOKEN)));

		setRules(rules.toArray(new IRule[rules.size()]));
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
