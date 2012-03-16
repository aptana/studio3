package com.aptana.editor.css.internal.text.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

/**
 * @author cwilliams
 */
public class CSSURLRule extends ExtendedWordRule
{

	private static class CSSURLWordDetector implements IWordDetector
	{
		private boolean foundEnd;

		public boolean isWordStart(char c)
		{
			foundEnd = false;
			return c == 'u' || c == 'U';
		}

		public boolean isWordPart(char c)
		{
			if (c == ')')
			{
				foundEnd = true;
				return true;
			}
			return !foundEnd;
		}
	}

	private Pattern pattern;

	/**
	 * CSSURLRule
	 * 
	 * @param token
	 */
	public CSSURLRule(IToken token)
	{
		super(new CSSURLWordDetector(), token, true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ExtendedWordRule#wordOK(java.lang.String,
	 * org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		if (pattern == null)
		{
			pattern = Pattern.compile("url\\([^)]*\\)", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
		}

		return pattern.matcher(word).matches();
	}
}
