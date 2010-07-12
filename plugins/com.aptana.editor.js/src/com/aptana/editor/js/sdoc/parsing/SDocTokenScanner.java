package com.aptana.editor.js.sdoc.parsing;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

public class SDocTokenScanner extends RuleBasedScanner
{
	static class TagDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return (c == '@');
		}

		public boolean isWordPart(char c)
		{
			return Character.isLetter(c);
		}
	}

	static class OperatorDetector implements IWordDetector
	{
		private int fPosition;

		public boolean isWordPart(char c)
		{
			boolean result = false;

			fPosition++;

			if (fPosition == 1)
			{
				switch (c)
				{
					case '/':
					case '*':
						result = true;
						break;
				}
			}
			else if (fPosition == 2)
			{
				switch (c)
				{
					case '*':
						result = true;
						break;
				}
			}

			return result;
		}

		public boolean isWordStart(char c)
		{
			boolean result = false;

			fPosition = 0;

			switch (c)
			{
				case '/':
				case '*':
					result = true;
					break;
			}

			return result;
		}
	}

	/**
	 * SDocTokenScanner
	 */
	public SDocTokenScanner()
	{
		List<IRule> rules = new LinkedList<IRule>();

		rules.add(new RegexpRule("[ \\t]+", getToken(SDocTokenType.WHITESPACE), true)); //$NON-NLS-1$
		rules.add(new RegexpRule("^[ \\t]*\\*(?!/)[ \\t]*", getToken(SDocTokenType.WHITESPACE), true)); //$NON-NLS-1$
		rules.add(new SingleCharacterRule('\r', getToken(SDocTokenType.WHITESPACE)));
		rules.add(new SingleCharacterRule('\n', getToken(SDocTokenType.WHITESPACE)));

		rules.add(new SingleCharacterRule('#', getToken(SDocTokenType.POUND)));
		rules.add(new SingleCharacterRule('[', getToken(SDocTokenType.LBRACKET)));
		rules.add(new SingleCharacterRule(']', getToken(SDocTokenType.RBRACKET)));

		WordRule operatorRules = new WordRule(new OperatorDetector(), getToken(SDocTokenType.UNKNOWN));
		operatorRules.addWord("/**", getToken(SDocTokenType.START_DOCUMENTATION)); //$NON-NLS-1$
		operatorRules.addWord("*/", getToken(SDocTokenType.END_DOCUMENTATION)); //$NON-NLS-1$
		rules.add(operatorRules);

		rules.add(new PatternRule("{", "}", getToken(SDocTokenType.TYPES), '\0', false)); //$NON-NLS-1$ //$NON-NLS-2$

		WordRule tagRules = new WordRule(new TagDetector(), getToken(SDocTokenType.UNKNOWN));
		tagRules.addWord("@advanced", getToken(SDocTokenType.ADVANCED)); //$NON-NLS-1$
		tagRules.addWord("@alias", getToken(SDocTokenType.ALIAS)); //$NON-NLS-1$
		tagRules.addWord("@author", getToken(SDocTokenType.AUTHOR)); //$NON-NLS-1$
		tagRules.addWord("@classDescription", getToken(SDocTokenType.CLASS_DESCRIPTION)); //$NON-NLS-1$
		tagRules.addWord("@constructor", getToken(SDocTokenType.CONSTRUCTOR)); //$NON-NLS-1$
		tagRules.addWord("@example", getToken(SDocTokenType.EXAMPLE)); //$NON-NLS-1$
		tagRules.addWord("@exception", getToken(SDocTokenType.EXCEPTION)); //$NON-NLS-1$
		tagRules.addWord("@extends", getToken(SDocTokenType.EXTENDS)); //$NON-NLS-1$
		tagRules.addWord("@internal", getToken(SDocTokenType.INTERNAL)); //$NON-NLS-1$
		tagRules.addWord("@method", getToken(SDocTokenType.METHOD)); //$NON-NLS-1$
		tagRules.addWord("@namespace", getToken(SDocTokenType.NAMESPACE)); //$NON-NLS-1$
		tagRules.addWord("@overview", getToken(SDocTokenType.OVERVIEW)); //$NON-NLS-1$
		tagRules.addWord("@param", getToken(SDocTokenType.PARAM)); //$NON-NLS-1$
		tagRules.addWord("@private", getToken(SDocTokenType.PRIVATE)); //$NON-NLS-1$
		tagRules.addWord("@property", getToken(SDocTokenType.PROPERTY)); //$NON-NLS-1$
		tagRules.addWord("@return", getToken(SDocTokenType.RETURN)); //$NON-NLS-1$
		tagRules.addWord("@see", getToken(SDocTokenType.SEE)); //$NON-NLS-1$
		tagRules.addWord("@type", getToken(SDocTokenType.TYPE)); //$NON-NLS-1$
		rules.add(tagRules);

		rules.add(new RegexpRule("[^ \\t{\\[\\]#]+", getToken(SDocTokenType.TEXT), true)); //$NON-NLS-1$

		this.setDefaultReturnToken(getToken(SDocTokenType.ERROR));
		this.setRules(rules.toArray(new IRule[rules.size()]));
	}

	/**
	 * getToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken getToken(SDocTokenType type)
	{
		return new Token(type);
	}
}
