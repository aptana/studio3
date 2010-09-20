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
package com.aptana.editor.ruby.formatter;

import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * Ruby code formatter constants.<br>
 * Since the formatters will be saved in a unified XML file, it's important to have a unique key for every setting. The
 * Ruby formatter constants are all starting with the {@link #FORMATTER_ID} string.
 */
public interface RubyFormatterConstants
{
	/**
	 * Ruby formatter ID.
	 */
	public static final String FORMATTER_ID = "ruby.formatter"; //$NON-NLS-1$

	public static final String FORMATTER_TAB_CHAR = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_CHAR;
	public static final String FORMATTER_TAB_SIZE = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_SIZE;
	public static final String FORMATTER_INDENTATION_SIZE = FORMATTER_ID + '.'
			+ CodeFormatterConstants.FORMATTER_INDENTATION_SIZE;

	public static final String INDENT_CLASS = FORMATTER_ID + ".indent.class"; //$NON-NLS-1$
	public static final String INDENT_MODULE = FORMATTER_ID + ".indent.module"; //$NON-NLS-1$
	public static final String INDENT_METHOD = FORMATTER_ID + ".indent.method"; //$NON-NLS-1$
	public static final String INDENT_BLOCKS = FORMATTER_ID + ".indent.blocks"; //$NON-NLS-1$
	public static final String INDENT_IF = FORMATTER_ID + ".indent.if"; //$NON-NLS-1$
	public static final String INDENT_CASE = FORMATTER_ID + ".indent.case"; //$NON-NLS-1$
	public static final String INDENT_WHEN = FORMATTER_ID + ".indent.when"; //$NON-NLS-1$

	public static final String LINES_FILE_AFTER_REQUIRE = FORMATTER_ID + ".line.file.require.after"; //$NON-NLS-1$

	public static final String LINES_FILE_BETWEEN_MODULE = FORMATTER_ID + ".line.file.module.between"; //$NON-NLS-1$
	public static final String LINES_FILE_BETWEEN_CLASS = FORMATTER_ID + ".line.file.class.between"; //$NON-NLS-1$
	public static final String LINES_FILE_BETWEEN_METHOD = FORMATTER_ID + ".line.file.method.between"; //$NON-NLS-1$

	public static final String LINES_BEFORE_FIRST = FORMATTER_ID + ".line.first.before"; //$NON-NLS-1$
	public static final String LINES_BEFORE_MODULE = FORMATTER_ID + ".line.module.before"; //$NON-NLS-1$
	public static final String LINES_BEFORE_CLASS = FORMATTER_ID + ".line.class.before"; //$NON-NLS-1$
	public static final String LINES_BEFORE_METHOD = FORMATTER_ID + ".line.method.before"; //$NON-NLS-1$

	public static final String LINES_PRESERVE = FORMATTER_ID + ".lines.preserve"; //$NON-NLS-1$

	public static final String WRAP_COMMENTS = FORMATTER_ID + ".wrap.comments"; //$NON-NLS-1$
	public static final String WRAP_COMMENTS_LENGTH = FORMATTER_ID + ".wrap.comments.length"; //$NON-NLS-1$
}
