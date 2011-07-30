/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

@SuppressWarnings("nls")
public enum CoffeeScopeType implements ITypePredicate
{

	UNDEFINED(""), TRUE(ICoffeeScopeConstants.TRUE), FALSE(ICoffeeScopeConstants.FALSE), IDENTIFIER(""), NULL(
			ICoffeeScopeConstants.NULL), // null
	INSTANCE_VARIABLE(ICoffeeScopeConstants.INSTANCE_VARIABLE),
	COLON(ICoffeeScopeConstants.COLON), // :
	SEMICOLON(ICoffeeScopeConstants.SEMICOLON), // ;
	LEFT_PAREN(ICoffeeScopeConstants.PAREN), // (
	RIGHT_PAREN(ICoffeeScopeConstants.PAREN), // )
	LEFT_BRACKET(ICoffeeScopeConstants.BRACKET), // [
	RIGHT_BRACKET(ICoffeeScopeConstants.BRACKET), // ]
	LEFT_CURLY(ICoffeeScopeConstants.CURLY), // {
	RIGHT_CURLY(ICoffeeScopeConstants.CURLY), // }
	COMMA(ICoffeeScopeConstants.COMMA), // ,
	PERIOD(ICoffeeScopeConstants.PERIOD), // .
	NUMERIC(ICoffeeScopeConstants.NUMERIC),
	NEW(ICoffeeScopeConstants.KEYWORD_NEW), // new
	EXTENDS(ICoffeeScopeConstants.KEYWORD_EXTENDS), // extends
	CLASS(ICoffeeScopeConstants.KEYWORD_CLASS), // class
	BOUND_FUNC(ICoffeeScopeConstants.FUNCTION_STORAGE), // =>
	FUNC((ICoffeeScopeConstants.FUNCTION_STORAGE)), // ->
	CLASS_NAME(ICoffeeScopeConstants.CLASS_NAME),
	SUPERCLASS(ICoffeeScopeConstants.SUPERCLASS),
	ENTITY_TYPE_INSTANCE(ICoffeeScopeConstants.ENTITY_TYPE_INSTANCE),
	EQUAL(ICoffeeScopeConstants.OPERATOR), // =
	LANGUAGE_VARIABLE(ICoffeeScopeConstants.LANGUAGE_VARIABLE),
	LANGUAGE_CONSTANT(ICoffeeScopeConstants.LANGUAGE_CONSTANT),
	CONTROL_KEYWORD(ICoffeeScopeConstants.CONTROL_KEYWORD),
	INCLUSIVE_RANGE(ICoffeeScopeConstants.PERIOD), // ..
	EXCLUSIVE_RANGE(ICoffeeScopeConstants.PERIOD), // ...
	OPERATOR(ICoffeeScopeConstants.OPERATOR),
	FUNCTION_NAME(ICoffeeScopeConstants.META_FUNCTION + " " + ICoffeeScopeConstants.FUNCTION_NAME);

	private static final Map<String, CoffeeScopeType> NAME_MAP;
	private String _scope;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, CoffeeScopeType>();

		for (CoffeeScopeType type : EnumSet.allOf(CoffeeScopeType.class))
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
	public static final CoffeeScopeType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * CoffeeScopeType
	 * 
	 * @param scope
	 */
	private CoffeeScopeType(String scope)
	{
		this._scope = scope;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ITypePredicate#getIndex()
	 */
	public short getIndex()
	{
		return (short) this.ordinal();
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
