/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.editor.css.parsing.Terminals;
import com.aptana.parsing.lexer.ITypePredicate;

public enum CSSTokenType implements ITypePredicate
{
	PROPERTY("support.type.property-name.css", Terminals.PROPERTY), //$NON-NLS-1$
	MEDIA("support.constant.media.css", Terminals.IDENTIFIER), //$NON-NLS-1$
	FUNCTION("support.function.misc.css", Terminals.IDENTIFIER), //$NON-NLS-1$
	COLOR("support.constant.color.w3c-standard-color-name.css", Terminals.COLOR), //$NON-NLS-1$
	DEPRECATED_COLOR("invalid.deprecated.color.w3c-non-standard-color-name.css", Terminals.COLOR), //$NON-NLS-1$
	LCURLY("punctuation.section.property-list.css", Terminals.LCURLY), //$NON-NLS-1$
	RCURLY("punctuation.section.property-list.css", Terminals.RCURLY), //$NON-NLS-1$
	COLON("punctuation.separator.key-value.css", Terminals.COLON), //$NON-NLS-1$
	SEMICOLON("punctuation.terminator.rule.css", Terminals.SEMICOLON), //$NON-NLS-1$
	RGB("constant.other.color.rgb-value.css", Terminals.COLOR), //$NON-NLS-1$
	ID("entity.other.attribute-name.id.css", Terminals.HASH), //$NON-NLS-1$
	CLASS("entity.other.attribute-name.class.css", Terminals.CLASS), //$NON-NLS-1$
	NUMBER("constant.numeric.css", Terminals.NUMBER), //$NON-NLS-1$
	AT_RULE("keyword.control.at-rule.media.css", Terminals.AT_RULE), //$NON-NLS-1$
	IDENTIFIER("source.css", Terminals.IDENTIFIER), //$NON-NLS-1$
	DOUBLE_QUOTED_STRING("string.quoted.double.css", Terminals.STRING), //$NON-NLS-1$
	SINGLE_QUOTED_STRING("string.quoted.single.css", Terminals.STRING), //$NON-NLS-1$
	COMMA("punctuation.separator.css", Terminals.COMMA), //$NON-NLS-1$
	SLASH("punctuation.slash.css", Terminals.SLASH), //$NON-NLS-1$
	STAR("punctuation.asterisk.css", Terminals.STAR), //$NON-NLS-1$
	PERCENTAGE("keyword.other.unit.css", Terminals.PERCENTAGE), //$NON-NLS-1$
	ELEMENT("entity.name.tag.css", Terminals.IDENTIFIER), //$NON-NLS-1$
	FONT("support.constant.font-name.css", Terminals.IDENTIFIER), //$NON-NLS-1$
	VALUE("support.constant.property-value.css", Terminals.IDENTIFIER), //$NON-NLS-1$
	
	// Stuff for the parser only:
	EOF("", Terminals.EOF), //$NON-NLS-1$
	LBRACKET("punctuation.bracket.css", Terminals.LBRACKET), //$NON-NLS-1$
	SELECTOR(".css", Terminals.SELECTOR), //$NON-NLS-1$
	URL(".css", Terminals.URL), //$NON-NLS-1$
	LENGTH("keyword.other.unit.css", Terminals.LENGTH), //$NON-NLS-1$
	EMS("keyword.other.unit.css", Terminals.EMS), //$NON-NLS-1$
	EXS("keyword.other.unit.css", Terminals.EXS), //$NON-NLS-1$
	ANGLE("keyword.other.unit.css", Terminals.ANGLE), //$NON-NLS-1$
	TIME("keyword.other.unit.css", Terminals.TIME), //$NON-NLS-1$
	FREQUENCY("keyword.other.unit.css", Terminals.FREQUENCY), //$NON-NLS-1$
	PAGE("keyword.control.at-rule.page.css", Terminals.PAGE), //$NON-NLS-1$
	CHARSET("keyword.control.at-rule.charset.css", Terminals.CHARSET), //$NON-NLS-1$
	MEDIA_KEYWORD("keyword.control.at-rule.import.css", Terminals.MEDIA), //$NON-NLS-1$
	FONTFACE("keyword.control.at-rule.fontface.css", Terminals.FONTFACE), //$NON-NLS-1$
	NAMESPACE("keyword.control.at-rule.namespace.css", Terminals.NAMESPACE), //$NON-NLS-1$
	RBRACKET("punctuation.bracket.css", Terminals.RBRACKET), //$NON-NLS-1$
	IMPORT("keyword.control.at-rule.import.css", Terminals.IMPORT), //$NON-NLS-1$
	PLUS("punctuation.plus.css", Terminals.PLUS), //$NON-NLS-1$
	MINUS("punctuation.minus.css", Terminals.MINUS), //$NON-NLS-1$
	LPAREN("punctuation.section.function.css", Terminals.LPAREN), //$NON-NLS-1$
	RPAREN("punctuation.section.function.css", Terminals.RPAREN), //$NON-NLS-1$
	IMPORTANT("support.constant.property-value.css", Terminals.IMPORTANT), //$NON-NLS-1$
	GREATER("punctuation.greater.css", Terminals.GREATER), //$NON-NLS-1$
	EQUAL("punctuation.equal.css", Terminals.EQUAL), //$NON-NLS-1$
	INCLUDES("keyword.control.at-rule.include.css", Terminals.INCLUDES), //$NON-NLS-1$
	DASHMATCH(".css", Terminals.DASHMATCH), //$NON-NLS-1$
	
	UNDEFINED("undefined.css", -1), //$NON-NLS-1$
	COMMENT("comment.block.css", 42); //$NON-NLS-1$
	
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
