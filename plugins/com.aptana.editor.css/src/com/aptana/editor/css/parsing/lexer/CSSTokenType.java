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
package com.aptana.editor.css.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum CSSTokenType implements ITypePredicate
{
	UNDEFINED("undefined.css", -1), //$NON-NLS-1$
	PROPERTY("support.type.property-name.css", 16), //$NON-NLS-1$
	MEDIA("support.constant.media.css", 1), //$NON-NLS-1$
	FUNCTION("support.function.misc.css", 1), //$NON-NLS-1$
	COLOR("support.constant.color.w3c-standard-color-name.css", 2), //$NON-NLS-1$
	DEPRECATED_COLOR("invalid.deprecated.color.w3c-non-standard-color-name.css", 2), //$NON-NLS-1$
	LCURLY("punctuation.section.property-list.css", 13), //$NON-NLS-1$
	RCURLY("punctuation.section.property-list.css", 4), //$NON-NLS-1$
	COLON("punctuation.separator.key-value.css", 3), //$NON-NLS-1$
	SEMICOLON("punctuation.terminator.rule.css", 5), //$NON-NLS-1$
	RGB("constant.other.color.rgb-value.css", 2), //$NON-NLS-1$
	ID("entity.other.attribute-name.id.css", 9), //$NON-NLS-1$
	CLASS("entity.other.attribute-name.class.css", 8), //$NON-NLS-1$
	NUMBER("constant.numeric.css", 17), //$NON-NLS-1$
	AT_RULE("keyword.control.at-rule.media.css", 26), //$NON-NLS-1$
	IDENTIFIER("source.css", 1), //$NON-NLS-1$
	DOUBLE_QUOTED_STRING("string.quoted.double.css", 6), //$NON-NLS-1$
	SINGLE_QUOTED_STRING("string.quoted.single.css", 6), //$NON-NLS-1$
	COMMENT("comment.block.css", 42), //$NON-NLS-1$	
	COMMA("punctuation.separator.css", 15), //$NON-NLS-1$
	SLASH("punctuation.slash.css", 34), //$NON-NLS-1$
	STAR("punctuation.asterisk.css", 11), //$NON-NLS-1$
	PERCENTAGE("keyword.other.unit.css", 18), //$NON-NLS-1$
	ELEMENT("entity.name.tag.css", 1), //$NON-NLS-1$
	FONT("support.constant.font-name.css", 1), //$NON-NLS-1$
	VALUE("support.constant.property-value.css", 1), //$NON-NLS-1$
	
	// Stuff for the parser only:
	EOF("", 0), //$NON-NLS-1$
	LBRACKET("punctuation.bracket.css", 7), //$NON-NLS-1$
	SELECTOR(".css", 12), //$NON-NLS-1$
	URL(".css", 10), //$NON-NLS-1$
	LENGTH("keyword.other.unit.css", 19), //$NON-NLS-1$
	EMS("keyword.other.unit.css", 20), //$NON-NLS-1$
	EXS("keyword.other.unit.css", 21), //$NON-NLS-1$
	ANGLE("keyword.other.unit.css", 22), //$NON-NLS-1$
	TIME("keyword.other.unit.css", 23), //$NON-NLS-1$
	FREQUENCY("keyword.other.unit.css", 24), //$NON-NLS-1$
	PAGE("keyword.control.at-rule.page.css", 25), //$NON-NLS-1$
	CHARSET("keyword.control.at-rule.charset.css", 27), //$NON-NLS-1$
	MEDIA_KEYWORD("keyword.control.at-rule.import.css", 28), //$NON-NLS-1$
	FONTFACE("keyword.control.at-rule.fontface.css", 29), //$NON-NLS-1$
	NAMESPACE("keyword.control.at-rule.namespace.css", 30), //$NON-NLS-1$
	RBRACKET("punctuation.bracket.css", 31), //$NON-NLS-1$
	IMPORT("keyword.control.at-rule.import.css", 32), //$NON-NLS-1$
	PLUS("punctuation.plus.css", 33), //$NON-NLS-1$
	MINUS("punctuation.minus.css", 35), //$NON-NLS-1$
	LPAREN("punctuation.section.function.css", 14), //$NON-NLS-1$
	RPAREN("punctuation.section.function.css", 36), //$NON-NLS-1$
	IMPORTANT("support.constant.property-value.css", 37), //$NON-NLS-1$
	GREATER("punctuation.greater.css", 38), //$NON-NLS-1$
	EQUAL("punctuation.equal.css", 39), //$NON-NLS-1$
	INCLUDES("keyword.control.at-rule.include.css", 40), //$NON-NLS-1$
	DASHMATCH(".css", 41); //$NON-NLS-1$
	
	private static final Map<String, CSSTokenType> NAME_MAP;
	private String _scope;
	private short beaverId;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, CSSTokenType>();

		for (CSSTokenType type : EnumSet.allOf(CSSTokenType.class))
		{
			NAME_MAP.put(type.getScope(), type);
		}
	}

	/**
	 * get
	 * 
	 * @param scope
	 * @return
	 */
	public static final CSSTokenType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param scope
	 */
	private CSSTokenType(String scope, int beaverId)
	{
		this._scope = scope;
		this.beaverId = (short) beaverId;
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
	
	public short getShort()
	{
		return beaverId;
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
		return this.getShort() + ": " + this.getScope(); //$NON-NLS-1$
	}
}
