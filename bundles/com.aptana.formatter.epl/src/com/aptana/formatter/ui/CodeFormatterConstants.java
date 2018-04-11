/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui;

public class CodeFormatterConstants
{

	/**
	 * <pre>
	 * FORMATTER / Option to specify the tabulation size
	 *     - possible values:   { TAB, SPACE, MIXED }
	 *     - default:           TAB
	 * </pre>
	 * 
	 * More values may be added in the future.
	 */
	public static final String FORMATTER_TAB_CHAR = "formatter.tabulation.char"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the equivalent number of spaces that represents one tabulation 
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;4&quot;
	 * </pre>
	 */
	public static final String FORMATTER_TAB_SIZE = "formatter.tabulation.size"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the equivalent number of spaces that represents one indentation 
	 *     - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
	 *     - default:           &quot;4&quot;
	 * </pre>
	 * <p>
	 * This option is used only if the tab char is set to MIXED.
	 * </p>
	 * 
	 * @see #FORMATTER_TAB_CHAR
	 */
	public static final String FORMATTER_INDENTATION_SIZE = "formatter.indentation.size"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Possible value for the option FORMATTER_TAB_CHAR
	 * </pre>
	 * 
	 * @see #FORMATTER_TAB_CHAR
	 */
	public static final String MIXED = "mixed"; //$NON-NLS-1$
	
	/**
	 * <pre>
	 * FORMATTER / Possible value for the option FORMATTER_TAB_CHAR (tab char by editor's setting)
	 * </pre>
	 * 
	 * @see #FORMATTER_TAB_CHAR
	 */
	public static final String EDITOR = "editor"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Possible value for the option FORMATTER_TAB_CHAR
	 * </pre>
	 * 
	 * @see #FORMATTER_TAB_CHAR
	 */
	public static final String TAB = "tab"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Possible value for the option FORMATTER_TAB_CHAR
	 * </pre>
	 * 
	 * @see #FORMATTER_TAB_CHAR
	 */
	public static final String SPACE = "space"; //$NON-NLS-1$

	/**
	 * Indicate that the brace should be positioned in the same line.
	 * 
	 * <pre>
	 * FORMATTER / Possible value for the Brace position formatter option
	 * </pre>
	 */
	public static final String SAME_LINE = "same.line"; //$NON-NLS-1$

	/**
	 * Indicate that the brace should be positioned in a new line.
	 * 
	 * <pre>
	 * FORMATTER / Possible value for the Brace position formatter option
	 * </pre>
	 */
	public static final String NEW_LINE = "new.line"; //$NON-NLS-1$
}
