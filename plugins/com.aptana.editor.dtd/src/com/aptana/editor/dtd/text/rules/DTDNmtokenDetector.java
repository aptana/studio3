/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

public class DTDNmtokenDetector implements IWordDetector
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c)
	{
		return isWordStart(c);
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
		||	(0x10000 <= c && c <= 0xEFFFF)
		||	c == '-'
		||	c == '.'
		||	('0' <= c && c <= '9')
		||	c == 0xB7
		||	(0x300 <= c && c <= 0x36F)
		||	(0x203F <= c && c <= 0x2040);
	}
}
