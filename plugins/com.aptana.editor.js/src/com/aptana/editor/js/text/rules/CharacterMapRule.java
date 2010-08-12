package com.aptana.editor.js.text.rules;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CharacterMapRule implements IPredicateRule
{
	private Map<Character,IToken> characterTokenMap;
	private IToken successToken;
	
	/**
	 * CharacterMapRule
	 */
	public CharacterMapRule()
	{
		characterTokenMap = new HashMap<Character,IToken>();
	}
	
	/**
	 * add
	 * 
	 * @param c
	 * @param token
	 */
	public void add(char c, IToken token)
	{
		characterTokenMap.put(c, token);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner, boolean)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		successToken = characterTokenMap.get((char) scanner.read());
			
		if (successToken == null)
		{
			scanner.unread();
			successToken = Token.UNDEFINED;
		}
	
		return successToken;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	@Override
	public IToken getSuccessToken()
	{
		return successToken;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{
		return evaluate(scanner, false);
	}
}
