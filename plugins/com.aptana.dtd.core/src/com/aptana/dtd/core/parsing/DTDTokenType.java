/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing;

@SuppressWarnings("nls")
public enum DTDTokenType
{
	UNDEFINED("", -1),
	EOF("", Terminals.EOF),
	NAME("source.dtd", Terminals.NAME),
	GREATER_THAN("source.dtd", Terminals.GREATER_THAN),
	LPAREN("source.dtd", Terminals.LPAREN),
	STRING("keyword.operator.dtd", Terminals.STRING),
	RPAREN("source.dtd", Terminals.RPAREN),
	PIPE("source.dtd", Terminals.PIPE),
	STAR("source.dtd", Terminals.STAR),
	ENTITY("keyword.operator.dtd", Terminals.ENTITY),
	ELEMENT("keyword.operator.dtd", Terminals.ELEMENT),
	SECTION_START("keyword.operator.dtd", Terminals.SECTION_START),
	ATTLIST("keyword.operator.dtd", Terminals.ATTLIST),
	NOTATION("keyword.operator.dtd", Terminals.NOTATION),
	PE_REF("entity.reference.parameter.dtd", Terminals.PE_REF),
	PI("source.dtd", Terminals.PI),
	COMMENT("comment.dtd", Terminals.COMMENT),
	PUBLIC("keyword.operator.dtd", Terminals.PUBLIC),
	QUESTION("source.dtd", Terminals.QUESTION),
	PLUS("source.dtd", Terminals.PLUS),
	SYSTEM("keyword.operator.dtd", Terminals.SYSTEM),
	LBRACKET("keyword.operator.dtd", Terminals.LBRACKET),
	SECTION_END("keyword.operator.dtd", Terminals.SECTION_END),
	COMMA("source.dtd", Terminals.COMMA),
	PCDATA("keyword.operator.dtd", Terminals.PCDATA),
	PERCENT("keyword.operator.dtd", Terminals.PERCENT),
	EMPTY("keyword.operator.dtd", Terminals.EMPTY),
	ANY("keyword.operator.dtd", Terminals.ANY),
	INCLUDE("keyword.operator.dtd", Terminals.INCLUDE),
	IGNORE("keyword.operator.dtd", Terminals.IGNORE),
	FIXED("keyword.operator.dtd", Terminals.FIXED),
	NOTATION_TYPE("keyword.operator.dtd", Terminals.NOTATION_TYPE),
	REQUIRED("keyword.operator.dtd", Terminals.REQUIRED),
	IMPLIED("keyword.operator.dtd", Terminals.IMPLIED),
	NDATA("keyword.operator.dtd", Terminals.NDATA),
	CDATA_TYPE("keyword.operator.dtd", Terminals.CDATA_TYPE),
	ID_TYPE("keyword.operator.dtd", Terminals.ID_TYPE),
	IDREF_TYPE("keyword.operator.dtd", Terminals.IDREF_TYPE),
	IDREFS_TYPE("keyword.operator.dtd", Terminals.IDREFS_TYPE),
	ENTITY_TYPE("keyword.operator.dtd", Terminals.ENTITY_TYPE),
	ENTITIES_TYPE("keyword.operator.dtd", Terminals.ENTITIES_TYPE),
	NMTOKEN_TYPE("keyword.operator.dtd", Terminals.NMTOKEN_TYPE),
	NMTOKENS_TYPE("keyword.operator.dtd", Terminals.NMTOKENS_TYPE),
	NMTOKEN("keyword.operator.dtd", Terminals.NMTOKEN);

	// ENTITY_REF("source.dtd"),

	// ATT_VALUE("source.dtd"),
	// ENTITY_VALUE("source.dtd"),
	// PUBID_LITERAL("source.dtd"),

	private short _index;
	private String _scope;

	/**
	 * DTDTokenType
	 * 
	 * @param scope
	 */
	private DTDTokenType(String scope, short beaverId)
	{
		this._scope = scope;
		this._index = beaverId;
	}

	private DTDTokenType(String scope, int index)
	{
		this(scope, (short) index);
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
