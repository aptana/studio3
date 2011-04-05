/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

class JSNumberDetector implements IWordDetector
{
	/**
	 * isWordStart
	 * 
	 * @param c
	 */
	public boolean isWordStart(char c)
	{
		return isWordPart(c);
	}

	/**
	 * isWordPart
	 * 
	 * @param c
	 */
	public boolean isWordPart(char c)
	{
		boolean result;

		switch (c)
		{
			// leading plus or minus and plus or minus on exponent
			case '-':
			case '+':

				// decimal point
			case '.':

				// decimal digits
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':

				// hex digits - also covers e and E in scientific notation
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':

				// leading 0x and 0X for hex numbers
			case 'x':
			case 'X':
				result = true;
				break;

			default:
				result = false;
				break;
		}

		return result;
	}
}