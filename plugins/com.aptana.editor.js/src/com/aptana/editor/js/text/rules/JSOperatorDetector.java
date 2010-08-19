package com.aptana.editor.js.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Special "word" detector for finding JS operators.
 * 
 * @author cwilliams
 */
public class JSOperatorDetector implements IWordDetector
{
	private int fPosition;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(char c)
	{
		fPosition++;
		if (fPosition > 1)
		{
			switch (c)
			{
				case '=':
				case '>':
					return true;
				default:
					return false;
			}
		}
		switch (c)
		{
			case '&':
			case '-':
			case '+':
			case '=':
			case '<':
			case '>':
			case '|':
				return true;
			default:
				return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(char c)
	{
		fPosition = 0;
		switch (c)
		{
			case '!':
			case '%':
			case '&':
			case '*':
			case '-':
			case '+':
			case '=':
			case '<':
			case '>':
			case '|':
			case '/':
			case '^':
				return true;
			default:
				return false;
		}
	}
}