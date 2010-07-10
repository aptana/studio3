/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     istvan@benedek-home.de - 103706 [formatter] indent empty lines
 *     Aaron Luchko, aluchko@redhat.com - 105926 [Formatter] Exporting Unnamed profile fails silently
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
public final class FormatterMessages extends NLS
{

	private static final String BUNDLE_NAME = FormatterMessages.class.getName();

	private FormatterMessages()
	{
		// Do not instantiate
	}

	/**
	 * 
	 */
	public static String CommentsTabPage_remove_blank_block_comment_lines;
	/**
	 * 
	 */
	public static String FormatterTabPage_ShowInvisibleCharacters_label;
	/**
	 * 
	 */
	public static String FormattingPreferencePage_BLANK_LINES_WHITESPACES_TITLE;
	public static String IndentationTabPage_MultilineAttrsTitle;
	public static String IndentationTabPage_MultilineTitle;
	/**
	 * 
	 */
	public static String ModifyDialog_BuiltIn_Status;
	/**
	 * 
	 */
	public static String ModifyDialog_Duplicate_Status;
	/**
	 * 
	 */
	public static String ModifyDialog_EmptyName_Status;
	/**
	 * 
	 */
	public static String ModifyDialog_Export_Button;
	/**
	 * 
	 */
	public static String ModifyDialog_NewCreated_Status;
	/**
	 * 
	 */
	public static String ModifyDialog_ProfileName_Label;
	/**
	 * 
	 */
	public static String ModifyDialog_Shared_Status;
	/**
	 * 
	 */
	public static String ProfileConfigurationBlock_0;
	/**
	 * 
	 */
	public static String ProfileConfigurationBlock_load_profile_wrong_profile_message;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_assignments;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_assignments_before_assignment_operator;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_assignments_after_assignment_operator;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_before_binary_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_after_binary_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_before_unary_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_after_unary_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_before_prefix_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_after_prefix_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_before_postfix_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_after_postfix_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_before_concat_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_after_concat_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_before_object_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_operators_after_object_operators;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_classes;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_classes_before_opening_brace_of_a_class;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_classes_before_opening_brace_of_anon_class;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_classes_before_comma_implements;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_classes_after_comma_implements;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_methods;
	public static String WhiteSpaceTabPage_constants;
	public static String WhiteSpaceTabPage_constants_after_comma;
	public static String WhiteSpaceTabPage_constants_before_comma;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_constructors;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_fields;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_fields_before_comma;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_fields_after_comma;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_localvars;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_localvars_before_comma;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_localvars_after_comma;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_arrayinit;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_arraydecls;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_arrayelem;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_arrayalloc;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_calls;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_calls_before_comma_in_method_args;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_calls_after_comma_in_method_args;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_calls_before_comma_in_alloc;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_calls_after_comma_in_alloc;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_calls_before_comma_in_qalloc;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_calls_after_comma_in_qalloc;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_statements;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_blocks;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_switch;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_switch_before_case_colon;
	/**
	 * 
	 */
	public static String WhiteSpaceTabPage_switch_before_default_colon;
	/**
	 * 
	 */
	/**  */
	public static String WhiteSpaceTabPage_do;
	/**  */
	public static String WhiteSpaceTabPage_synchronized;
	/**  */
	public static String WhiteSpaceTabPage_try;
	/**  */
	public static String WhiteSpaceTabPage_if;
	/**  */
	public static String WhiteSpaceTabPage_assert;
	/**  */
	public static String WhiteSpaceTabPage_for;
	/**  */
	public static String WhiteSpaceTabPage_for_before_comma_init;
	/**  */
	public static String WhiteSpaceTabPage_for_after_comma_init;
	/**  */
	public static String WhiteSpaceTabPage_for_before_comma_inc;
	/**  */
	public static String WhiteSpaceTabPage_for_after_comma_inc;
	/**  */
	public static String WhiteSpaceTabPage_labels;
	/**  */
	public static String WhiteSpaceTabPage_annotations;
	/**  */
	public static String WhiteSpaceTabPage_annotation_types;
	/**  */
	public static String WhiteSpaceTabPage_enums;
	/**  */
	public static String WhiteSpaceTabPage_wildcardtype;
	/**  */
	public static String WhiteSpaceTabPage_param_type_ref;
	/**  */
	public static String WhiteSpaceTabPage_type_arguments;
	/**  */
	public static String WhiteSpaceTabPage_type_parameters;
	/**  */
	public static String WhiteSpaceTabPage_conditionals;
	/**  */
	public static String WhiteSpaceTabPage_typecasts;
	/**  */
	public static String WhiteSpaceTabPage_parenexpr;
	/**  */
	public static String WhiteSpaceTabPage_declarations;
	/**  */
	public static String WhiteSpaceTabPage_expressions;
	/**  */
	public static String WhiteSpaceTabPage_arrays;
	/**  */
	public static String WhiteSpaceTabPage_parameterized_types;
	/**  */
	public static String WhiteSpaceTabPage_after_opening_brace;
	/**  */
	public static String WhiteSpaceTabPage_after_closing_brace;
	/**  */
	public static String WhiteSpaceTabPage_before_opening_brace;
	/**  */
	public static String WhiteSpaceTabPage_before_closing_brace;
	/**  */
	public static String WhiteSpaceTabPage_between_empty_braces;
	/**  */
	public static String WhiteSpaceTabPage_after_opening_paren;
	/**  */
	public static String WhiteSpaceTabPage_after_closing_paren;
	/**  */
	public static String WhiteSpaceTabPage_before_opening_paren;
	/**  */
	public static String WhiteSpaceTabPage_before_closing_paren;
	/**  */
	public static String WhiteSpaceTabPage_between_empty_parens;
	/**  */
	public static String WhiteSpaceTabPage_after_opening_bracket;
	/**  */
	public static String WhiteSpaceTabPage_before_opening_bracket;
	/**  */
	public static String WhiteSpaceTabPage_before_closing_bracket;
	/**  */
	public static String WhiteSpaceTabPage_between_empty_brackets;
	/**  */
	public static String WhiteSpaceTabPage_before_comma_in_params;
	/**  */
	public static String WhiteSpaceTabPage_after_comma_in_params;
	/**  */
	public static String WhiteSpaceTabPage_before_comma_in_throws;
	/**  */
	public static String WhiteSpaceTabPage_after_comma_in_throws;
	/**  */
	public static String WhiteSpaceTabPage_before_ellipsis;
	/**  */
	public static String WhiteSpaceTabPage_after_ellipsis;
	/**  */
	public static String WhiteSpaceTabPage_before_comma;
	/**  */
	public static String WhiteSpaceTabPage_after_comma;
	/**  */
	public static String WhiteSpaceTabPage_after_semicolon;
	/**  */
	public static String WhiteSpaceTabPage_before_semicolon;
	/**  */
	public static String WhiteSpaceTabPage_before_colon;
	/**  */
	public static String WhiteSpaceTabPage_after_colon;
	/**  */
	public static String WhiteSpaceTabPage_before_question;
	/**  */
	public static String WhiteSpaceTabPage_after_question;
	/**  */
	public static String WhiteSpaceTabPage_before_at;
	/**  */
	public static String WhiteSpaceTabPage_after_at;
	/**  */
	public static String WhiteSpaceTabPage_after_opening_angle_bracket;
	/**  */
	public static String WhiteSpaceTabPage_after_closing_angle_bracket;
	/**  */
	public static String WhiteSpaceTabPage_before_opening_angle_bracket;
	/**  */
	public static String WhiteSpaceTabPage_before_closing_angle_bracket;
	/**  */
	public static String WhiteSpaceTabPage_before_and_list;
	/**  */
	public static String WhiteSpaceTabPage_after_and_list;
	/**  */
	public static String WhiteSpaceTabPage_enum_decl_before_opening_brace;
	/**  */
	public static String WhiteSpaceTabPage_enum_decl_before_comma;
	/**  */
	public static String WhiteSpaceTabPage_enum_decl_after_comma;
	/**  */
	public static String WhiteSpaceTabPage_enum_const_arg_before_opening_paren;
	/**  */
	public static String WhiteSpaceTabPage_enum_const_arg_after_opening_paren;
	/**  */
	public static String WhiteSpaceTabPage_enum_const_arg_between_empty_parens;
	/**  */
	public static String WhiteSpaceTabPage_enum_const_arg_before_comma;
	/**  */
	public static String WhiteSpaceTabPage_enum_const_arg_after_comma;
	/**  */
	public static String WhiteSpaceTabPage_enum_const_arg_before_closing_paren;
	/**  */
	public static String WhiteSpaceTabPage_enum_const_before_opening_brace;
	/**  */
	public static String WhiteSpaceTabPage_annot_type_method_before_opening_paren;
	/**  */
	public static String WhiteSpaceTabPage_annot_type_method_between_empty_parens;
	/**  */
	public static String WhiteSpaceTabPage_before_parenthesized_expressions;
	/**  */
	public static String WhiteSpaceTabPage_insert_space;
	/**  */
	public static String WhiteSpaceOptions_return;
	/**  */
	public static String WhiteSpaceOptions_throw;
	/**  */
	public static String WhiteSpaceOptions_before;
	/**  */
	public static String WhiteSpaceOptions_after;
	/**  */
	public static String WhiteSpaceOptions_operator;
	/**  */
	public static String WhiteSpaceOptions_assignment_operator;
	/**  */
	public static String WhiteSpaceOptions_binary_operator;
	/**  */
	public static String WhiteSpaceOptions_unary_operator;
	/**  */
	public static String WhiteSpaceOptions_prefix_operator;
	/**  */
	public static String WhiteSpaceOptions_postfix_operator;
	/**  */
	public static String WhiteSpaceOptions_concat_operator;
	/**  */
	public static String WhiteSpaceOptions_object_operator;
	/**  */
	public static String WhiteSpaceOptions_opening_paren;
	/**  */
	public static String WhiteSpaceOptions_catch;
	/**  */
	public static String WhiteSpaceOptions_for;
	/**  */
	public static String WhiteSpaceOptions_if;
	/**  */
	public static String WhiteSpaceOptions_switch;
	/**  */
	public static String WhiteSpaceOptions_synchronized;
	/**  */
	public static String WhiteSpaceOptions_while;
	/**  */
	public static String WhiteSpaceOptions_assert;
	/**  */
	public static String WhiteSpaceOptions_member_function_declaration;
	/**  */
	public static String WhiteSpaceOptions_constructor;
	/**  */
	public static String WhiteSpaceOptions_method;
	/**  */
	public static String WhiteSpaceOptions_method_call;
	/**  */
	public static String WhiteSpaceOptions_paren_expr;
	/**  */
	public static String WhiteSpaceOptions_enum_constant_body;
	/**  */
	public static String WhiteSpaceOptions_enum_constant_arguments;
	/**  */
	public static String WhiteSpaceOptions_enum_declaration;
	/**  */
	public static String WhiteSpaceOptions_annotation_modifier;
	/**  */
	public static String WhiteSpaceOptions_annotation_modifier_args;
	/**  */
	public static String WhiteSpaceOptions_annotation_type_member;
	/**  */
	public static String WhiteSpaceOptions_annotation_type;
	/**  */
	public static String WhiteSpaceOptions_type_cast;
	/**  */
	public static String WhiteSpaceOptions_parameterized_type;
	/**  */
	public static String WhiteSpaceOptions_type_arguments;
	/**  */
	public static String WhiteSpaceOptions_type_parameters;
	/**  */
	public static String WhiteSpaceOptions_vararg_parameter;
	/**  */
	public static String WhiteSpaceOptions_closing_paren;
	/**  */
	public static String WhiteSpaceOptions_opening_brace;
	/**  */
	public static String WhiteSpaceOptions_closing_brace;
	/**  */
	public static String WhiteSpaceOptions_opening_bracket;
	/**  */
	public static String WhiteSpaceOptions_closing_bracket;
	/**  */
	public static String WhiteSpaceOptions_class_decl;
	/**  */
	public static String WhiteSpaceOptions_anon_class_decl;
	/**  */
	public static String WhiteSpaceOptions_initializer;
	/**  */
	public static String WhiteSpaceOptions_block;
	/**  */
	public static String WhiteSpaceOptions_array_decl;
	/**  */
	public static String WhiteSpaceOptions_array_element_access;
	/**  */
	public static String WhiteSpaceOptions_array_alloc;
	/**  */
	public static String WhiteSpaceOptions_array_init;
	/**  */
	public static String WhiteSpaceOptions_arguments;
	/**  */
	public static String WhiteSpaceOptions_initialization;
	/**  */
	public static String WhiteSpaceOptions_incrementation;
	/**  */
	public static String WhiteSpaceOptions_parameters;
	/**  */
	public static String WhiteSpaceOptions_explicit_constructor_call;
	/**  */
	public static String WhiteSpaceOptions_alloc_expr;
	/**  */
	public static String WhiteSpaceOptions_throws;
	/**  */
	public static String WhiteSpaceOptions_mult_decls;
	/**  */
	public static String WhiteSpaceOptions_local_vars;
	/**  */
	public static String WhiteSpaceOptions_fields;
	/**  */
	public static String WhiteSpaceOptions_implements_clause;
	/**  */
	public static String WhiteSpaceOptions_colon;
	/**  */
	public static String WhiteSpaceOptions_conditional;
	/**  */
	public static String WhiteSpaceOptions_wildcard;
	/**  */
	public static String WhiteSpaceOptions_label;
	/**  */
	public static String WhiteSpaceOptions_comma;
	/**  */
	public static String WhiteSpaceOptions_semicolon;
	/**  */
	public static String WhiteSpaceOptions_question_mark;
	/**  */
	public static String WhiteSpaceOptions_between_empty_parens;
	/**  */
	public static String WhiteSpaceOptions_between_empty_braces;
	/**  */
	public static String WhiteSpaceOptions_between_empty_brackets;
	/**  */
	public static String WhiteSpaceOptions_constructor_decl;
	/**  */
	public static String WhiteSpaceOptions_method_decl;
	/**  */
	public static String WhiteSpaceOptions_case;
	/**  */
	public static String WhiteSpaceOptions_default;
	/**  */
	public static String WhiteSpaceOptions_statements;
	/**  */
	public static String WhiteSpaceOptions_before_opening_paren;
	/**  */
	public static String WhiteSpaceOptions_after_opening_paren;
	/**  */
	public static String WhiteSpaceOptions_before_closing_paren;
	/**  */
	public static String WhiteSpaceOptions_after_closing_paren;
	/**  */
	public static String WhiteSpaceOptions_before_opening_brace;
	/**  */
	public static String WhiteSpaceOptions_after_opening_brace;
	/**  */
	public static String WhiteSpaceOptions_after_closing_brace;
	/**  */
	public static String WhiteSpaceOptions_before_closing_brace;
	/**  */
	public static String WhiteSpaceOptions_before_opening_bracket;
	/**  */
	public static String WhiteSpaceOptions_after_opening_bracket;
	/**  */
	public static String WhiteSpaceOptions_before_closing_bracket;
	/**  */
	public static String WhiteSpaceOptions_before_opening_angle_bracket;
	/**  */
	public static String WhiteSpaceOptions_after_opening_angle_bracket;
	/**  */
	public static String WhiteSpaceOptions_before_closing_angle_bracket;
	/**  */
	public static String WhiteSpaceOptions_after_closing_angle_bracket;
	/**  */
	public static String WhiteSpaceOptions_before_operator;
	/**  */
	public static String WhiteSpaceOptions_after_operator;
	/**  */
	public static String WhiteSpaceOptions_before_comma;
	/**  */
	public static String WhiteSpaceOptions_after_comma;
	/**  */
	public static String WhiteSpaceOptions_after_colon;
	/**  */
	public static String WhiteSpaceOptions_before_colon;
	/**  */
	public static String WhiteSpaceOptions_before_semicolon;
	/**  */
	public static String WhiteSpaceOptions_after_semicolon;
	/**  */
	public static String WhiteSpaceOptions_before_question_mark;
	/**  */
	public static String WhiteSpaceOptions_after_question_mark;
	/**  */
	public static String WhiteSpaceOptions_before_at;
	/**  */
	public static String WhiteSpaceOptions_after_at;
	/**  */
	public static String WhiteSpaceOptions_before_and;
	/**  */
	public static String WhiteSpaceOptions_after_and;
	/**  */
	public static String WhiteSpaceOptions_before_ellipsis;
	/**  */
	public static String WhiteSpaceOptions_after_ellipsis;
	/**  */
	public static String WhiteSpaceOptions_return_with_parenthesized_expression;
	/**  */
	public static String WhiteSpaceOptions_throw_with_parenthesized_expression;
	public static String WhiteSpaceOptions_multiple_const;
	/**  */
	public static String LineWrappingTabPage_compact_if_else;
	/**  */
	public static String LineWrappingTabPage_extends_clause;
	/**  */
	public static String LineWrappingTabPage_enum_constant_arguments;
	/**  */
	public static String LineWrappingTabPage_enum_constants;
	/**  */
	public static String LineWrappingTabPage_implements_clause;
	/**  */
	public static String LineWrappingTabPage_parameters;
	/**  */
	public static String LineWrappingTabPage_arguments;
	/**  */
	public static String LineWrappingTabPage_method_chaining;
	/**  */
	public static String LineWrappingTabPage_qualified_invocations;
	/**  */
	public static String LineWrappingTabPage_throws_clause;
	/**  */
	public static String LineWrappingTabPage_object_allocation;
	/**  */
	public static String LineWrappingTabPage_qualified_object_allocation;
	/**  */
	public static String LineWrappingTabPage_array_init;
	/**  */
	public static String LineWrappingTabPage_explicit_constructor_invocations;
	/**  */
	public static String LineWrappingTabPage_conditionals;
	/**  */
	public static String LineWrappingTabPage_binary_exprs;
	/**  */
	public static String LineWrappingTabPage_indentation_default;
	/**  */
	public static String LineWrappingTabPage_indentation_on_column;
	/**  */
	public static String LineWrappingTabPage_indentation_by_one;
	/**  */
	public static String LineWrappingTabPage_class_decls;
	/**  */
	public static String LineWrappingTabPage_method_decls;
	/**  */
	public static String LineWrappingTabPage_constructor_decls;
	/**  */
	public static String LineWrappingTabPage_function_calls;
	/**  */
	public static String LineWrappingTabPage_expressions;
	/**  */
	public static String LineWrappingTabPage_statements;
	/**  */
	public static String LineWrappingTabPage_enum_decls;
	/**  */
	public static String LineWrappingTabPage_wrapping_policy_label_text;
	/**  */
	public static String LineWrappingTabPage_indentation_policy_label_text;
	/**  */
	public static String LineWrappingTabPage_force_split_checkbox_text;
	/**  */
	public static String LineWrappingTabPage_force_split_checkbox_multi_text;
	/**  */
	public static String LineWrappingTabPage_line_width_for_preview_label_text;
	/**  */
	public static String LineWrappingTabPage_group;
	/**  */
	public static String LineWrappingTabPage_multi_group;
	/**  */
	public static String LineWrappingTabPage_multiple_selections;
	/**  */
	public static String LineWrappingTabPage_occurences;
	/**  */
	public static String LineWrappingTabPage_splitting_do_not_split;
	/**  */
	public static String LineWrappingTabPage_splitting_wrap_when_necessary;
	/**  */
	public static String LineWrappingTabPage_splitting_always_wrap_first_others_when_necessary;
	/**  */
	public static String LineWrappingTabPage_splitting_wrap_always;
	/**  */
	public static String LineWrappingTabPage_splitting_wrap_always_indent_all_but_first;
	/**  */
	public static String LineWrappingTabPage_splitting_wrap_always_except_first_only_if_necessary;
	/**  */
	public static String LineWrappingTabPage_width_indent;
	/**  */
	public static String LineWrappingTabPage_width_indent_option_max_line_width;
	/**  */
	public static String LineWrappingTabPage_width_indent_option_default_indent_wrapped;
	/**  */
	public static String LineWrappingTabPage_width_indent_option_default_indent_array;
	/**  */
	public static String LineWrappingTabPage_error_invalid_value;
	/**  */
	public static String LineWrappingTabPage_enum_superinterfaces;
	/**  */
	public static String LineWrappingTabPage_assignment_alignment;
	/**  */
	public static String LineWrappingTabPage_binary_expression_wrap_operator;
	/**  */
	public static String LineWrappingTabPage_binary_expression;

	/**  */
	public static String BlankLinesTabPage_preview_header;
	/**  */
	public static String BlankLinesTabPage_compilation_unit_group_title;
	/**  */
	public static String BlankLinesTabPage_compilation_unit_option_before_package;
	/**  */
	public static String BlankLinesTabPage_compilation_unit_option_after_package;
	/**  */
	public static String BlankLinesTabPage_compilation_unit_option_before_import;
	/**  */
	public static String BlankLinesTabPage_compilation_unit_option_after_import;
	/**  */
	public static String BlankLinesTabPage_compilation_unit_option_between_import_groups;
	/**  */
	public static String BlankLinesTabPage_compilation_unit_option_between_type_declarations;
	/**  */
	public static String BlankLinesTabPage_class_group_title;
	/**  */
	public static String BlankLinesTabPage_class_option_before_first_decl;
	/**  */
	public static String BlankLinesTabPage_class_option_before_decls_of_same_kind;
	/**  */
	public static String BlankLinesTabPage_class_option_before_member_class_decls;
	/**  */
	public static String BlankLinesTabPage_class_option_before_field_decls;
	/**  */
	public static String BlankLinesTabPage_class_option_before_method_decls;
	/**  */
	public static String BlankLinesTabPage_class_option_at_beginning_of_method_body;
	/**  */
	public static String BlankLinesTabPage_blank_lines_group_title;
	/**  */
	public static String BlankLinesTabPage_blank_lines_option_empty_lines_to_preserve;
	/**  */
	public static String BracesTabPage_preview_header;
	/**  */
	public static String BracesTabPage_position_same_line;
	/**  */
	public static String BracesTabPage_position_next_line;
	/**  */
	public static String BracesTabPage_position_next_line_indented;
	/**  */
	public static String BracesTabPage_position_next_line_on_wrap;
	/**  */
	public static String BracesTabPage_group_brace_positions_title;
	/**  */
	public static String BracesTabPage_option_class_declaration;
	/**  */
	public static String BracesTabPage_option_anonymous_class_declaration;
	/**  */
	public static String BracesTabPage_option_method_declaration;
	/**  */
	public static String BracesTabPage_option_constructor_declaration;
	/**  */
	public static String BracesTabPage_option_blocks;
	/**  */
	public static String BracesTabPage_option_blocks_in_case;
	/**  */
	public static String BracesTabPage_option_switch_case;
	/**  */
	public static String BracesTabPage_option_array_initializer;
	/**  */
	public static String BracesTabPage_option_keep_empty_array_initializer_on_one_line;
	/**  */
	public static String BracesTabPage_option_enum_declaration;
	/**  */
	public static String BracesTabPage_option_enumconst_declaration;
	/**  */
	public static String BracesTabPage_option_annotation_type_declaration;

	/**  */
	public static String CodingStyleConfigurationBlock_preview_title;

	/**  */
	public static String CommentsTabPage_group1_title;
	/**  */
	public static String commentsTabPage_enable_javadoc_comment_formatting;
	/**  */
	public static String CommentsTabPage_enable_line_comment_formatting;
	/**  */
	public static String CommentsTabPage_enable_block_comment_formatting;
	/**  */
	public static String CommentsTabPage_format_header;
	/**  */
	public static String CommentsTabPage_format_html;
	/**  */
	public static String CommentsTabPage_format_code_snippets;
	/**  */
	public static String CommentsTabPage_group2_title;
	/**  */
	public static String CommentsTabPage_clear_blank_lines;
	/**  */
	public static String CommentsTabPage_blank_line_before_javadoc_tags;
	/**  */
	public static String CommentsTabPage_indent_javadoc_tags;
	/**  */
	public static String CommentsTabPage_indent_description_after_param;
	/**  */
	public static String CommentsTabPage_new_line_after_param_tags;
	/**  */
	public static String CommentsTabPage_group3_title;
	/**  */
	public static String CommentsTabPage_group4_title;
	/**  */
	public static String CommentsTabPage_group5_title;
	/**  */
	public static String CommentsTabPage_line_width;
	/**  */
	public static String CommentsTabPage_never_indent_block_comments_on_first_column;
	/**  */
	public static String CommentsTabPage_never_indent_line_comments_on_first_column;
	/**  */
	public static String ControlStatementsTabPage_preview_header;
	/**  */
	public static String ControlStatementsTabPage_general_group_title;
	/**  */
	public static String ControlStatementsTabPage_general_group_insert_new_line_before_else_statements;
	/**  */
	public static String ControlStatementsTabPage_general_group_insert_new_line_before_catch_statements;
	/**  */
	public static String ControlStatementsTabPage_general_group_insert_new_line_before_finally_statements;
	/**  */
	public static String ControlStatementsTabPage_general_group_insert_new_line_before_while_in_do_statements;
	/**  */
	public static String ControlStatementsTabPage_if_else_group_title;
	/**  */
	public static String ControlStatementsTabPage_if_else_group_keep_then_on_same_line;
	/**  */
	public static String ControlStatementsTabPage_if_else_group_keep_simple_if_on_one_line;
	/**  */
	public static String ControlStatementsTabPage_if_else_group_keep_else_on_same_line;
	/**  */
	public static String ControlStatementsTabPage_if_else_group_keep_else_if_on_one_line;
	/**  */
	public static String ControlStatementsTabPage_if_else_group_keep_guardian_clause_on_one_line;

	/**  */
	public static String IndentationTabPage_preview_header;
	/**  */
	public static String IndentationTabPage_general_group_title;
	/**  */
	public static String IndentationTabPage_general_group_option_tab_policy;
	/**  */
	public static String IndentationTabPage_general_group_option_tab_policy_SPACE;
	/**  */
	public static String IndentationTabPage_general_group_option_tab_policy_TAB;
	/**  */
	public static String IndentationTabPage_general_group_option_tab_policy_MIXED;
	/**  */
	public static String IndentationTabPage_general_group_option_tab_size;
	/**  */
	public static String IndentationTabPage_general_group_option_indent_size;
	/**  */
	public static String IndentationTabPage_field_alignment_group_title;
	/**  */
	public static String IndentationTabPage_field_alignment_group_align_fields_in_columns;
	/**  */
	public static String IndentationTabPage_indent_group_title;
	/**  */
	public static String IndentationTabPage_class_group_option_indent_declarations_within_class_body;
	/**  */
	public static String IndentationTabPage_class_group_option_indent_declarations_within_enum_const;
	/**  */
	public static String IndentationTabPage_class_group_option_indent_declarations_within_enum_decl;
	/**  */
	public static String IndentationTabPage_class_group_option_indent_declarations_within_annot_decl;
	/**  */
	public static String IndentationTabPage_block_group_option_indent_statements_compare_to_body;
	/**  */
	public static String IndentationTabPage_block_group_option_indent_statements_compare_to_block;
	/**  */
	public static String IndentationTabPage_switch_group_option_indent_statements_within_switch_body;
	/**  */
	public static String IndentationTabPage_switch_group_option_indent_statements_within_case_body;
	/**  */
	public static String IndentationTabPage_switch_group_option_indent_break_statements;
	/**  */
	public static String IndentationTabPage_indent_empty_lines;
	/**  */
	public static String IndentationTabPage_inline_php;
	/**  */
	public static String IndentationTabPage_use_tabs_only_for_leading_indentations;

	/**  */
	public static String ModifyDialog_tabpage_braces_title;
	/**  */
	public static String ModifyDialog_tabpage_indentation_title;
	/**  */
	public static String ModifyDialog_tabpage_whitespace_title;
	/**  */
	public static String ModifyDialog_tabpage_blank_lines_title;
	/**  */
	public static String ModifyDialog_tabpage_new_lines_title;
	/**  */
	public static String ModifyDialog_tabpage_control_statements_title;
	/**  */
	public static String ModifyDialog_tabpage_line_wrapping_title;
	/**  */
	public static String ModifyDialog_tabpage_comments_title;

	/**  */
	public static String NewLinesTabPage_preview_header;
	/**  */
	public static String NewLinesTabPage_newlines_group_title;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_class_body;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_annotation_decl_body;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_anonymous_class_body;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_enum_declaration;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_enum_constant;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_method_body;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_block;
	/**  */
	public static String NewLinesTabPage_newlines_group_option_empty_end_of_file;
	/**  */
	public static String NewLinesTabPage_empty_statement_group_title;
	/**  */
	public static String NewLinesTabPage_emtpy_statement_group_option_empty_statement_on_new_line;
	/**  */
	public static String NewLinesTabPage_arrayInitializer_group_title;
	/**  */
	public static String NewLinesTabPage_array_group_option_after_opening_brace_of_array_initializer;
	/**  */
	public static String NewLinesTabPage_array_group_option_before_closing_brace_of_array_initializer;
	/**  */
	public static String NewLinesTabPage_annotations_group_title;
	/**  */
	public static String NewLinesTabPage_annotations_group_option_after_annotation;
	/**  */
	public static String ProfileManager_default_profile_name;
	/**  */
	public static String ProfileManager_eclipse_profile_name;
	/**  */
	public static String ProfileManager_aptana_conventions_profile_name;
	/**  */
	public static String ProfileManager_noformatting_profile_name;

	/**  */
	public static String JavaPreview_formatter_exception;
	/**  */
	public static String WhiteSpaceTabPage_sort_by_java_element;
	/**  */
	public static String WhiteSpaceTabPage_sort_by_syntax_element;

	/**  */
	public static String AlreadyExistsDialog_message_profile_already_exists;
	/**  */
	public static String AlreadyExistsDialog_message_profile_name_empty;
	/**  */
	public static String AlreadyExistsDialog_dialog_title;
	/**  */
	public static String AlreadyExistsDialog_dialog_label;
	/**  */
	public static String AlreadyExistsDialog_rename_radio_button_desc;
	/**  */
	public static String AlreadyExistsDialog_overwrite_radio_button_desc;

	/**  */
	public static String CodingStyleConfigurationBlock_save_profile_dialog_title;
	/**  */
	public static String CodingStyleConfigurationBlock_save_profile_error_title;
	/**  */
	public static String CodingStyleConfigurationBlock_save_profile_error_message;
	/**  */
	public static String CodingStyleConfigurationBlock_load_profile_dialog_title;
	/**  */
	public static String CodingStyleConfigurationBlock_load_profile_error_title;
	/**  */
	public static String CodingStyleConfigurationBlock_load_profile_error_message;
	/**  */
	public static String CodingStyleConfigurationBlock_load_profile_error_too_new_title;
	/**  */
	public static String CodingStyleConfigurationBlock_load_profile_error_too_new_message;
	/**  */
	public static String CodingStyleConfigurationBlock_save_profile_overwrite_title;
	/**  */
	public static String CodingStyleConfigurationBlock_save_profile_overwrite_message;
	/**  */
	public static String CodingStyleConfigurationBlock_edit_button_desc;
	/**  */
	public static String CodingStyleConfigurationBlock_remove_button_desc;
	/**  */
	public static String CodingStyleConfigurationBlock_new_button_desc;
	/**  */
	public static String CodingStyleConfigurationBlock_load_button_desc;
	/**  */
	public static String CodingStyleConfigurationBlock_save_button_desc;
	/**  */
	public static String CodingStyleConfigurationBlock_preview_label_text;
	/**  */
	public static String CodingStyleConfigurationBlock_error_reading_xml_message;
	/**  */
	public static String CodingStyleConfigurationBlock_error_serializing_xml_message;
	/**  */
	public static String CodingStyleConfigurationBlock_delete_confirmation_title;
	/**  */
	public static String CodingStyleConfigurationBlock_delete_confirmation_question;

	/**  */
	public static String CreateProfileDialog_status_message_profile_with_this_name_already_exists;
	/**  */
	public static String CreateProfileDialog_status_message_profile_name_is_empty;
	/**  */
	public static String CreateProfileDialog_dialog_title;
	/**  */
	public static String CreateProfileDialog_profile_name_label_text;
	/**  */
	public static String CreateProfileDialog_base_profile_label_text;
	/**  */
	public static String CreateProfileDialog_open_edit_dialog_checkbox_text;

	/**  */
	public static String ModifyDialog_dialog_title;
	/**  */
	public static String ModifyDialog_apply_button;
	/**  */
	public static String ModifyDialogTabPage_preview_label_text;

	/**  */
	public static String ProfileManager_unmanaged_profile;
	/**  */
	public static String ProfileManager_unmanaged_profile_with_name;

	/**  */
	public static String ModifyDialogTabPage_error_msg_values_text_unassigned;
	/**  */
	public static String ModifyDialogTabPage_error_msg_values_items_text_unassigned;
	/**  */
	public static String ModifyDialogTabPage_NumberPreference_error_invalid_key;
	/**  */
	public static String ModifyDialogTabPage_NumberPreference_error_invalid_value;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}
}
