package com.aptana.editor.css.internal.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Detects non-letters and digits (punctuation, special chars)
 * @author Chris Williams
 *
 */
public class SpecialCharacterWordDetector implements IWordDetector
{

	@Override
	public boolean isWordPart(char c)
	{
		return !Character.isLetterOrDigit(c);
	}

	@Override
	public boolean isWordStart(char c)
	{
		return !Character.isLetterOrDigit(c);
	}
}
