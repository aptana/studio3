package com.aptana.editor.js.sdoc.parsing;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
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

		rules.add(new RegexpRule("[ \\t]+", getToken(SDocTokenType.WHITESPACE), true)); //$NON-NLS-1$
		rules.add(new SingleCharacterRule('\r', getToken(SDocTokenType.WHITESPACE)));
		rules.add(new SingleCharacterRule('\n', getToken(SDocTokenType.WHITESPACE)));

		rules.add(new SingleCharacterRule('(', getToken(SDocTokenType.LPAREN)));
		rules.add(new SingleCharacterRule(')', getToken(SDocTokenType.RPAREN)));
		rules.add(new SingleCharacterRule('{', getToken(SDocTokenType.LCURLY)));
		rules.add(new SingleCharacterRule('}', getToken(SDocTokenType.RCURLY)));
		rules.add(new SingleCharacterRule('[', getToken(SDocTokenType.LBRACKET)));
		rules.add(new SingleCharacterRule(']', getToken(SDocTokenType.RBRACKET)));
		rules.add(new SingleCharacterRule('<', getToken(SDocTokenType.LESS_THAN)));
		rules.add(new SingleCharacterRule('>', getToken(SDocTokenType.GREATER_THAN)));
		rules.add(new SingleCharacterRule(':', getToken(SDocTokenType.COLON)));
		rules.add(new SingleCharacterRule(',', getToken(SDocTokenType.COMMA)));
		rules.add(new SingleCharacterRule('|', getToken(SDocTokenType.PIPE)));

		WordRule operatorRules = new WordRule(new OperatorDetector(), getToken(SDocTokenType.ERROR));
		operatorRules.addWord("...", getToken(SDocTokenType.ELLIPSIS)); //$NON-NLS-1$
		operatorRules.addWord("->", getToken(SDocTokenType.ARROW)); //$NON-NLS-1$
		rules.add(operatorRules);

		WordRule keywordRules = new WordRule(new IdentifierDetector(), getToken(SDocTokenType.IDENTIFIER));
		keywordRules.addWord("Array", getToken(SDocTokenType.ARRAY)); //$NON-NLS-1$
		keywordRules.addWord("Function", getToken(SDocTokenType.FUNCTION)); //$NON-NLS-1$
		keywordRules.addWord("Class", getToken(SDocTokenType.CLASS)); //$NON-NLS-1$
		rules.add(keywordRules);

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
