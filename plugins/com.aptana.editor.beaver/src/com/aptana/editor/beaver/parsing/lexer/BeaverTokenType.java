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
package com.aptana.editor.beaver.parsing.lexer;

import java.util.EnumSet;

@SuppressWarnings("nls")
public enum BeaverTokenType
{
	STRING_DOUBLE("string.quoted.double.js"),
	STRING_SINGLE("string.quoted.single.js"),
	HEADER("keyword.operator.beaver"),
	PACKAGE("keyword.operator.beaver"),
	IMPORT("keyword.operator.beaver"),
	CLASS("keyword.operator.beaver"),
	EMBED("keyword.operator.beaver"),
	INIT("keyword.operator.beaver"),
	TERMINALS("keyword.operator.beaver"),
	TYPEOF("keyword.operator.beaver"),
	LEFT("keyword.operator.beaver"),
	RIGHT("keyword.operator.beaver"),
	GOAL("keyword.operator.beaver"),
	IMPLEMENTS("keyword.operator.beaver"),
	BLOCK("keyword.operator.beaver"),
	SEMICOLON("keyword.operator.beaver"),
	EQUAL("keyword.operator.beaver"),
	PIPE("keyword.operator.beaver"),
	QUESTION("keyword.operator.beaver"),
	STAR("keyword.operator.beaver"),
	PLUS("keyword.operator.beaver"),
	IDENTIFIER("source.identifier.beaver"),
	COMMENT("comment.line.double-slash.beaver"),
	MULTILINE_COMMENT("");

	private short _index;
	private String _scope;

	/**
	 * static initializer
	 */
	static
	{
		short index = -1;

		for (BeaverTokenType type : EnumSet.allOf(BeaverTokenType.class))
		{
			type._index = index++;
		}
	}

	/**
	 * BeaverTokenType
	 * 
	 * @param scope
	 */
	private BeaverTokenType(String scope)
	{
		this._scope = scope;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public short getIndex()
	{
		return this._index;
	}

	/**
	 * getScope
	 * 
	 * @return
	 */
	public String getScope()
	{
		return this._scope;
	}
}
