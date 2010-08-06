package com.aptana.editor.css.internal.text.rules;

/**
 * Detects words starting with the provided character and containing letters or digits.
 * 
 * @author Chris Williams
 *
 */
public class IdentifierWithPrefixDetector extends KeywordIdentifierDetector
{
	private char start;
	
	public IdentifierWithPrefixDetector(char start)
	{
		this.start = start;
	}
	
	@Override
	public boolean isWordStart(char c) 
	{
		return c == start;
	}
}
