/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum JSScopeType implements ITypePredicate
{
	UNDEFINED("undefined.js"), //$NON-NLS-1$
	KEYWORD("keyword.operator.js"), //$NON-NLS-1$
	SUPPORT_FUNCTION("support.function.js"), //$NON-NLS-1$
	EVENT_HANDLER_FUNCTION("support.function.event-handler.js"), //$NON-NLS-1$
	DOM_FUNCTION("support.function.dom.js"), //$NON-NLS-1$
	FIREBUG_FUNCTION("support.function.js.firebug"), //$NON-NLS-1$
	OPERATOR("keyword.operator.js"), //$NON-NLS-1$
	SUPPORT_CONSTANT("support.constant.js"), //$NON-NLS-1$
	DOM_CONSTANTS("support.constant.dom.js"), //$NON-NLS-1$
	SOURCE("source.js"), //$NON-NLS-1$
	CONTROL_KEYWORD("keyword.control.js"), //$NON-NLS-1$
	STORAGE_TYPE("storage.type.js"), //$NON-NLS-1$
	STORAGE_MODIFIER("storage.modifier.js"), //$NON-NLS-1$
	SUPPORT_CLASS("support.class.js"), //$NON-NLS-1$
	SUPPORT_DOM_CONSTANT("support.constant.dom.js"), //$NON-NLS-1$
	TRUE("constant.language.boolean.true.js"), //$NON-NLS-1$
	FALSE("constant.language.boolean.false.js"), //$NON-NLS-1$
	NULL("constant.language.null.js"), //$NON-NLS-1$
	CONSTANT("constant.language.js"), //$NON-NLS-1$
	VARIABLE("variable.language.js"), //$NON-NLS-1$
	OTHER_KEYWORD("keyword.other.js"), //$NON-NLS-1$
	SEMICOLON("punctuation.terminator.statement.js"), //$NON-NLS-1$
	PARENTHESIS("meta.brace.round.js"), //$NON-NLS-1$
	BRACKET("meta.brace.square.js"), //$NON-NLS-1$
	CURLY_BRACE("meta.brace.curly.js"), //$NON-NLS-1$
	COMMA("meta.delimiter.object.comma.js"), //$NON-NLS-1$
	NUMBER("constant.numeric.js"), //$NON-NLS-1$
	// Left Paren in function declaration
	LEFT_PAREN("punctuation.definition.parameters.begin.js"), //$NON-NLS-1$
	// Right Paren in function declaration
	RIGHT_PAREN("punctuation.definition.parameters.end.js"), //$NON-NLS-1$
	// periods outside numbers (should be only for method calls, probably)
	PERIOD("meta.delimiter.method.period.js"), //$NON-NLS-1$
	// 'function' in function declaration
	FUNCTION_KEYWORD("storage.type.function.js"), //$NON-NLS-1$
	// The name of a function in the function decl
	FUNCTION_NAME("entity.name.function.js"), //$NON-NLS-1$
	// Parameter/argument in function decl
	FUNCTION_PARAMETER("variable.parameter.function.js"), //$NON-NLS-1$
	DOT("operator.dot.js"); //$NON-NLS-1$

	private static final Map<String, JSScopeType> NAME_MAP;
	private String _scope;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, JSScopeType>();

		for (JSScopeType type : EnumSet.allOf(JSScopeType.class))
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
	public static final JSScopeType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * JSScopeType
	 * 
	 * @param scope
	 */
	private JSScopeType(String scope)
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
