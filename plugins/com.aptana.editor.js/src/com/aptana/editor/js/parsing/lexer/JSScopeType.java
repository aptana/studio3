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
package com.aptana.editor.js.parsing.lexer;

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
