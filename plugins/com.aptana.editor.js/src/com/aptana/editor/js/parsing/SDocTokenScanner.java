package com.aptana.editor.js.parsing;

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
import com.aptana.editor.js.parsing.lexer.SDocTokenType;

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
	
	static class KeywordDetector implements IWordDetector
	{
		public boolean isWordPart(char c)
		{
			return Character.isLetter(c);
		}

		public boolean isWordStart(char c)
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
					case '>':
					case '.':
					case '*':
						result = true;
						break;
				}
			}
			else if (fPosition == 2)
			{
				switch (c)
				{
					case '.':
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
				case '-':
				case '*':
				case '.':
					result = true;
					break;
			}
			
			return result;
		}
	}
	
	public SDocTokenScanner()
	{
		List<IRule> rules = new LinkedList<IRule>();
		
		rules.add(new RegexpRule("[ \\t]*\\*(?!/)[ \\t]*", getToken(SDocTokenType.WHITESPACE), true));
		rules.add(new RegexpRule("[ \\t]+", getToken(SDocTokenType.WHITESPACE)));
		rules.add(new RegexpRule("\\r\\n|\\r|\\n", getToken(SDocTokenType.WHITESPACE)));

		rules.add(new SingleCharacterRule('(', getToken(SDocTokenType.LPAREN)));
		rules.add(new SingleCharacterRule(')', getToken(SDocTokenType.RPAREN)));
		rules.add(new SingleCharacterRule('{', getToken(SDocTokenType.LCURLY)));
		rules.add(new SingleCharacterRule('}', getToken(SDocTokenType.RCURLY)));
		rules.add(new SingleCharacterRule('[', getToken(SDocTokenType.LBRACKET)));
		rules.add(new SingleCharacterRule(']', getToken(SDocTokenType.RBRACKET)));
		rules.add(new SingleCharacterRule('<', getToken(SDocTokenType.LESS_THAN)));
		rules.add(new SingleCharacterRule('>', getToken(SDocTokenType.GREATER_THAN)));
		
		rules.add(new SingleCharacterRule(':', getToken(SDocTokenType.COLON)));
		rules.add(new SingleCharacterRule('#', getToken(SDocTokenType.POUND)));
		rules.add(new SingleCharacterRule(',', getToken(SDocTokenType.COMMA)));
		rules.add(new SingleCharacterRule('|', getToken(SDocTokenType.PIPE)));
		
		WordRule operatorRules = new WordRule(new OperatorDetector(), getToken(SDocTokenType.UNKNOWN));
		operatorRules.addWord("/**", getToken(SDocTokenType.START_DOCUMENTATION));
		operatorRules.addWord("->", getToken(SDocTokenType.ARROW));
		operatorRules.addWord("...", getToken(SDocTokenType.ELLIPSIS));
		operatorRules.addWord("*/", getToken(SDocTokenType.END_DOCUMENTATION));
		rules.add(operatorRules);
		
		WordRule tagRules = new WordRule(new TagDetector(), getToken(SDocTokenType.UNKNOWN));
		tagRules.addWord("@advanced", getToken(SDocTokenType.ADVANCED));
		tagRules.addWord("@alias", getToken(SDocTokenType.ALIAS));
		tagRules.addWord("@author", getToken(SDocTokenType.AUTHOR));
		tagRules.addWord("@classDescription", getToken(SDocTokenType.CLASS_DESCRIPTION));
		tagRules.addWord("@constructor", getToken(SDocTokenType.CONSTRUCTOR));
		tagRules.addWord("@example", getToken(SDocTokenType.EXAMPLE));
		tagRules.addWord("@exception", getToken(SDocTokenType.EXCEPTION));
		tagRules.addWord("@extends", getToken(SDocTokenType.EXTENDS));
		tagRules.addWord("@internal", getToken(SDocTokenType.INTERNAL));
		tagRules.addWord("@method", getToken(SDocTokenType.METHOD));
		tagRules.addWord("@namespace", getToken(SDocTokenType.NAMESPACE));
		tagRules.addWord("@overview", getToken(SDocTokenType.OVERVIEW));
		tagRules.addWord("@param", getToken(SDocTokenType.PARAM));
		tagRules.addWord("@private", getToken(SDocTokenType.PRIVATE));
		tagRules.addWord("@property", getToken(SDocTokenType.PROPERTY));
		tagRules.addWord("@return", getToken(SDocTokenType.RETURN));
		tagRules.addWord("@see", getToken(SDocTokenType.SEE));
		rules.add(tagRules);
		
		WordRule keywordRules = new WordRule(new KeywordDetector(), getToken(SDocTokenType.IDENTIFIER));
		keywordRules.addWord("array", getToken(SDocTokenType.ARRAY));
		keywordRules.addWord("function", getToken(SDocTokenType.FUNCTION));
		rules.add(keywordRules);
		
		rules.add(new WordRule(new IdentifierDetector(), getToken(SDocTokenType.IDENTIFIER)));
		
		this.setDefaultReturnToken(getToken(SDocTokenType.TEXT));
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
