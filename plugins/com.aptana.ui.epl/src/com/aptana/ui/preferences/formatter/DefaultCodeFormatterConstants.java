/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.util.HashMap;
import java.util.Map;

import com.aptana.ui.epl.UIEplPlugin;

/**
 * Constants used to set up the options of the code formatter.
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
public class DefaultCodeFormatterConstants
{

	/**
	 * INITIAL_TAGS_DO_NOT_WRAP
	 */
	public static final String INITIAL_TAGS_DO_NOT_WRAP = "a,span,i,b,em,strong,h1,h2,h3,h4,h5,h6,title,option";//$NON-NLS-1$

	/**
	 * INITIAL_TAGS_TO_WRAP
	 */
	public static final String INITIAL_TAGS_TO_WRAP = "doctype,br,link,meta";//$NON-NLS-1$

	/**
	 * INITIAL_TAGS_TO_NOT_INDENT
	 */
	public static final String INITIAL_TAGS_TO_NOT_INDENT = "";//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the end of a line.
	 * </pre>
	 * 
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
	public static final String END_OF_LINE = "end_of_line"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Value to set an option to false.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FALSE = "false"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to align type members of a type declaration on column
	 *     - option id:         &quot;com.aptana.ui.epl.formatter.align_type_members_on_columns&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGN_TYPE_MEMBERS_ON_COLUMNS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.align_type_members_on_columns"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_arguments_in_allocation_expression&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_arguments_in_allocation_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_arguments_in_enum_constant&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.1
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_arguments_in_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in explicit constructor call
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_arguments_in_explicit_constructor_call&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_EXPLICIT_CONSTRUCTOR_CALL = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_arguments_in_explicit_constructor_call"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_arguments_in_method_invocation&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_METHOD_INVOCATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_arguments_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of method invocation chaining (e.g. a chained use or the arrow operator ->)
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_method_invocation_chaining&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since Aptana PHP 1.1
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_METHOD_INVOCATION_CHAINING = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_method_invocation_chaining"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in qualified allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_arguments_in_qualified_allocation_expression&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_QUALIFIED_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_arguments_in_qualified_allocation_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of assignment
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_assignment&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, M_NO_ALIGNMENT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.2
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ASSIGNMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_assignment"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of binary expression
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_binary_expression&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_BINARY_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_binary_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of compact if
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_compact_if&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_ONE_PER_LINE, INDENT_BY_ONE)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_COMPACT_IF = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_compact_if"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of conditional expression
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_conditional_expression&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_ONE_PER_LINE, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_CONDITIONAL_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_conditional_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of enum constants
	 *     - option id:        &quot;com.aptana.ui.epl.alignment_for_enum_constants&quot;
	 *     - possible values:  values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:          createAlignmentValue(false, WRAP_NO_SPLIT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.1
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_enum_constants"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of expressions in array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_expressions_in_array_initializer&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_EXPRESSIONS_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_expressions_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of multiple fields
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_multiple_fields&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_SPACES_BEFORE_ATTRS_ON_MULTILINE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_multiple_fields";//$NON-NLS-1$	
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of parameters in constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_parameters_in_constructor_declaration&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_parameters_in_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of parameters in method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_parameters_in_method_declaration&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_parameters_in_method_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of selector in method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_selector_in_method_invocation&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SELECTOR_IN_METHOD_INVOCATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_selector_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of superclass in type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_superclass_in_type_declaration&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_NEXT_SHIFTED, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SUPERCLASS_IN_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_superclass_in_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of superinterfaces in enum declaration
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_superinterfaces_in_enum_declaration&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.1
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SUPERINTERFACES_IN_ENUM_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_superinterfaces_in_enum_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of superinterfaces in type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_superinterfaces_in_type_declaration&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SUPERINTERFACES_IN_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_superinterfaces_in_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of throws clause in constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_throws_clause_in_constructor_declaration&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_throws_clause_in_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of throws clause in method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.alignment_for_throws_clause_in_method_declaration&quot;
	 *     - possible values:   values returned by
	 * <code>
	 * createAlignmentValue(boolean, int, int)
	 * </code>
	 *  call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * 
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.alignment_for_throws_clause_in_method_declaration"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines after the imports declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_after_imports&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_AFTER_IMPORTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_after_imports"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines after the package declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_after_package&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_AFTER_PACKAGE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_after_package"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines at the beginning of the method body
	 *     - option id:         &quot;com.aptana.ui.epl.number_of_blank_lines_at_beginning_of_method_body&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY = UIEplPlugin.PLUGIN_ID
			+ ".formatter.number_of_blank_lines_at_beginning_of_method_body"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a field declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_before_field&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_FIELD = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_before_field"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before the first class body declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_before_first_class_body_declaration&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_FIRST_CLASS_BODY_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_before_first_class_body_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before the imports declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_before_imports&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_IMPORTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_before_imports"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a member type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_before_member_type&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_MEMBER_TYPE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_before_member_type"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_before_method&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_METHOD = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_before_method"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a new chunk
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_before_new_chunk&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_NEW_CHUNK = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_before_new_chunk"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before the package declaration
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_before_package&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_PACKAGE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_before_package"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines between import groups
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_between_import_groups&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;1&quot;
	 * </pre>
	 * 
	 * Note: Import groups are defined once "Organize Import" operation has been executed. The code formatter itself
	 * doesn't define the import groups.
	 * 
	 * @since 3.3
	 */
	public static final String FORMATTER_BLANK_LINES_BETWEEN_IMPORT_GROUPS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_between_import_groups"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines between type declarations
	 *     - option id:         &quot;com.aptana.ui.epl.blank_lines_between_type_declarations&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BETWEEN_TYPE_DECLARATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.blank_lines_between_type_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an annotation type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_annotation_type_declaration&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.1
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ANNOTATION_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_annotation_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an anonymous type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_anonymous_type_declaration&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_anonymous_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_array_initializer&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a block
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_block&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_BLOCK = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_block"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a block in a case statement when the block is the first statement following
	 *             the case
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_block_in_case&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_block_in_case"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_constructor_declaration&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_enum_constant&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.1
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an enum declaration
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_enum_declaration&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.1
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_enum_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_method_declaration&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_method_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a switch statement
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_switch&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_SWITCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_switch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.brace_position_for_type_declaration&quot;
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * 
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.brace_position_for_type_declaration"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether blank lines are cleared inside comments
	 *     - option id:         &quot;com.aptana.ui.epl.comment.clear_blank_lines&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 * @deprecated Use {@link #FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_BLOCK_COMMENT} and
	 *             {@link #FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_JAVADOC_COMMENT}
	 */
	public final static String FORMATTER_COMMENT_CLEAR_BLANK_LINES = "com.aptana.ui.epl.comment.clear_blank_lines"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether blank lines are cleared inside javadoc comments
	 *     - option id:         &quot;com.aptana.ui.epl.comment.clear_blank_lines_in_javadoc_comment&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public final static String FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_JAVADOC_COMMENT = "com.aptana.ui.epl.comment.clear_blank_lines_in_javadoc_comment"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether blank lines are cleared inside block comments
	 *     - option id:         &quot;com.aptana.ui.epl.comment.clear_blank_lines_in_block_comment&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public final static String FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_BLOCK_COMMENT = "com.aptana.ui.epl.comment.clear_blank_lines_in_block_comment"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether comments are formatted
	 *     - option id:         &quot;com.aptana.ui.epl.comment.format_comments&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 * @deprecated Use multiple settings for each kind of comments. See {@link #FORMATTER_COMMENT_FORMAT_BLOCK_COMMENT},
	 *             {@link #FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT} and {@link #FORMATTER_COMMENT_FORMAT_LINE_COMMENT}.
	 */
	public final static String FORMATTER_COMMENT_FORMAT = "com.aptana.ui.epl.comment.format_comments"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether single line comments are formatted
	 *     - option id:         &quot;com.aptana.ui.epl.comment.format_line_comments&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public final static String FORMATTER_COMMENT_FORMAT_LINE_COMMENT = "com.aptana.ui.epl.comment.format_line_comments"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether multiple lines comments are formatted
	 *     - option id:         &quot;com.aptana.ui.epl.comment.format_block_comments&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public final static String FORMATTER_COMMENT_FORMAT_BLOCK_COMMENT = "com.aptana.ui.epl.comment.format_block_comments"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether javadoc comments are formatted
	 *     - option id:         &quot;com.aptana.ui.epl.comment.format_javadoc_comments&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public final static String FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT = "com.aptana.ui.epl.comment.format_javadoc_comments"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether the header comment of a Java source file is formatted
	 *     - option id:         &quot;com.aptana.ui.epl.comment.format_header&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_FORMAT_HEADER = "com.aptana.ui.epl.comment.format_header"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether HTML tags are formatted.
	 *     - option id:         &quot;com.aptana.ui.epl.comment.format_html&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_FORMAT_HTML = "com.aptana.ui.epl.comment.format_html"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether code snippets are formatted in comments
	 *     - option id:         &quot;com.aptana.ui.epl.comment.format_source_code&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_FORMAT_SOURCE = "com.aptana.ui.epl.comment.format_source_code"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether description of Javadoc parameters are indented
	 *     - option id:         &quot;com.aptana.ui.epl.comment.indent_parameter_description&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_INDENT_PARAMETER_DESCRIPTION = "com.aptana.ui.epl.comment.indent_parameter_description"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether Javadoc root tags are indented.
	 *     - option id:         &quot;com.aptana.ui.epl.comment.indent_root_tags&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_INDENT_ROOT_TAGS = "com.aptana.ui.epl.comment.indent_root_tags"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert an empty line before the Javadoc root tag block
	 *     - option id:         &quot;com.aptana.ui.epl.comment.insert_new_line_before_root_tags&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_INSERT_EMPTY_LINE_BEFORE_ROOT_TAGS = "com.aptana.ui.epl.comment.insert_new_line_before_root_tags"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line after Javadoc root tag parameters
	 *     - option id:         &quot;com.aptana.ui.epl.comment.insert_new_line_for_parameter&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_INSERT_NEW_LINE_FOR_PARAMETER = "com.aptana.ui.epl.comment.insert_new_line_for_parameter"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the line length for comments.
	 *     - option id:         &quot;com.aptana.ui.epl.comment.line_length&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;80&quot;
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public final static String FORMATTER_COMMENT_LINE_LENGTH = "com.aptana.ui.epl.comment.line_length"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to compact else/if
	 *     - option id:         &quot;com.aptana.ui.epl.compact_else_if&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_COMPACT_ELSE_IF = UIEplPlugin.PLUGIN_ID + ".formatter.compact_else_if"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to set the continuation indentation
	 *     - option id:         &quot;com.aptana.ui.epl.continuation_indentation&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;2&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_CONTINUATION_INDENTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.continuation_indentation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to set the continuation indentation inside array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.continuation_indentation_for_array_initializer&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;2&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_CONTINUATION_INDENTATION_FOR_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.continuation_indentation_for_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing annotation declaration header
	 *     - option id:         &quot;com.aptana.ui.epl.indent_body_declarations_compare_to_annotation_declaration_header&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.2
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ANNOTATION_DECLARATION_HEADER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_body_declarations_compare_to_annotation_declaration_header"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing enum constant header
	 *     - option id:         &quot;com.aptana.ui.epl.indent_body_declarations_compare_to_enum_constant_header&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ENUM_CONSTANT_HEADER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_body_declarations_compare_to_enum_constant_header"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing enum declaration header
	 *     - option id:         &quot;com.aptana.ui.epl.indent_body_declarations_compare_to_enum_declaration_header&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ENUM_DECLARATION_HEADER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_body_declarations_compare_to_enum_declaration_header"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing type header
	 *     - option id:         &quot;com.aptana.ui.epl.indent_body_declarations_compare_to_type_header&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_body_declarations_compare_to_type_header"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent breaks compare to cases
	 *     - option id:         &quot;com.aptana.ui.epl.indent_breaks_compare_to_cases&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_breaks_compare_to_cases"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent empty lines
	 *     - option id:         &quot;com.aptana.ui.epl.indent_empty_lines&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.2
	 */
	public static final String FORMATTER_INDENT_EMPTY_LINES = UIEplPlugin.PLUGIN_ID + ".formatter.indent_empty_lines"; //$NON-NLS-1$	
	/**
	 * <pre>
	 * FORMATTER / Option to indent statements inside a block
	 *     - option id:         &quot;com.aptana.ui.epl.indent_statements_compare_to_block&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_statements_compare_to_block"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent statements inside the body of a method or a constructor
	 *     - option id:         &quot;com.aptana.ui.epl.indent_statements_compare_to_body&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_statements_compare_to_body"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent switch statements compare to cases
	 *     - option id:         &quot;com.aptana.ui.epl.indent_switchstatements_compare_to_cases&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_switchstatements_compare_to_cases"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent switch statements compare to switch
	 *     - option id:         &quot;com.aptana.ui.epl.indent_switchstatements_compare_to_switch&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.indent_switchstatements_compare_to_switch"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the equivalent number of spaces that represents one indentation 
	 *     - option id:         &quot;com.aptana.ui.epl.indentation.size&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;4&quot;
	 * </pre>
	 * <p>
	 * This option is used only if the tab char is set to MIXED.
	 * </p>
	 * 
	 * @see #FORMATTER_TAB_CHAR
	 * @since 3.1
	 */
	public static final String FORMATTER_INDENTATION_SIZE = UIEplPlugin.PLUGIN_ID + ".formatter.indentation.size"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line after an annotation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_after_annotation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_after_annotation";//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line after the opening brace in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_after_opening_brace_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_after_opening_brace_in_array_initializer";//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line at the end of the current file if missing
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_at_end_of_file_if_missing&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_at_end_of_file_if_missing";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the catch keyword in try statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_before_catch_in_try_statement&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_before_catch_in_try_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the closing brace in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_before_closing_brace_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_before_closing_brace_in_array_initializer";//$NON-NLS-1$
	/**
	 * Option to insert a new line before if statemets
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_before_if_statement"; //$NON-NLS-1$
	/**
	 * Option to insert a new line before return statements
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_before_return_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the else keyword in if statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_before_else_in_if_statement&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_before_else_in_if_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the finally keyword in try statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_before_finally_in_try_statement&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_before_finally_in_try_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before while in do statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_before_while_in_do_statement&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_before_while_in_do_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty annotation declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_in_empty_annotation_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.2
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ANNOTATION_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_in_empty_annotation_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty anonymous type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_in_empty_anonymous_type_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ANONYMOUS_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_in_empty_anonymous_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty block
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_in_empty_block&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_in_empty_block"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_in_empty_enum_constant&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_in_empty_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty enum declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_in_empty_enum_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_WRAP_TAGS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_in_empty_enum_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty method body
	 *     - option id:         &quot;com.aptana.ui.epl.insert_new_line_in_empty_method_body&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_METHOD_BODY = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_in_empty_method_body"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to store set of tags that should not be wrapped
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_DO_NOT_WRAP_TAGS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_new_line_in_empty_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to store set of tags that should not be indented
	 * 
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_DO_NOT_INDENT_TAGS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_and_in_type_parameter"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after an assignment operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_assignment_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_assignment_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after at in annotation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_at_in_annotation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_at_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after at in annotation type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_at_in_annotation_type_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_at_in_annotation_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after a binary operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_binary_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_binary_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing angle bracket in type arguments
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_closing_angle_bracket_in_type_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_closing_angle_bracket_in_type_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing angle bracket in type parameters
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_closing_angle_bracket_in_type_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_closing_angle_bracket_in_type_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing brace of a block
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_closing_brace_in_block&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_closing_brace_in_block"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing parenthesis of a cast expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_closing_paren_in_cast&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_CAST = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_closing_paren_in_cast"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the colon in an assert statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_colon_in_assert&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_ASSERT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_colon_in_assert"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after colon in a case statement when a opening brace follows the colon
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_colon_in_case&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CASE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_colon_in_case"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the colon in a conditional expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_colon_in_conditional&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CONDITIONAL = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_colon_in_conditional"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after colon in a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_colon_in_for&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_FOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_colon_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the colon in a labeled statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_colon_in_labeled_statement&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_LABELED_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_colon_in_labeled_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in an allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_allocation_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_allocation_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in annotation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_annotation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ANNOTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the parameters of a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_constructor_declaration_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_constructor_declaration_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the exception names in a throws clause of a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_constructor_declaration_throws&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_constructor_declaration_throws"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the arguments of an enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_enum_constant_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_CONSTANT_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_enum_constant_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in enum declarations
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_enum_declarations&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_DECLARATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_enum_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the arguments of an explicit constructor call
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_explicitconstructorcall_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_explicitconstructorcall_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the increments of a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_for_increments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INCREMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_for_increments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the initializations of a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_for_inits&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_for_inits"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the parameters of a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_method_declaration_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_method_declaration_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the exception names in a throws clause of a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_method_declaration_throws&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_THROWS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_method_declaration_throws"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the arguments of a method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_method_invocation_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_method_invocation_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in multiple field declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_multiple_field_declarations&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_multiple_field_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in multiple local declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_multiple_local_declarations&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_multiple_local_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in parameterized type reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_parameterized_type_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_parameterized_type_reference"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in superinterfaces names of a type header
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_superinterfaces&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SUPERINTERFACES = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_superinterfaces"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in type arguments
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_type_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_type_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in type parameters
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_comma_in_type_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_comma_in_type_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after ellipsis
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_ellipsis&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_ELLIPSIS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_ellipsis"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening angle bracket in parameterized type reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_angle_bracket_in_parameterized_type_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_angle_bracket_in_parameterized_type_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening angle bracket in type arguments
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_angle_bracket_in_type_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_angle_bracket_in_type_arguments";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening angle bracket in type parameters
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_angle_bracket_in_type_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_angle_bracket_in_type_parameters";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening brace in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_brace_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_brace_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening bracket inside an array allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_bracket_in_array_allocation_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_bracket_in_array_allocation_expression";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening bracket inside an array reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_bracket_in_array_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_bracket_in_array_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in annotation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_annotation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ANNOTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a cast expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_cast&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CAST = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_cast"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a catch
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_catch&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_catch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_constructor_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_enum_constant&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_for&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in an if statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_if&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_if"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_method_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_method_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_method_invocation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a parenthesized expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_parenthesized_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_parenthesized_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a switch statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_switch&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SWITCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_switch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a synchronized statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_synchronized&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SYNCHRONIZED = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_synchronized"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a while statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_opening_paren_in_while&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_opening_paren_in_while"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after a postfix operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_postfix_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_POSTFIX_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_postfix_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after a prefix operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_prefix_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_PREFIX_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_prefix_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after question mark in a conditional expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_question_in_conditional&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_CONDITIONAL = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_question_in_conditional"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after question mark in a wildcard
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_question_in_wildcard&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_WILDCARD = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_question_in_wildcard"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after semicolon in a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_semicolon_in_for&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_semicolon_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after an unary operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_after_unary_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_UNARY_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_after_unary_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before and in wildcard
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_and_in_type_parameter&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_AND_IN_TYPE_PARAMETER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_and_in_type_parameter"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before an assignment operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_assignment_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_assignment_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before at in annotation type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_at_in_annotation_type_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_AT_IN_ANNOTATION_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_at_in_annotation_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before an binary operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_binary_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_binary_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing angle bracket in parameterized type reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_angle_bracket_in_parameterized_type_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_angle_bracket_in_parameterized_type_reference"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing angle bracket in type arguments
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_angle_bracket_in_type_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_angle_bracket_in_type_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing angle bracket in type parameters
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_angle_bracket_in_type_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_angle_bracket_in_type_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing brace in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_brace_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_brace_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing bracket in an array allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_bracket_in_array_allocation_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_bracket_in_array_allocation_expression";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing bracket in an array reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_bracket_in_array_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_bracket_in_array_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in annotation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_annotation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ANNOTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a cast expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_cast&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CAST = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_cast"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a catch
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_catch&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CATCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_catch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_constructor_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_enum_constant&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_for&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in an if statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_if&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_IF = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_if"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_method_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_method_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_method_invocation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a parenthesized expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_parenthesized_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_PARENTHESIZED_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_parenthesized_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a switch statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_switch&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SWITCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_switch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a synchronized statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_synchronized&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SYNCHRONIZED = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_synchronized"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a while statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_closing_paren_in_while&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_closing_paren_in_while"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in an assert statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_colon_in_assert&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_ASSERT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_colon_in_assert"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a case statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_colon_in_case&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CASE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_colon_in_case"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a conditional expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_colon_in_conditional&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CONDITIONAL = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_colon_in_conditional"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a default statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_colon_in_default&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_DEFAULT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_colon_in_default"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_colon_in_for&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_FOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_colon_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a labeled statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_colon_in_labeled_statement&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_LABELED_STATEMENT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_colon_in_labeled_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in an allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_allocation_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_allocation_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in annotation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_annotation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ANNOTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the parameters of a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_constructor_declaration_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_constructor_declaration_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the exception names of the throws clause of a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_constructor_declaration_throws&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_constructor_declaration_throws"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the arguments of enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_enum_constant_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_CONSTANT_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_enum_constant_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in enum declarations
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_enum_declarations&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_DECLARATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_enum_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the arguments of an explicit constructor call
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_explicitconstructorcall_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_explicitconstructorcall_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the increments of a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_for_increments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INCREMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_for_increments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the initializations of a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_for_inits&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_for_inits"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the parameters of a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_method_declaration_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_method_declaration_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the exception names of the throws clause of a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_method_declaration_throws&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_THROWS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_method_declaration_throws"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the arguments of a method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_method_invocation_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_method_invocation_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in a multiple field declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_multiple_field_declarations&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_multiple_field_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in a multiple local declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_multiple_local_declarations&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_multiple_local_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in parameterized type reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_parameterized_type_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_parameterized_type_reference"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the superinterfaces names in a type header
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_superinterfaces&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_SUPERINTERFACES = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_superinterfaces"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in type arguments
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_type_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_type_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in type parameters
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_comma_in_type_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_comma_in_type_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before ellipsis
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_ellipsis&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ELLIPSIS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_ellipsis"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening angle bracket in parameterized type reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_angle_bracket_in_parameterized_type_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_angle_bracket_in_parameterized_type_reference"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening angle bracket in type arguments
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_angle_bracket_in_type_arguments&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_angle_bracket_in_type_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening angle bracket in type parameters
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_angle_bracket_in_type_parameters&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_angle_bracket_in_type_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an annotation type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_annotation_type_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ANNOTATION_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_annotation_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an anonymous type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_anonymous_type_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ANONYMOUS_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_anonymous_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a block
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_block&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_BLOCK = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_block"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_constructor_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_enum_constant&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an enum declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_enum_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_enum_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_method_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_method_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a switch statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_switch&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_SWITCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_switch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a type declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_brace_in_type_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_TYPE_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_brace_in_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening bracket in an array allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_bracket_in_array_allocation_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_bracket_in_array_allocation_expression";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening bracket in an array reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_bracket_in_array_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_bracket_in_array_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening bracket in an array type reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_bracket_in_array_type_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_TYPE_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_bracket_in_array_type_reference"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in annotation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_annotation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in annotation type member declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_annotation_type_member_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION_TYPE_MEMBER_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_annotation_type_member_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a catch
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_catch&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_catch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_constructor_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_enum_constant&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_for&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in an if statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_if&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_if"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_method_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_method_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_method_invocation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a parenthesized expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_parenthesized_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_parenthesized_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a switch statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_switch&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_switch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a synchronized statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_synchronized&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SYNCHRONIZED = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_synchronized"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a while statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_opening_paren_in_while&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_opening_paren_in_while"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before parenthesized expression in return statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_parenthesized_expression_in_return&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.2
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_RETURN = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_parenthesized_expression_in_return"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before parenthesized expression in throw statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_parenthesized_expression_in_throw&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.3
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_THROW = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_parenthesized_expression_in_throw"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before a postfix operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_postfix_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_POSTFIX_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_postfix_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before a prefix operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_prefix_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_PREFIX_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_prefix_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before question mark in a conditional expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_question_in_conditional&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_CONDITIONAL = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_question_in_conditional"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before question mark in a wildcard
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_question_in_wildcard&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_WILDCARD = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_question_in_wildcard"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before semicolon
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_semicolon&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_semicolon"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before semicolon in for statement
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_semicolon_in_for&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_semicolon_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before unary operator
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_before_unary_operator&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_UNARY_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_before_unary_operator"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between brackets in an array type reference
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_brackets_in_array_type_reference&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_BRACKETS_IN_ARRAY_TYPE_REFERENCE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_brackets_in_array_type_reference"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty braces in an array initializer
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_empty_braces_in_array_initializer&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACES_IN_ARRAY_INITIALIZER = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_braces_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty brackets in an array allocation expression
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_empty_brackets_in_array_allocation_expression&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACKETS_IN_ARRAY_ALLOCATION_EXPRESSION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_brackets_in_array_allocation_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in an annotation type member declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_empty_parens_in_annotation_type_member_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String DO_NOT_WRAP_SIMPLE_TAGS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_parens_in_annotation_type_member_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in a constructor declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_empty_parens_in_constructor_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CONSTRUCTOR_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_parens_in_constructor_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in enum constant
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_empty_parens_in_enum_constant&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_ENUM_CONSTANT = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_parens_in_enum_constant"; //$NON-NLS-1$

	public static final String NO_FORMATTING = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_parens_in_enum_constant"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in a method declaration
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_empty_parens_in_method_declaration&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_DECLARATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_parens_in_method_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in a method invocation
	 *     - option id:         &quot;com.aptana.ui.epl.insert_space_between_empty_parens_in_method_invocation&quot;
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION = UIEplPlugin.PLUGIN_ID
			+ ".formatter.insert_space_between_empty_parens_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep else statement on the same line
	 *     - option id:         &quot;com.aptana.ui.epl.keep_else_statement_on_same_line&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.keep_else_statement_on_same_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep empty array initializer one one line
	 *     - option id:         &quot;com.aptana.ui.epl.keep_empty_array_initializer_on_one_line&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.keep_empty_array_initializer_on_one_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep guardian clause on one line
	 *     - option id:         &quot;com.aptana.ui.epl.format_guardian_clause_on_one_line&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.format_guardian_clause_on_one_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep simple if statement on the one line
	 *     - option id:         &quot;com.aptana.ui.epl.keep_imple_if_on_one_line&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.keep_imple_if_on_one_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep then statement on the same line
	 *     - option id:         &quot;com.aptana.ui.epl.keep_then_statement_on_same_line&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.keep_then_statement_on_same_line";//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the length of the page. Beyond this length, the formatter will try to split the code
	 *     - option id:         &quot;com.aptana.ui.epl.lineSplit&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;80&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_LINE_SPLIT = UIEplPlugin.PLUGIN_ID + ".formatter.lineSplit"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent block comments that start on the first column
	 *     - option id:         &quot;com.aptana.ui.epl.formatter.never_indent_block_comments_on_first_column&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * Note that this option is ignored if the formatter is created with the mode
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public static final String FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN = UIEplPlugin.PLUGIN_ID
			+ ".formatter.never_indent_block_comments_on_first_column"; //$NON-NLS-1$	
	/**
	 * <pre>
	 * FORMATTER / Option to indent line comments that start on the first column
	 *     - option id:         &quot;com.aptana.ui.epl.formatter.never_indent_line_comments_on_first_column&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * Note that this option is ignored if the formatter is created with the mode {}.
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public static final String FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN = UIEplPlugin.PLUGIN_ID
			+ ".formatter.never_indent_line_comments_on_first_column"; //$NON-NLS-1$	
	/**
	 * <pre>
	 * FORMATTER / Option to specify the number of empty lines to preserve
	 *     - option id:         &quot;com.aptana.ui.epl.number_of_empty_lines_to_preserve&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;0&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.number_of_empty_lines_to_preserve"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to specify whether or not empty statement should be on a new line
	 *     - option id:         &quot;com.aptana.ui.epl.put_empty_statement_on_new_line&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_PUT_EMPTY_STATEMENT_ON_NEW_LINE = UIEplPlugin.PLUGIN_ID
			+ ".formatter.put_empty_statement_on_new_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to specify the tabulation size
	 *     - option id:         &quot;com.aptana.ui.epl.tabulation.char&quot;
	 *     - possible values:   { TAB, SPACE, MIXED }
	 *     - default:           TAB
	 * </pre>
	 * 
	 * More values may be added in the future.
	 * 
	 * @see #MIXED
	 * @since 3.0
	 */
	public static final String FORMATTER_TAB_CHAR = BasicCodeFormatterConstants.FORMATTER_TAB_CHAR;
	/**
	 * <pre>
	 * FORMATTER / Option to specify the equivalent number of spaces that represents one tabulation 
	 *     - option id:         &quot;com.aptana.ui.epl.tabulation.size&quot;
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;4&quot;
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String FORMATTER_TAB_SIZE = BasicCodeFormatterConstants.FORMATTER_TAB_SIZE;

	/**
	 * <pre>
	 * FORMATTER / Option to use tabulations only for leading indentations 
	 *     - option id:         &quot;com.aptana.ui.epl.use_tabs_only_for_leading_indentations&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public static final String FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.use_tabs_only_for_leading_indentations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to wrap before the binary operator
	 *     - option id:         &quot;com.aptana.ui.epl.wrap_before_binary_operator&quot;
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * 
	 * This option is used only if the option {@link #FORMATTER_ALIGNMENT_FOR_BINARY_EXPRESSION} is set.
	 * 
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.3
	 */
	public static final String FORMATTER_WRAP_BEFORE_BINARY_OPERATOR = UIEplPlugin.PLUGIN_ID
			+ ".formatter.wrap_before_binary_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting by one compare to the current indentation.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int INDENT_BY_ONE = 2;

	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by using the current indentation.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int INDENT_DEFAULT = 0;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting on column under the splitting location.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int INDENT_ON_COLUMN = 1;

	/**
	 * <pre>
	 * FORMATTER / Possible value for the option FORMATTER_TAB_CHAR
	 * </pre>
	 * 
	 * @since 3.1
	 * @see #FORMATTER_TAB_CHAR
	 */
	public static final String MIXED = "mixed"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the start of the next line with
	 *             the right indentation.
	 * </pre>
	 * 
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
	public static final String NEXT_LINE = "next_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the start of the next line if a wrapping
	 *             occured.
	 * </pre>
	 * 
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
	public static final String NEXT_LINE_ON_WRAP = "next_line_on_wrap"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the start of the next line with
	 *             an extra indentation.
	 * </pre>
	 * 
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
	public static final String NEXT_LINE_SHIFTED = "next_line_shifted"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set an option to true.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final String TRUE = "true"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done using as few lines as possible.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int WRAP_COMPACT = 1;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done putting the first element on a new
	 *             line and then wrapping next elements using as few lines as possible.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int WRAP_COMPACT_FIRST_BREAK = 2;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by putting each element on its own line
	 *             except the first element.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int WRAP_NEXT_PER_LINE = 5;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by putting each element on its own line.
	 *             All elements are indented by one except the first element.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int WRAP_NEXT_SHIFTED = 4;

	/**
	 * <pre>
	 * FORMATTER / Value to disable alignment.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int WRAP_NO_SPLIT = 0;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by putting each element on its own line.
	 * </pre>
	 * 
	 * @since 3.0
	 */
	public static final int WRAP_ONE_PER_LINE = 3;

	/*
	 * Private constants. Not in javadoc
	 */
	private static final IllegalArgumentException WRONG_ARGUMENT = new IllegalArgumentException();

	/**
	 * Do we keep extra carriage returns?
	 */
	public static final String FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS = UIEplPlugin.PLUGIN_ID
			+ ".formatter.preserve_extra_carriage_returns"; //$NON-NLS-1$

	/**
	 * FORMATTER_DO_NOT_WRAP_TAGS2
	 */
	public static final String FORMATTER_DO_NOT_WRAP_TAGS2 = UIEplPlugin.PLUGIN_ID
			+ ".formatter.preserve_extra_carriage_returns2"; //$NON-NLS-1$

	/**
	 * FORMATTER_WRAP_TAGS2
	 */
	public static final String FORMATTER_WRAP_TAGS2 = UIEplPlugin.PLUGIN_ID + ".formatter.wraptags2"; //$NON-NLS-1$

	/**
	 * FORMATTER_PRESERVE_WHITESPACE_IN_CDATA
	 */
	public static final String FORMATTER_PRESERVE_WHITESPACE_IN_CDATA = UIEplPlugin.PLUGIN_ID
			+ ".formatter.preserveWhitespaceinCDATA"; //$NON-NLS-1$

	/**
	 * Create a new alignment value according to the given values. This must be used to set up the alignment options.
	 * 
	 * @param forceSplit
	 *            the given force value
	 * @param wrapStyle
	 *            the given wrapping style
	 * @param indentStyle
	 *            the given indent style
	 * @return the new alignement value
	 */
	public static String createAlignmentValue(boolean forceSplit, int wrapStyle, int indentStyle)
	{
		int alignmentValue = 0;
		switch (wrapStyle)
		{
			case WRAP_COMPACT:
				alignmentValue |= Alignment.M_COMPACT_SPLIT;
				break;
			case WRAP_COMPACT_FIRST_BREAK:
				alignmentValue |= Alignment.M_COMPACT_FIRST_BREAK_SPLIT;
				break;
			case WRAP_NEXT_PER_LINE:
				alignmentValue |= Alignment.M_NEXT_PER_LINE_SPLIT;
				break;
			case WRAP_NEXT_SHIFTED:
				alignmentValue |= Alignment.M_NEXT_SHIFTED_SPLIT;
				break;
			case WRAP_ONE_PER_LINE:
				alignmentValue |= Alignment.M_ONE_PER_LINE_SPLIT;
				break;
		}
		if (forceSplit)
		{
			alignmentValue |= Alignment.M_FORCE;
		}
		switch (indentStyle)
		{
			case INDENT_BY_ONE:
				alignmentValue |= Alignment.M_INDENT_BY_ONE;
				break;
			case INDENT_ON_COLUMN:
				alignmentValue |= Alignment.M_INDENT_ON_COLUMN;
		}
		return String.valueOf(alignmentValue);
	}

	/**
	 * Returns the formatter settings that most closely approximate the default formatter settings of Eclipse version
	 * 2.1.
	 * 
	 * @return the Eclipse 2.1 settings
	 * @since 3.0
	 */
	public static Map<String, String> getEclipse21Settings()
	{
		return new HashMap<String, String>();
		// return DefaultCodeFormatterOptions.getDefaultSettings().getMap();
	}

	/**
	 * Returns the default Eclipse formatter settings
	 * 
	 * @return the Eclipse default settings
	 * @since 3.1
	 */
	public static Map<String, String> getEclipseDefaultSettings()
	{
		return new HashMap<String, String>();
		// /return DefaultCodeFormatterOptions.getEclipseDefaultSettings().getMap();
	}

	/**
	 * <p>
	 * Return the force value of the given alignment value. The given alignment value should be created using the
	 * <code>createAlignmentValue(boolean, int, int)</code> API.
	 * </p>
	 * 
	 * @param value
	 *            the given alignment value
	 * @return the force value of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException
	 *                if the given alignment value is null, or if it doesn't have a valid format.
	 */
	public static boolean getForceWrapping(String value)
	{
		if (value == null)
		{
			throw WRONG_ARGUMENT;
		}
		try
		{
			int existingValue = Integer.parseInt(value);
			return (existingValue & Alignment.M_FORCE) != 0;
		}
		catch (NumberFormatException e)
		{
			throw WRONG_ARGUMENT;
		}
	}

	/**
	 * <p>
	 * Return the indentation style of the given alignment value. The given alignment value should be created using the
	 * <code>createAlignmentValue(boolean, int, int)</code> API.
	 * </p>
	 * 
	 * @param value
	 *            the given alignment value
	 * @return the indentation style of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException
	 *                if the given alignment value is null, or if it doesn't have a valid format.
	 */
	public static int getIndentStyle(String value)
	{
		if (value == null)
		{
			throw WRONG_ARGUMENT;
		}
		try
		{
			int existingValue = Integer.parseInt(value);
			if ((existingValue & Alignment.M_INDENT_BY_ONE) != 0)
			{
				return INDENT_BY_ONE;
			}
			else if ((existingValue & Alignment.M_INDENT_ON_COLUMN) != 0)
			{
				return INDENT_ON_COLUMN;
			}
			else
			{
				return INDENT_DEFAULT;
			}
		}
		catch (NumberFormatException e)
		{
			throw WRONG_ARGUMENT;
		}
	}

	/**
	 * Returns the settings according to the Java conventions.
	 * 
	 * @return the settings according to the Java conventions
	 * @since 3.0
	 */
	public static Map<String, String> getJavaConventionsSettings()
	{
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap
				.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT,
						CommentsTabPage.INSERT);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT,
				CommentsTabPage.INSERT);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT,
				CommentsTabPage.INSERT);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT,
				CommentsTabPage.INSERT);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT,
				CommentsTabPage.INSERT);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT,
				CommentsTabPage.INSERT);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, Boolean.FALSE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, Boolean.FALSE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, Boolean.FALSE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, Boolean.FALSE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE, Boolean.FALSE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " "); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_SPACES_BEFORE_ATTRS_ON_MULTILINE, "1"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY, Boolean.TRUE.toString());
		hashMap
				.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK, Boolean.TRUE
						.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH, Boolean.TRUE
				.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES, Boolean.TRUE
				.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES, Boolean.TRUE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_IMPORTS, "5"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD, "1"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, Boolean.TRUE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY, "0"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS, INITIAL_TAGS_DO_NOT_WRAP);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS, INITIAL_TAGS_TO_WRAP);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS, INITIAL_TAGS_TO_NOT_INDENT);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2, ""); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS2, ""); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION,
				DefaultCodeFormatterConstants.END_OF_LINE);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION,
				DefaultCodeFormatterConstants.END_OF_LINE);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK,
				DefaultCodeFormatterConstants.END_OF_LINE);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE,
				DefaultCodeFormatterConstants.END_OF_LINE);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH,
				DefaultCodeFormatterConstants.END_OF_LINE);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER,
				DefaultCodeFormatterConstants.END_OF_LINE);
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE, Boolean.TRUE
				.toString());
		hashMap.put(DefaultCodeFormatterConstants.NO_FORMATTING, ""); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_WHITESPACE_IN_CDATA, Boolean.TRUE.toString());
		hashMap.put(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, Boolean.FALSE.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK, Boolean.FALSE
				.toString());
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE, "2"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, "80"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION, "1"); //$NON-NLS-1$
		hashMap.put(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION_FOR_ARRAY_INITIALIZER, "2"); //$NON-NLS-1$

		return hashMap;
	}

	/**
	 * <p>
	 * Return the wrapping style of the given alignment value. The given alignment value should be created using the
	 * <code>createAlignmentValue(boolean, int, int)</code> API.
	 * </p>
	 * 
	 * @param value
	 *            the given alignment value
	 * @return the wrapping style of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException
	 *                if the given alignment value is null, or if it doesn't have a valid format.
	 */
	public static int getWrappingStyle(String value)
	{
		if (value == null)
		{
			return WRAP_COMPACT;
		}
		try
		{
			int existingValue = Integer.parseInt(value) & Alignment.SPLIT_MASK;
			switch (existingValue)
			{
				case Alignment.M_COMPACT_SPLIT:
					return WRAP_COMPACT;
				case Alignment.M_COMPACT_FIRST_BREAK_SPLIT:
					return WRAP_COMPACT_FIRST_BREAK;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					return WRAP_NEXT_PER_LINE;
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					return WRAP_NEXT_SHIFTED;
				case Alignment.M_ONE_PER_LINE_SPLIT:
					return WRAP_ONE_PER_LINE;
				default:
					return WRAP_NO_SPLIT;
			}
		}
		catch (NumberFormatException e)
		{
			return WRAP_COMPACT;
		}
	}

	/**
	 * <p>
	 * Set the force value of the given alignment value and return the new value. The given alignment value should be
	 * created using the <code>createAlignmentValue(boolean, int, int)</code> API.
	 * </p>
	 * 
	 * @param value
	 *            the given alignment value
	 * @param force
	 *            the given force value
	 * @return the new alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException
	 *                if the given alignment value is null, or if it doesn't have a valid format.
	 */
	public static String setForceWrapping(String value, boolean force)
	{
		if (value == null)
		{
			throw WRONG_ARGUMENT;
		}
		try
		{
			int existingValue = Integer.parseInt(value);
			// clear existing force bit
			existingValue &= ~Alignment.M_FORCE;
			if (force)
			{
				existingValue |= Alignment.M_FORCE;
			}
			return String.valueOf(existingValue);
		}
		catch (NumberFormatException e)
		{
			throw WRONG_ARGUMENT;
		}
	}

	/**
	 * <p>
	 * Set the indentation style of the given alignment value and return the new value. The given value should be
	 * created using the <code>createAlignmentValue(boolean, int, int)</code> API.
	 * </p>
	 * 
	 * @param value
	 *            the given alignment value
	 * @param indentStyle
	 *            the given indentation style
	 * @return the new alignment value
	 * @see #INDENT_BY_ONE
	 * @see #INDENT_DEFAULT
	 * @see #INDENT_ON_COLUMN
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException
	 *                if the given alignment value is null, if the given indentation style is not one of the possible
	 *                indentation styles, or if the given alignment value doesn't have a valid format.
	 */
	public static String setIndentStyle(String value, int indentStyle)
	{
		if (value == null)
		{
			throw WRONG_ARGUMENT;
		}
		switch (indentStyle)
		{
			case INDENT_BY_ONE:
			case INDENT_DEFAULT:
			case INDENT_ON_COLUMN:
				break;
			default:
				throw WRONG_ARGUMENT;
		}
		try
		{
			int existingValue = Integer.parseInt(value);
			// clear existing indent bits
			existingValue &= ~(Alignment.M_INDENT_BY_ONE | Alignment.M_INDENT_ON_COLUMN);
			switch (indentStyle)
			{
				case INDENT_BY_ONE:
					existingValue |= Alignment.M_INDENT_BY_ONE;
					break;
				case INDENT_ON_COLUMN:
					existingValue |= Alignment.M_INDENT_ON_COLUMN;
			}
			return String.valueOf(existingValue);
		}
		catch (NumberFormatException e)
		{
			throw WRONG_ARGUMENT;
		}
	}

	/**
	 * <p>
	 * Set the wrapping style of the given alignment value and return the new value. The given value should be created
	 * using the <code>createAlignmentValue(boolean, int, int)</code> API.
	 * </p>
	 * 
	 * @param value
	 *            the given alignment value
	 * @param wrappingStyle
	 *            the given wrapping style
	 * @return the new alignment value
	 * @see #WRAP_COMPACT
	 * @see #WRAP_COMPACT_FIRST_BREAK
	 * @see #WRAP_NEXT_PER_LINE
	 * @see #WRAP_NEXT_SHIFTED
	 * @see #WRAP_NO_SPLIT
	 * @see #WRAP_ONE_PER_LINE
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException
	 *                if the given alignment value is null, if the given wrapping style is not one of the possible
	 *                wrapping styles, or if the given alignment value doesn't have a valid format.
	 */
	public static String setWrappingStyle(String value, int wrappingStyle)
	{
		if (value == null)
		{
			throw WRONG_ARGUMENT;
		}
		switch (wrappingStyle)
		{
			case WRAP_COMPACT:
			case WRAP_COMPACT_FIRST_BREAK:
			case WRAP_NEXT_PER_LINE:
			case WRAP_NEXT_SHIFTED:
			case WRAP_NO_SPLIT:
			case WRAP_ONE_PER_LINE:
				break;
			default:
				throw WRONG_ARGUMENT;
		}
		try
		{
			int existingValue = Integer.parseInt(value);
			// clear existing split bits
			existingValue &= ~(Alignment.SPLIT_MASK);
			switch (wrappingStyle)
			{
				case WRAP_COMPACT:
					existingValue |= Alignment.M_COMPACT_SPLIT;
					break;
				case WRAP_COMPACT_FIRST_BREAK:
					existingValue |= Alignment.M_COMPACT_FIRST_BREAK_SPLIT;
					break;
				case WRAP_NEXT_PER_LINE:
					existingValue |= Alignment.M_NEXT_PER_LINE_SPLIT;
					break;
				case WRAP_NEXT_SHIFTED:
					existingValue |= Alignment.M_NEXT_SHIFTED_SPLIT;
					break;
				case WRAP_ONE_PER_LINE:
					existingValue |= Alignment.M_ONE_PER_LINE_SPLIT;
					break;
			}
			return String.valueOf(existingValue);
		}
		catch (NumberFormatException e)
		{
			throw WRONG_ARGUMENT;
		}
	}
}
