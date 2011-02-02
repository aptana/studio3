/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.idl.parsing.lexer.IDLTokenType;
import com.aptana.editor.idl.text.rules.IDLNumberRule;
import com.aptana.editor.idl.text.rules.IDLOperatorDetector;

@SuppressWarnings("nls")
public class IDLSourceScanner extends RuleBasedScanner
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
			return Character.isLetter(c) || '_' == c;
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c) || '_' == c;
		}
	}

	/**
	 * DTDScanner
	 */
	public IDLSourceScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		rules.add(new SingleLineRule("\"", "\"", createToken(IDLTokenType.STRING)));

		// Already handled by partitioning, but we need this for the parser
		rules.add(new MultiLineRule("/**", "*/", createToken(IDLTokenType.DOC_COMMENT), '\0', true));
		rules.add(new MultiLineRule("/*", "*/", createToken(IDLTokenType.MULTILINE_COMMENT), '\0', true));

		// TODO: rule for other, but I think that is to catch errors and not necessary here

		WordRule operatorRule = new WordRule(new IDLOperatorDetector(), Token.UNDEFINED);
		operatorRule.addWord("::", createToken(IDLTokenType.DOUBLE_COLON));
		operatorRule.addWord("...", createToken(IDLTokenType.ELLIPSIS));
		rules.add(operatorRule);

		WordRule wordRule = new WordRule(new WordDetector(), createToken(IDLTokenType.IDENTIFIER));
		wordRule.addWord("any", createToken(IDLTokenType.ANY));
		wordRule.addWord("attribute", createToken(IDLTokenType.ATTRIBUTE));
		wordRule.addWord("boolean", createToken(IDLTokenType.BOOLEAN));
		wordRule.addWord("caller", createToken(IDLTokenType.CALLER));
		wordRule.addWord("const", createToken(IDLTokenType.CONST));
		wordRule.addWord("creator", createToken(IDLTokenType.CREATOR));
		wordRule.addWord("deleter", createToken(IDLTokenType.DELETER));
		wordRule.addWord("DOMString", createToken(IDLTokenType.DOMSTRING));
		wordRule.addWord("double", createToken(IDLTokenType.DOUBLE));
		wordRule.addWord("exception", createToken(IDLTokenType.EXCEPTION));
		wordRule.addWord("false", createToken(IDLTokenType.FALSE));
		wordRule.addWord("float", createToken(IDLTokenType.FLOAT));
		wordRule.addWord("getraises", createToken(IDLTokenType.GETRAISES));
		wordRule.addWord("getter", createToken(IDLTokenType.GETTER));
		wordRule.addWord("implements", createToken(IDLTokenType.IMPLEMENTS));
		wordRule.addWord("in", createToken(IDLTokenType.IN));
		wordRule.addWord("interface", createToken(IDLTokenType.INTERFACE));
		wordRule.addWord("long", createToken(IDLTokenType.LONG));
		wordRule.addWord("module", createToken(IDLTokenType.MODULE));
		wordRule.addWord("Object", createToken(IDLTokenType.OBJECT));
		wordRule.addWord("octet", createToken(IDLTokenType.OCTET));
		wordRule.addWord("omittable", createToken(IDLTokenType.OMITTABLE));
		wordRule.addWord("optional", createToken(IDLTokenType.OPTIONAL));
		wordRule.addWord("raises", createToken(IDLTokenType.RAISES));
		wordRule.addWord("readonly", createToken(IDLTokenType.READONLY));
		wordRule.addWord("sequence", createToken(IDLTokenType.SEQUENCE));
		wordRule.addWord("setraises", createToken(IDLTokenType.SETRAISES));
		wordRule.addWord("setter", createToken(IDLTokenType.SETTER));
		wordRule.addWord("short", createToken(IDLTokenType.SHORT));
		wordRule.addWord("stringifier", createToken(IDLTokenType.STRINGIFIER));
		wordRule.addWord("true", createToken(IDLTokenType.TRUE));
		wordRule.addWord("typedef", createToken(IDLTokenType.TYPEDEF));
		wordRule.addWord("unsigned", createToken(IDLTokenType.UNSIGNED));
		wordRule.addWord("void", createToken(IDLTokenType.VOID));
		rules.add(wordRule);

		// single-character operators and punctuation
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('{', createToken(IDLTokenType.LCURLY));
		cmRule.add('}', createToken(IDLTokenType.RCURLY));
		cmRule.add(';', createToken(IDLTokenType.SEMICOLON));
		cmRule.add(':', createToken(IDLTokenType.COLON));
		cmRule.add('<', createToken(IDLTokenType.LESS_THAN));
		cmRule.add('>', createToken(IDLTokenType.GREATER_THAN));
		cmRule.add('=', createToken(IDLTokenType.EQUAL));
		cmRule.add('(', createToken(IDLTokenType.LPAREN));
		cmRule.add(')', createToken(IDLTokenType.RPAREN));
		cmRule.add(',', createToken(IDLTokenType.COMMA));
		cmRule.add('[', createToken(IDLTokenType.LBRACKET));
		cmRule.add(']', createToken(IDLTokenType.RBRACKET));
		cmRule.add('?', createToken(IDLTokenType.QUESTION));
		rules.add(cmRule);

		// NOTE: this combines integer and float
		rules.add(new IDLNumberRule(createToken(IDLTokenType.NUMBER)));

		this.setRules(rules.toArray(new IRule[rules.size()]));
		// this.setDefaultReturnToken(this.createToken("text"));
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(IDLTokenType type)
	{
		return new Token(type.getScope());
	}
}
