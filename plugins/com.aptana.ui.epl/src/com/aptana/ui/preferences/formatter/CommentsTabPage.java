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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * Tab page for the comment formatter settings.
 */
@SuppressWarnings( { "rawtypes", "unchecked" })
public class CommentsTabPage extends FormatterTabPage
{
	/**
	 * 
	 */
	public static final String DO_NOT_INSERT = "not-insert"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String INSERT = "insert"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String SPACE = " "; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String FORMATTER_PROFILE = "formatter_profile"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String TAB = "\t"; //$NON-NLS-1$

	/**
	 * Constant array for boolean selection
	 */
	private static String[] FALSE_TRUE = { DefaultCodeFormatterConstants.FALSE, DefaultCodeFormatterConstants.TRUE };

	/**
	 * Constant array for insert / not_insert.
	 */
	private static String[] DO_NOT_INSERT_INSERT = { DO_NOT_INSERT, INSERT };

	private static abstract class Controller implements Observer
	{

		private final Collection<CheckboxPreference> fMasters;
		private final Collection fSlaves;

		/**
		 * @param masters
		 * @param slaves
		 */
		public Controller(Collection<CheckboxPreference> masters, Collection slaves)
		{
			fMasters = masters;
			fSlaves = slaves;
			for (final Iterator<CheckboxPreference> iter = fMasters.iterator(); iter.hasNext();)
			{
				iter.next().addObserver(this);
			}
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg)
		{
			boolean enabled = areSlavesEnabled();

			for (final Iterator iter = fSlaves.iterator(); iter.hasNext();)
			{
				final Object obj = iter.next();
				if (obj instanceof Preference)
				{
					((Preference) obj).setEnabled(enabled);
				}
				else if (obj instanceof Control)
				{
					((Group) obj).setEnabled(enabled);
				}
			}
		}

		/**
		 * @return s
		 */
		public Collection getMasters()
		{
			return fMasters;
		}

		/**
		 * @return s
		 */
		@SuppressWarnings("unused")
		public Collection getSlaves()
		{
			return fSlaves;
		}

		/**
		 * @return s
		 */
		protected abstract boolean areSlavesEnabled();
	}

	private final static class OrController extends Controller
	{

		/**
		 * @param masters
		 * @param slaves
		 */
		public OrController(Collection masters, Collection slaves)
		{
			super(masters, slaves);
			update(null, null);
		}

		/**
		 * {@inheritDoc}
		 */
		protected boolean areSlavesEnabled()
		{
			for (final Iterator iter = getMasters().iterator(); iter.hasNext();)
			{
				if (((CheckboxPreference) iter.next()).getChecked())
					return true;
			}
			return false;
		}
	}

	private final String PREVIEW = createPreviewHeader("An example for comment formatting. This example is meant to illustrate the various possibilities offered by <i>Eclipse</i> in order to format comments.") + //$NON-NLS-1$
			"package mypackage;\n"//$NON-NLS-1$
			+ "/**\n"//$NON-NLS-1$
			+ " * This is the comment for the example interface.\n"//$NON-NLS-1$
			+ " */\n"//$NON-NLS-1$
			+ " interface Example {\n"//$NON-NLS-1$
			+ "// This is a long comment that should be split in multiple line comments in case the line comment formatting is enabled\n"//$NON-NLS-1$
			+ "int foo3();\n"//$NON-NLS-1$
			+ "/*\n"//$NON-NLS-1$
			+ "*\n"//$NON-NLS-1$
			+ "* These possibilities include:\n"//$NON-NLS-1$
			+ "* <ul><li>Formatting of header comments.</li><li>Formatting of Javadoc tags</li></ul>\n"//$NON-NLS-1$
			+ "*/\n"//$NON-NLS-1$
			+ "int foo4();\n"//$NON-NLS-1$
			+ " /**\n"//$NON-NLS-1$
			+ " *\n"//$NON-NLS-1$
			+ " * These possibilities include:\n"//$NON-NLS-1$
			+ " * <ul><li>Formatting of header comments.</li><li>Formatting of Javadoc tags</li></ul>\n"//$NON-NLS-1$
			+ " */\n"//$NON-NLS-1$
			+ " int bar();\n"//$NON-NLS-1$
			+ " /*\n"//$NON-NLS-1$
			+ " *\n"//$NON-NLS-1$
			+ " * These possibilities include:\n"//$NON-NLS-1$
			+ " * <ul><li>Formatting of header comments.</li><li>Formatting of Javadoc tags</li></ul>\n"//$NON-NLS-1$
			+ " */\n"//$NON-NLS-1$
			+ " int bar2();"//$NON-NLS-1$
			+ " // This is a long comment that should be split in multiple line comments in case the line comment formatting is enabled\n"//$NON-NLS-1$
			+ " int foo2();" + //$NON-NLS-1$
			" /**\n" + //$NON-NLS-1$
			" * The following is some sample code which illustrates source formatting within javadoc comments:\n" + //$NON-NLS-1$
			" * <pre>public class Example {final int a= 1;final boolean b= true;}</pre>\n" + //$NON-NLS-1$ 
			" * Descriptions of parameters and return values are best appended at end of the javadoc comment.\n" + //$NON-NLS-1$
			" * @param a The first parameter. For an optimum result, this should be an odd number\n" + //$NON-NLS-1$
			" * between 0 and 100.\n" + //$NON-NLS-1$
			" * @param b The second parameter.\n" + //$NON-NLS-1$
			" * @return The result of the foo operation, usually within 0 and 1000.\n" + //$NON-NLS-1$
			" */" + //$NON-NLS-1$
			" int foo(int a, int b);\n" + //$NON-NLS-1$
			"}"; //$NON-NLS-1$

	private CompilationUnitPreview fPreview;

	private String editor;

	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public CommentsTabPage(ModifyDialog modifyDialog, Map<String, String> workingValues, String editor)
	{
		super(modifyDialog, workingValues);
		this.editor = editor;
	}

	protected void doCreatePreferences(Composite composite, int numColumns)
	{

		// global group
		final Group globalGroup = createGroup(numColumns, composite, FormatterMessages.CommentsTabPage_group1_title);
		final CheckboxPreference javadoc = createPrefTrueFalse(globalGroup, numColumns,
				FormatterMessages.commentsTabPage_enable_javadoc_comment_formatting,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT);
		final CheckboxPreference blockComment = createPrefTrueFalse(globalGroup, numColumns,
				FormatterMessages.CommentsTabPage_enable_block_comment_formatting,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_BLOCK_COMMENT);
		final CheckboxPreference singleLineComments = createPrefTrueFalse(globalGroup, numColumns,
				FormatterMessages.CommentsTabPage_enable_line_comment_formatting,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_LINE_COMMENT);
		final CheckboxPreference header = createPrefTrueFalse(globalGroup, numColumns,
				FormatterMessages.CommentsTabPage_format_header,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_HEADER);
		createPrefTrueFalse(globalGroup, numColumns,
				FormatterMessages.CommentsTabPage_never_indent_block_comments_on_first_column,
				DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN);
		createPrefTrueFalse(globalGroup, numColumns,
				FormatterMessages.CommentsTabPage_never_indent_line_comments_on_first_column,
				DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN);

		// javadoc comment formatting settings
		final Group settingsGroup = createGroup(numColumns, composite, FormatterMessages.CommentsTabPage_group2_title);
		final CheckboxPreference html = createPrefTrueFalse(settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_format_html,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_HTML);
		final CheckboxPreference code = createPrefTrueFalse(settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_format_code_snippets,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_SOURCE);
		final CheckboxPreference blankJavadoc = createPrefInsert(settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_blank_line_before_javadoc_tags,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_INSERT_EMPTY_LINE_BEFORE_ROOT_TAGS);
		final CheckboxPreference indentJavadoc = createPrefTrueFalse(settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_indent_javadoc_tags,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_INDENT_ROOT_TAGS);
		final CheckboxPreference indentDesc = createCheckboxPref(settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_indent_description_after_param,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_INDENT_PARAMETER_DESCRIPTION, FALSE_TRUE);
		((GridData) indentDesc.getControl().getLayoutData()).horizontalIndent = fPixelConverter
				.convertWidthInCharsToPixels(4);
		final CheckboxPreference nlParam = createPrefInsert(settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_new_line_after_param_tags,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_INSERT_NEW_LINE_FOR_PARAMETER);
		final CheckboxPreference blankLinesJavadoc = createPrefTrueFalse(settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_clear_blank_lines,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_JAVADOC_COMMENT);

		// block comment settings
		final Group blockSettingsGroup = createGroup(numColumns, composite,
				FormatterMessages.CommentsTabPage_group4_title);
		final CheckboxPreference blankLinesBlock = createPrefTrueFalse(blockSettingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_remove_blank_block_comment_lines,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_BLOCK_COMMENT);

		final Group widthGroup = createGroup(numColumns, composite, FormatterMessages.CommentsTabPage_group3_title);
		final NumberPreference lineWidth = createNumberPref(widthGroup, numColumns,
				FormatterMessages.CommentsTabPage_line_width,
				DefaultCodeFormatterConstants.FORMATTER_COMMENT_LINE_LENGTH, 0, 9999);

		ArrayList<CheckboxPreference> javaDocMaster = new ArrayList<CheckboxPreference>();
		javaDocMaster.add(javadoc);
		javaDocMaster.add(header);

		ArrayList javaDocSlaves = new ArrayList();
		javaDocSlaves.add(settingsGroup);
		javaDocSlaves.add(html);
		javaDocSlaves.add(code);
		javaDocSlaves.add(blankJavadoc);
		javaDocSlaves.add(indentJavadoc);
		javaDocSlaves.add(nlParam);
		javaDocSlaves.add(blankLinesJavadoc);

		new OrController(javaDocMaster, javaDocSlaves);

		ArrayList indentMasters = new ArrayList();
		indentMasters.add(javadoc);
		indentMasters.add(header);
		indentMasters.add(indentJavadoc);

		ArrayList indentSlaves = new ArrayList();
		indentSlaves.add(indentDesc);

		new Controller(indentMasters, indentSlaves)
		{
			protected boolean areSlavesEnabled()
			{
				return (javadoc.getChecked() || header.getChecked()) && indentJavadoc.getChecked();
			}
		}.update(null, null);

		ArrayList blockMasters = new ArrayList();
		blockMasters.add(blockComment);
		blockMasters.add(header);

		ArrayList blockSlaves = new ArrayList();
		blockSlaves.add(blockSettingsGroup);
		blockSlaves.add(blankLinesBlock);

		new OrController(blockMasters, blockSlaves);

		ArrayList lineWidthMasters = new ArrayList();
		lineWidthMasters.add(javadoc);
		lineWidthMasters.add(blockComment);
		lineWidthMasters.add(singleLineComments);
		lineWidthMasters.add(header);

		ArrayList lineWidthSlaves = new ArrayList();
		lineWidthSlaves.add(widthGroup);
		lineWidthSlaves.add(lineWidth);

		new OrController(lineWidthMasters, lineWidthSlaves);
	}

	protected void initializePage()
	{
		fPreview.setPreviewText(PREVIEW);
	}

	protected Preview doCreateJavaPreview(Composite parent)
	{
		fPreview = new CompilationUnitPreview(fWorkingValues, parent, editor, null);
		return fPreview;
	}

	protected void doUpdatePreview()
	{
		super.doUpdatePreview();
		fPreview.update();
	}

	private CheckboxPreference createPrefTrueFalse(Composite composite, int numColumns, String text, String key)
	{
		return createCheckboxPref(composite, numColumns, text, key, FALSE_TRUE);
	}

	private CheckboxPreference createPrefInsert(Composite composite, int numColumns, String text, String key)
	{
		return createCheckboxPref(composite, numColumns, text, key, DO_NOT_INSERT_INSERT);
	}
}
