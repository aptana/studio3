/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

public class DTDOperatorWordDetector implements IWordDetector
{
	private int _position;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c)
	{
		this._position++;

		switch (this._position)
		{
			case 1:
				switch (c)
				{
					case '*':
					case ']':
					case '!':
						return true;

					default:
						return false;
				}

			case 2:
				switch (c)
				{
					case '[':
					case '>':
						return true;

					default:
						return false;
				}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c)
	{
		this._position = 0;

		switch (c)
		{
			case '<':
			case ']':
			case ')':
				return true;

			default:
				return false;
		}
	}

}
