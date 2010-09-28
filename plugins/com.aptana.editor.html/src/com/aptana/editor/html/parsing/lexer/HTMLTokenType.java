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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum HTMLTokenType implements ITypePredicate
{
	UNDEFINED("undefined.html"), //$NON-NLS-1$
	DOUBLE_QUOTED_STRING("string.quoted.double.html"), //$NON-NLS-1$
	SINGLE_QUOTED_STRING("string.quoted.single.html"), //$NON-NLS-1$
	ATTRIBUTE("entity.other.attribute-name.html"), //$NON-NLS-1$
	ID("entity.other.attribute-name.id.html"), //$NON-NLS-1$
	CLASS("entity.other.attribute-name.class.html"), //$NON-NLS-1$
	META("meta.tag.other.html"), //$NON-NLS-1$
	SCRIPT("entity.name.tag.script.html"), //$NON-NLS-1$
	STYLE("entity.name.tag.style.html"), //$NON-NLS-1$
	STRUCTURE_TAG("entity.name.tag.structure.any.html"), //$NON-NLS-1$
	BLOCK_TAG("entity.name.tag.block.any.html"), //$NON-NLS-1$
	INLINE_TAG("entity.name.tag.inline.any.html"), //$NON-NLS-1$
	TAG_END("punctuation.definition.tag.end.html"), //$NON-NLS-1$
	EQUAL("punctuation.separator.key-value.html"), //$NON-NLS-1$
	TAG_START("punctuation.definition.tag.begin.html"), //$NON-NLS-1$
	TEXT("text"), //$NON-NLS-1$
	TAG_SELF_CLOSE("punctuation.definition.tag.self_close.html"); //$NON-NLS-1$

	private static final Map<String, HTMLTokenType> NAME_MAP;
	private String _scope;
	private int _index;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, HTMLTokenType>();
		int index = 0;

		for (HTMLTokenType type : EnumSet.allOf(HTMLTokenType.class))
		{
			NAME_MAP.put(type.getScope(), type);
			type._index = index++;
		}
	}

	/**
	 * get
	 * 
	 * @param scope
	 * @return
	 */
	public static final HTMLTokenType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param scope
	 */
	private HTMLTokenType(String scope)
	{
		this._scope = scope;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return this._index;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getScope()
	{
		return this._scope;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.lexer.ITypePredicate#isDefined()
	 */
	public boolean isDefined()
	{
		return (this != UNDEFINED);
	}

	/**
	 * toString
	 */
	public String toString()
	{
		return this.getScope();
	}
}
