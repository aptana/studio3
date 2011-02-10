/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

public class DTDEntityDetector implements IWordDetector
{
	private DTDNameDetector _detector = new DTDNameDetector();
	private int _index;
	private char _startingChararacter;
	private boolean _done;

	/**
	 * DTDEntityDetector
	 * 
	 * @param startingCharacter
	 */
	public DTDEntityDetector(char startingCharacter)
	{
		this._startingChararacter = startingCharacter;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c)
	{
		boolean result = false;

		this._index++;

		if (this._done == false)
		{
			if (this._index == 1)
			{
				result = this._detector.isWordStart(c);
			}
			else if (c == ';')
			{
				this._done = true;

				result = true;
			}
			else
			{
				result = this._detector.isWordPart(c);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c)
	{
		this._index = 0;
		this._done = false;

		return c == this._startingChararacter;
	}
}
