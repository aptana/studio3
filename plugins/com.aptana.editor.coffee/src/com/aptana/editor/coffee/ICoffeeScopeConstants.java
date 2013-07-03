/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee;

@SuppressWarnings("nls")
public interface ICoffeeScopeConstants
{
	String TOPLEVEL = "source.coffee";

	// Keywords
	String CONTROL_KEYWORD = "keyword.control.coffee";
	String OPERATOR = "keyword.operator.coffee";
	String KEYWORD_NEW = "keyword.operator.new.coffee";
	String KEYWORD_EXTENDS = "keyword.control.inheritance.coffee";

	// Constants
	String NUMERIC = "constant.numeric.coffee";
	String TRUE = "constant.language.boolean.true.coffee";
	String FALSE = "constant.language.boolean.false.coffee";
	String NULL = "constant.language.null.coffee";
	String LANGUAGE_CONSTANT = "constant.language.coffee";

	// Comments
	String COMMENT_LINE = "comment.line.coffee";
	String COMMENT_BLOCK = "comment.block.coffee";

	// Strings
	String STRING_SINGLE = "string.quoted.single.coffee";
	String STRING_DOUBLE = "string.quoted.double.coffee";
	String STRING_HEREDOC_SINGLE = "string.quoted.heredoc.coffee";
	String STRING_HEREDOC_DOUBLE = "string.quoted.double.heredoc.coffee";
	String REGEXP = "string.regexp.coffee";
	// Embedded JS
	String COMMAND = "string.quoted.script.coffee";

	// Entities
	String FUNCTION_NAME = "entity.name.function.coffee";
	String ENTITY_TYPE_INSTANCE = "entity.name.type.instance.coffee";
	String SUPERCLASS = "entity.other.inherited-class.coffee";
	String CLASS_NAME = "entity.name.type.class.coffee";

	// Punctuation
	String SEMICOLON = "punctuation.terminator.statement.coffee";
	String COLON = "punctuation.separator.key-value.coffee";

	// Meta
	String COMMA = "meta.delimiter.object.comma.coffee";
	String PERIOD = "meta.delimiter.method.period.coffee";
	String BRACKET = "meta.brace.square.coffee";
	String PAREN = "meta.brace.round.coffee";
	String CURLY = "meta.brace.curly.coffee";
	String META_FUNCTION = "meta.function.coffee";
	String META_INLINE_FUNCTION = "meta.inline.function.coffee";
	String META_CLASS = "meta.class.coffee";

	// Storage
	String FUNCTION_STORAGE = "storage.type.function.coffee";
	String KEYWORD_CLASS = "storage.type.class.coffee";

	// Variables
	String INSTANCE_VARIABLE = "variable.other.readwrite.instance.coffee";
	String LANGUAGE_VARIABLE = "variable.language.coffee";
	String ASSIGNMENT_VARIABLE = "variable.assignment.coffee";
	String PARAMETER_VARIABLE = "variable.parameter.function.coffee";

}
