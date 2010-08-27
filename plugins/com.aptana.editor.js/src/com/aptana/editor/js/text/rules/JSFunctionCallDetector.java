package com.aptana.editor.js.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Special word detector to detect calls to functions (leading period plus function name)
 * 
 * @author cwilliams
 */
public class JSFunctionCallDetector implements IWordDetector
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(char c)
	{
		return Character.isLetter(c) || c == '_' || c == '$' || Character.isDigit(c);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(char c)
	{
		return c == '.';
	}
}