/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
	ATTR_ID("entity.other.attribute-name.id.html"), //$NON-NLS-1$
	ATTR_CLASS("entity.other.attribute-name.class.html"), //$NON-NLS-1$
	ATTR_STYLE("entity.other.attribute-name.style.html"), //$NON-NLS-1$
	ATTR_SCRIPT("entity.other.attribute-name.script.html"), //$NON-NLS-1$
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
	private short _index;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, HTMLTokenType>();
		short index = 0;

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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ITypePredicate#getIndex()
	 */
	public short getIndex()
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
		return this != UNDEFINED;
	}

	/**
	 * toString
	 */
	public String toString()
	{
		return this.getScope();
	}
}
