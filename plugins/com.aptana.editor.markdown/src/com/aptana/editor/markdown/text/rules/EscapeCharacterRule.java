package com.aptana.editor.markdown.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.ExtendedWordRule;

public class EscapeCharacterRule extends ExtendedWordRule
{
	public EscapeCharacterRule(IToken defaultToken)
	{
		super(new EscapeCharacterDetector(), defaultToken, false);
	}

	@Override
	protected boolean wordOK(String word, ICharacterScanner scanner)
	{
		if (word.length() != 2)
		{
			return false;
		}
		char c = word.charAt(1);
		switch (c)
		{
			case '\\':
			case '`':
			case '*':
			case '_':
			case '{':
			case '}':
			case '[':
			case ']':
			case '(':
			case ')':
			case '#':
			case '+':
			case '-':
			case '.':
			case '!':
				return true;
			default:
				return false;
		}
	}
}