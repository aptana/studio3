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
	private char _lastChar;
	private boolean _inExponent;
	private boolean _seenDecimalPoint;
	private boolean _inHex;

	/**
	 * isWordStart
	 * 
	 * @param c
	 */
	public boolean isWordStart(char c)
	{
		// reset state
		this._lastChar = '\0';
		this._inExponent = false;
		this._seenDecimalPoint = false;
		this._inHex = false;

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

		// NOTE: There's no need to set flags on success only since the overall rule will fail anyway
		switch (c)
		{
			// plus or minus on exponent
			case '-':
			case '+':
				result = (this._lastChar == 'e' || this._lastChar == 'E');
				break;

			// leading 0x and 0X for hex numbers
			case 'x':
			case 'X':
				result = (this._lastChar == '0');
				this._inHex = true;
				break;

			// decimal point
			case '.':
				result = (!this._inExponent && !this._seenDecimalPoint);
				this._seenDecimalPoint = true;
				break;

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
				result = true;
				break;

			// hex digits - e and E are covered in the scientific notation logic below
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'f':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'F':
				result = this._inHex;
				break;

			// exponent
			case 'e':
			case 'E':
				result = (this._inHex || !this._inExponent);
				this._inExponent = !this._inHex;
				break;

			default:
				result = false;
				break;
		}

		this._lastChar = c;

		return result;
	}
}