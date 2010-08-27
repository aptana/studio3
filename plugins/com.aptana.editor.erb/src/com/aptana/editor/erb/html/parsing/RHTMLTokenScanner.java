package com.aptana.editor.erb.html.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.erb.parsing.lexer.ERBTokens;
import com.aptana.editor.html.parsing.HTMLTokenScanner;

public class RHTMLTokenScanner extends HTMLTokenScanner
{

	@SuppressWarnings("nls")
	private static final String[] RUBY_START = { "<%", "<%=" };
	@SuppressWarnings("nls")
	private static final String[] RUBY_END = new String[] { "-%>", "%>" };

	public RHTMLTokenScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// adds rules for finding the ruby start and end sequences
		WordRule wordRule = new WordRule(new RubyStartDetector(), Token.UNDEFINED);
		IToken token = createToken(getTokenName(ERBTokens.RUBY));
		for (String word : RUBY_START)
		{
			wordRule.addWord(word, token);
		}
		rules.add(wordRule);
		wordRule = new WordRule(new RubyEndDetector(), Token.UNDEFINED);
		token = createToken(getTokenName(ERBTokens.RUBY_END));
		for (String word : RUBY_END)
		{
			wordRule.addWord(word, token);
		}
		rules.add(wordRule);

		for (IRule rule : fRules)
		{
			rules.add(rule);
		}

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	private static String getTokenName(short token)
	{
		return ERBTokens.getTokenName(token);
	}

	private static final class RubyStartDetector implements IWordDetector
	{

		@Override
		public boolean isWordPart(char c)
		{
			switch (c)
			{
				case '<':
				case '%':
				case '=':
					return true;
			}
			return false;
		}

		@Override
		public boolean isWordStart(char c)
		{
			return c == '<';
		}
	}

	private static final class RubyEndDetector implements IWordDetector
	{

		@Override
		public boolean isWordPart(char c)
		{
			switch (c)
			{
				case '-':
				case '%':
				case '>':
					return true;
			}
			return false;
		}

		@Override
		public boolean isWordStart(char c)
		{
			return c == '-' || c == '%';
		}
	}
}
