package com.aptana.editor.css.internal.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Detects words consisting only of letters, digits, '-', and '_'. Must start with letter
 * 
 * @author Chris Williams
 */
public class KeywordIdentifierDetector implements IWordDetector
{
	@Override
	public boolean isWordPart(char c)
	{
		return Character.isLetterOrDigit(c) || c == '-' || c == '_';
	}

	@Override
	public boolean isWordStart(char c)
	{
		return Character.isLetter(c);
	}
}