/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;

/**
 * @author Chris Williams
 */
public class YAMLCodeScanner extends BufferedRuleBasedScanner
{

	public YAMLCodeScanner()
	{
		List<IRule> rules = createRules();
		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(new Token("string.unquoted.yaml")); //$NON-NLS-1$
	}

	protected List<IRule> createRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Key/Property names
		WordRule rule = new YAMLKeyRule(new YAMLKeyDetector(), new Token("entity.name.tag.yaml"), false); //$NON-NLS-1$
		rule.setColumnConstraint(0);
		rules.add(rule);

		// Variables
		rule = new WordRule(new YAMLVariableDetector(), new Token("variable.other.yaml")); //$NON-NLS-1$
		rules.add(rule);

		// Numbers
		rule = new YAMLNumberRule(new YAMLNumberDetector(), new Token("constant.numeric.yaml"), false); //$NON-NLS-1$
		rules.add(rule);

		// Dates
		rule = new YAMLDateRule(new YAMLDateDetector(), new Token("constant.other.date.yaml"), false); //$NON-NLS-1$
		rules.add(rule);

		// Block/Unquoted strings
		rule = new WordRule(new YAMLUnquotedWordDetector(), new Token("string.unquoted.yaml")); //$NON-NLS-1$
		rules.add(rule);

		// Directive Separators
		rule = new YAMLDirectiveSeparatorRule(new Token("meta.separator.yaml")); //$NON-NLS-1$
		rules.add(rule);

		// Document Separators
		rule = new YAMLDocumentSeparatorRule(new Token("meta.separator.yaml")); //$NON-NLS-1$
		rules.add(rule);

		rules.add(new SingleCharacterRule('-', new Token("keyword.operator.symbol"))); //$NON-NLS-1$

		return rules;
	}

	/**
	 * Detects "...".
	 * 
	 * @author cwilliams
	 */
	private static final class YAMLDocumentSeparatorRule extends ExtendedWordRule
	{
		private YAMLDocumentSeparatorRule(IToken defaultToken)
		{
			super(new SingleCharacterDetector('.'), defaultToken, false);
			setColumnConstraint(0);
		}

		@Override
		protected boolean wordOK(String word, ICharacterScanner scanner)
		{
			return word.length() == 3;
		}
	}

	private static final class SingleCharacterDetector implements IWordDetector
	{
		private char fChar;

		SingleCharacterDetector(char c)
		{
			this.fChar = c;
		}

		public boolean isWordStart(char c)
		{
			return c == fChar;
		}

		public boolean isWordPart(char c)
		{
			return c == fChar;
		}
	}

	/**
	 * Detects "---".
	 * 
	 * @author cwilliams
	 */
	private static final class YAMLDirectiveSeparatorRule extends ExtendedWordRule
	{
		private YAMLDirectiveSeparatorRule(IToken defaultToken)
		{
			super(new SingleCharacterDetector('-'), defaultToken, false);
			setColumnConstraint(0);
		}

		@Override
		protected boolean wordOK(String word, ICharacterScanner scanner)
		{
			return word.length() == 3;
		}
	}

	/**
	 * Keys are just a "scalar" ending with a colon ':'
	 * 
	 * @author cwilliams
	 */
	private static final class YAMLKeyRule extends ExtendedWordRule
	{
		private YAMLKeyRule(IWordDetector detector, IToken defaultToken, boolean ignoreCase)
		{
			super(detector, defaultToken, ignoreCase);
		}

		protected boolean wordOK(String word, ICharacterScanner scanner)
		{
			if (word.length() < 2 || word.charAt(word.length() - 1) != ':')
			{
				return false;
			}
			return true;
		}
	}

	private static final class YAMLNumberRule extends ExtendedWordRule
	{
		private Pattern pattern;

		private YAMLNumberRule(IWordDetector detector, IToken defaultToken, boolean ignoreCase)
		{
			super(detector, defaultToken, ignoreCase);
		}

		protected boolean wordOK(String word, ICharacterScanner scanner)
		{
			return getPattern().matcher(word).matches();
		}

		private synchronized Pattern getPattern()
		{
			if (pattern == null)
			{
				pattern = Pattern
						.compile("(\\+|-)?((0(x|X|o|O)[0-9a-fA-F]*)|(([0-9]+\\.?[0-9]*)|(\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)?)(L|l|UL|ul|u|U|F|f)?"); //$NON-NLS-1$
			}
			return pattern;
		}
	}

	private static final class YAMLDateRule extends ExtendedWordRule
	{
		private Pattern pattern;

		private YAMLDateRule(IWordDetector detector, IToken defaultToken, boolean ignoreCase)
		{
			super(detector, defaultToken, ignoreCase);
		}

		protected boolean wordOK(String word, ICharacterScanner scanner)
		{
			if (word.length() != 10)
			{
				return false;
			}
			return getPattern().matcher(word).matches();
		}

		private synchronized Pattern getPattern()
		{
			if (pattern == null)
			{
				pattern = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})"); //$NON-NLS-1$
			}
			return pattern;
		}
	}

	private static final class YAMLUnquotedWordDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return Character.isLetterOrDigit(c);
		}

		public boolean isWordPart(char c)
		{
			return isWordStart(c) || c == '-' || c == '_' || c == '/' || c == '.';
		}
	}

	/**
	 * Word detector for YAML variable references.
	 * 
	 * @author cwilliams
	 */
	private static class YAMLVariableDetector implements IWordDetector
	{

		public boolean isWordStart(char c)
		{
			return c == '&' || c == '*';
		}

		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c) || c == '_' || c == '-';
		}
	}

	/**
	 * Word detector for YAML property keys. Just a "scalar" ending with a colon ':' See
	 * http://www.yaml.org/spec/1.2/spec.html#Characters TODO Disallow unprintable Unicode characters
	 * 
	 * @author cwilliams
	 */
	private static class YAMLKeyDetector implements IWordDetector
	{
		boolean stop = false;

		public boolean isWordStart(char c)
		{
			stop = false;

			switch (c)
			{
				case ',':
				case ':':
				case '?':
				case '[':
				case ']':
				case '{':
				case '}':
				case '\r':
				case '\n':
					return false;
				default:
					return true;
			}
		}

		public boolean isWordPart(char c)
		{
			if (stop)
			{
				stop = false;
				return false;
			}

			switch (c)
			{
				case ':':
					stop = true;
					return true;
				case ',':
				case '?':
				case '[':
				case ']':
				case '{':
				case '}':
				case '\r':
				case '\n':
					return false;
				default:
					return true;
			}
		}

	}

	/**
	 * Word detector for YAML numbers.
	 * 
	 * @author cwilliams
	 */
	private static class YAMLNumberDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return Character.isDigit(c) || c == '.' || c == '-' || c == '+';
		}

		public boolean isWordPart(char c)
		{
			if (isWordStart(c))
			{
				return true;
			}
			c = Character.toLowerCase(c);
			return c == 'x' || c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'l'
					|| c == 'u' || c == 'o';
		}
	}

	/**
	 * Word detector for YAML dates.
	 * 
	 * @author cwilliams
	 */
	private static class YAMLDateDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return Character.isDigit(c);
		}

		public boolean isWordPart(char c)
		{
			return isWordStart(c) || c == '-';
		}
	}
}
