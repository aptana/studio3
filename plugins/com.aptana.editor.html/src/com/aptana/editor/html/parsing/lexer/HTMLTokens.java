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
package com.aptana.editor.html.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class HTMLTokens
{
	public static final short UNKNOWN = -1;
	public static final short EOF = 0;
	public static final short STYLE = 1;
	public static final short SCRIPT = 2;
	public static final short START_TAG = 3;
	public static final short TEXT = 4;
	public static final short END_TAG = 5;
	public static final short XML_DECL = 6;
	public static final short STRING = 7;
	public static final short COMMENT = 8;
	public static final short DOCTYPE = 9;
	public static final short STYLE_END = 10;
	public static final short SCRIPT_END = 11;
	public static final short CDATA = 12;

	private static final short MAXIMUM = 12;

	@SuppressWarnings("nls")
	private static final String[] NAMES = { "EOF", "STYLE", "SCRIPT", "START_TAG", "TEXT", "END_TAG", "XML_DECL",
			"STRING", "COMMENT", "DOCTYPE", "STYLE_END", "SCRIPT_END", "CDATA" };
	private static final String NAME_UNKNOWN = "UNKNOWN"; //$NON-NLS-1$

	private static Map<String, Short> nameIndexMap;

	public static String getTokenName(short token)
	{
		init();
		if (token < 0 || token > MAXIMUM)
		{
			return NAME_UNKNOWN;
		}
		return NAMES[token];
	}

	public static short getToken(String tokenName)
	{
		init();
		Short token = nameIndexMap.get(tokenName);
		return (token == null) ? UNKNOWN : token;
	}

	private static void init()
	{
		if (nameIndexMap == null)
		{
			nameIndexMap = new HashMap<String, Short>();
			short index = 0;
			for (String name : NAMES)
			{
				nameIndexMap.put(name, index++);
			}
		}
	}

	private HTMLTokens()
	{
	}
}
