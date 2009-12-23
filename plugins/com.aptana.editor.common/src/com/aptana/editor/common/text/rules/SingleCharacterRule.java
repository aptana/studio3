package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Optimized rule to match a single character. Faster than a RegexpRule for one char.
 * 
 * @author cwilliams
 */
public class SingleCharacterRule implements IPredicateRule
{

	private IToken successToken;
	private char c;

	public SingleCharacterRule(char c, IToken successToken)
	{
		this.c = c;
		this.successToken = successToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		if (c == (char) scanner.read())
		{
			return getSuccessToken();
		}
		scanner.unread();
		return Token.UNDEFINED;
	}

	@Override
	public IToken getSuccessToken()
	{
		return successToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{
		return evaluate(scanner, false);
	}
}
