/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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