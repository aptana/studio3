package com.aptana.editor.js.sdoc.parsing;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

public class SDocTypeTokenScanner extends RuleBasedScanner
{
	static class IdentifierDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			boolean result = false;

			switch (c)
			{
				case '$':
				case '_':
					result = true;
					break;

				default:
					result = Character.isJavaIdentifierStart(c);
			}

			return result;
		}

		public boolean isWordPart(char c)
		{
			boolean result = false;

			switch (c)
			{
				case '$':
				case '_':
				case '.':
					result = true;
					break;

				default:
					result = Character.isJavaIdentifierPart(c);
			}

			return result;
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
					case '>':
					case '.':
						result = true;
						break;
				}
			}
			else if (fPosition == 2)
			{
				switch (c)
				{
					case '.':
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
				case '-':
				case '.':
					result = true;
					break;
			}

			return result;
		}
	}

	/**
	 * SDocTypeTokenScanner
	 */
	public SDocTypeTokenScanner()
	{
		List<IRule> rules = new LinkedList<IRule>();

		rules.add(new WordRule(new WhitespaceDetector(), getToken(SDocTokenType.WHITESPACE)));
		
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('(', getToken(SDocTokenType.LPAREN));
		cmRule.add(')', getToken(SDocTokenType.RPAREN));
		cmRule.add('{', getToken(SDocTokenType.LCURLY));
		cmRule.add('}', getToken(SDocTokenType.RCURLY));
		cmRule.add('[', getToken(SDocTokenType.LBRACKET));
		cmRule.add(']', getToken(SDocTokenType.RBRACKET));
		cmRule.add('<', getToken(SDocTokenType.LESS_THAN));
		cmRule.add('>', getToken(SDocTokenType.GREATER_THAN));
		cmRule.add(':', getToken(SDocTokenType.COLON));
		cmRule.add(',', getToken(SDocTokenType.COMMA));
		cmRule.add('|', getToken(SDocTokenType.PIPE));
		cmRule.add('\r', getToken(SDocTokenType.WHITESPACE));
		cmRule.add('\r', getToken(SDocTokenType.WHITESPACE));
		rules.add(cmRule);
		
		WordRule keywordRules = new WordRule(new IdentifierDetector(), getToken(SDocTokenType.IDENTIFIER));
		keywordRules.addWord("Array", getToken(SDocTokenType.ARRAY)); //$NON-NLS-1$
		keywordRules.addWord("Function", getToken(SDocTokenType.FUNCTION)); //$NON-NLS-1$
		keywordRules.addWord("Class", getToken(SDocTokenType.CLASS)); //$NON-NLS-1$
		rules.add(keywordRules);
		
		WordRule operatorRules = new WordRule(new OperatorDetector(), getToken(SDocTokenType.ERROR));
		operatorRules.addWord("...", getToken(SDocTokenType.ELLIPSIS)); //$NON-NLS-1$
		operatorRules.addWord("->", getToken(SDocTokenType.ARROW)); //$NON-NLS-1$
		rules.add(operatorRules);

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
