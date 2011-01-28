/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

public class DTDNameDetector implements IWordDetector
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c)
	{
		return
			isWordStart(c)
		||	c == '-'
		||	c == '.'
		||	('0' <= c && c <= '9')
		||	c == 0xB7
		||	(0x300 <= c && c <= 0x36F)
		||	(0x203F <= c && c <= 0x2040);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c)
	{
		return
			c == ':'
		||	('A' <= c && c <= 'Z')
		||	c == '_'
		||	('a' <= c && c <= 'z')
		||	(0xC0 <= c && c <= 0xD6)
		||	(0xD8 <= c && c <= 0xF6)
		||	(0xF8 <= c && c <= 0x2FF)
		||	(0x370 <= c && c <= 0x37D)
		||	(0x37F <= c && c <= 0x1FFF)
		||	(0x200C <= c && c <= 0x200D)
		||	(0x2070 <= c && c <= 0x218F)
		||	(0x2C00 <= c && c <= 0x2FEF)
		||	(0x3001 <= c && c <= 0xD7FF)
		||	(0xF900 <= c && c <= 0xFDCF)
		||	(0xFDF0 <= c && c <= 0xFFFD)
		||	(0x10000 <= c && c <= 0xEFFFF);
	}
}
