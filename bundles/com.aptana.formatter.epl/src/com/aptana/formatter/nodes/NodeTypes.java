/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.nodes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that holds definitions for arbitrary node types, such as punctuation and operators types.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class NodeTypes
{
	/**
	 * Supported node types for punctuation.<br>
	 * A punctuation type can only have a single char in its name.
	 */
	public enum TypePunctuation
	{
		CSS_CHILD_COMBINATOR(">"), //$NON-NLS-1$
		JS_DOT_PROPERTY("."), //$NON-NLS-1$
		SELECTOR_COLON(":"), //$NON-NLS-1$
		PROPERTY_COLON(":"), //$NON-NLS-1$
		CASE_COLON(":"), //$NON-NLS-1$
		GOTO_COLON(":"), //$NON-NLS-1$
		SEMICOLON(";"), //$NON-NLS-1$
		FOR_SEMICOLON(";"), //$NON-NLS-1$
		COMMA(","), //$NON-NLS-1$
		ARRAY_COMMA(","), //$NON-NLS-1$
		NAMESPACE_SEPARATOR("\\");//$NON-NLS-1$

		String name;

		TypePunctuation(String name)
		{
			if (name == null || name.length() != 1)
			{
				throw new IllegalArgumentException("Cannot create a TypePunctuation with the name: " + name); //$NON-NLS-1$
			}
			this.name = name;
		}

		public String toString()
		{
			return name;
		}
	};

	/**
	 * Supported node types for operators.
	 */
	public enum TypeOperator
	{
		ASSIGNMENT("="), //$NON-NLS-1$
		DOT_CONCATENATION("."), //$NON-NLS-1$
		PLUS_CONCATENATION("+"), //$NON-NLS-1$
		GREATER_THAN(">"), //$NON-NLS-1$
		LESS_THAN("<"), //$NON-NLS-1$
		GREATER_THAN_OR_EQUAL(">="), //$NON-NLS-1$
		LESS_THAN_OR_EQUAL("<="), //$NON-NLS-1$
		DOT_EQUAL(".="), //$NON-NLS-1$
		PLUS_EQUAL("+="), //$NON-NLS-1$
		MINUS_EQUAL("-="), //$NON-NLS-1$
		MULTIPLY_EQUAL("*="), //$NON-NLS-1$
		DIVIDE_EQUAL("/="), //$NON-NLS-1$
		TILDE_EQUAL("~="), //$NON-NLS-1$
		MODULUS_EQUAL("%="), //$NON-NLS-1$
		MULTIPLY("*"), //$NON-NLS-1$
		PLUS("+"), //$NON-NLS-1$
		MINUS("-"), //$NON-NLS-1$
		DIVIDE("/"), //$NON-NLS-1$
		MODULUS("%"), //$NON-NLS-1$
		POSTFIX_INCREMENT("++"), //$NON-NLS-1$
		PREFIX_INCREMENT("++"), //$NON-NLS-1$
		POSTFIX_DECREMENT("--"), //$NON-NLS-1$
		PREFIX_DECREMENT("--"), //$NON-NLS-1$
		OR("||"), //$NON-NLS-1$
		OR_LITERAL("or"), //$NON-NLS-1$
		AND("&&"), //$NON-NLS-1$
		AND_LITERAL("and"), //$NON-NLS-1$
		XOR("^"), //$NON-NLS-1$
		XOR_EQUAL("^="), //$NON-NLS-1$
		XOR_LITERAL("xor"), //$NON-NLS-1$
		BINARY_OR("|"), //$NON-NLS-1$
		BINARY_AND("&"), //$NON-NLS-1$
		OR_EQUAL("|="), //$NON-NLS-1$
		AND_EQUAL("&="), //$NON-NLS-1$
		EQUAL("=="), //$NON-NLS-1$
		SHIFT_RIGHT(">>"), //$NON-NLS-1$
		SHIFT_LEFT("<<"), //$NON-NLS-1$
		SHIFT_RIGHT_ASSIGN(">>="), //$NON-NLS-1$
		SHIFT_LEFT_ASSIGN("<<="), //$NON-NLS-1$
		SHIFT_RIGHT_ZERO_FILL(">>>"), //$NON-NLS-1$
		SHIFT_RIGHT_ZERO_FILL_ASSIGN(">>>="), //$NON-NLS-1$
		IDENTICAL("==="), //$NON-NLS-1$
		TILDE("~"), //$NON-NLS-1$
		NOT("!"), //$NON-NLS-1$
		NOT_EQUAL("!="), //$NON-NLS-1$
		NOT_EQUAL_ALTERNATE("<>"), //$NON-NLS-1$
		NOT_IDENTICAL("!=="), //$NON-NLS-1$
		ARROW("->"), //$NON-NLS-1$
		STATIC_INVOCATION("::"), //$NON-NLS-1$
		KEY_VALUE("=>"), //$NON-NLS-1$
		CONDITIONAL("?"), //$NON-NLS-1$
		CONDITIONAL_COLON(":"), //$NON-NLS-1$
		KEY_VALUE_COLON(":"), //$NON-NLS-1$
		TYPEOF("typeof"), //$NON-NLS-1$
		IN("in"), //$NON-NLS-1$
		DELETE("delete"), //$NON-NLS-1$
		VOID("void"), //$NON-NLS-1$
		INSTANCOF("instanceof"); //$NON-NLS-1$

		String name;

		private static Map<String, TypeOperator> OPERATORS_MAP;

		/**
		 * static initializer
		 */
		static
		{
			OPERATORS_MAP = new HashMap<String, TypeOperator>();
			for (TypeOperator type : EnumSet.allOf(TypeOperator.class))
			{
				OPERATORS_MAP.put(type.toString(), type);
			}
		}

		TypeOperator(String name)
		{
			this.name = name;
		}

		public String toString()
		{
			return name;
		}

		/**
		 * Returns a {@link TypeOperator} by a string.
		 * 
		 * @param operationString
		 * @return The matching {@link TypeOperator}; Null, if no match was found.
		 */
		public static TypeOperator getTypeOperator(String operationString)
		{
			return OPERATORS_MAP.get(operationString.toLowerCase());
		}
	};

	/**
	 * Supported node types for brackets.<br>
	 * Brackets are defined enums with left and right strings.
	 */
	public enum TypeBracket
	{
		CURLY("{", "}"), //$NON-NLS-1$ //$NON-NLS-2$
		SQUARE("[", "]"), //$NON-NLS-1$ //$NON-NLS-2$
		PARENTHESIS("(", ")"), //$NON-NLS-1$ //$NON-NLS-2$
		ARRAY_CURLY("{", "}"), //$NON-NLS-1$ //$NON-NLS-2$
		ARRAY_SQUARE("[", "]"), //$NON-NLS-1$ //$NON-NLS-2$
		ARRAY_PARENTHESIS("(", ")"), //$NON-NLS-1$ //$NON-NLS-2$
		LOOP_PARENTHESIS("(", ")"), //$NON-NLS-1$ //$NON-NLS-2$
		CONDITIONAL_PARENTHESIS("(", ")"), //$NON-NLS-1$ //$NON-NLS-2$
		DECLARATION_PARENTHESIS("(", ")"), //$NON-NLS-1$ //$NON-NLS-2$
		INVOCATION_PARENTHESIS("(", ")"); //$NON-NLS-1$ //$NON-NLS-2$

		String left;
		String right;

		TypeBracket(String left, String right)
		{
			if (left == null || left.length() != 1 || right == null || right.length() != 1)
			{
				throw new IllegalArgumentException("Cannot create a TypeBracket with " + left + ", " + right); //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.left = left;
			this.right = right;
		}

		public String getLeft()
		{
			return left;
		}

		public String getRight()
		{
			return right;
		}

		public String toString()
		{
			return left + ' ' + right;
		}
	};
}
