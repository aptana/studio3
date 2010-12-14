package com.aptana.editor.css.formatter.tests;

import java.util.HashMap;
import java.util.Map;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.ui.CodeFormatterConstants;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place css files under the 'formatting' folder
	private static boolean INITIALIZE_MODE = true;

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.css.formatterFactory"; //$NON-NLS-1$
	private static String FORMATTER_ID = "com.aptana.editor.css.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "css"; //$NON-NLS-1$
	private Map<String, String> prefs;

	@Override
	public void setUp() throws Exception
	{
		IScriptFormatterFactory factory = (IScriptFormatterFactory) ScriptFormatterManager.getInstance()
				.getContributionById(FORMATTER_FACTORY_ID);

		setDefaultPreferences();
		IScriptFormatter codeFormatter = factory.createFormatter(System.getProperty("line.separator"), prefs); //$NON-NLS-1$
		setFormatter(codeFormatter, FORMATTER_ID);
		
		if (INITIALIZE_MODE)
		{
			generateFormattedFiles(FILE_TYPE);
			INITIALIZE_MODE = false;
		}
		
		super.setUp();
	}

	@Override
	protected boolean compareWithWhiteSpace(String original, String formattedText)
	{
		return original.equals(formattedText);
	}

	private void setDefaultPreferences()
	{
		prefs = new HashMap<String, String>();

		prefs.put(CSSFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
		prefs.put(CSSFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.WRAP_COMMENTS, "false"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.WRAP_COMMENTS_LENGTH, "80"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS, CodeFormatterConstants.SAME_LINE);
		prefs.put(CSSFormatterConstants.LINES_AFTER_ELEMENTS, "1"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.LINES_AFTER_DECLARATION, "0"); //$NON-NLS-1$
		prefs.put(CSSFormatterConstants.PRESERVED_LINES, "1"); //$NON-NLS-1$
	}

	public void testFilesInFormattingFolder() throws Exception
	{
		String[] files = getFilesToFormat(FORMATTING_FOLDER, FILE_TYPE);

		for (String file : files)
		{
			formatterTest(file, FORMATTING_PREFIX + file);
		}

		// Tests that are not working are named without the .css extension

		// Notes on Tests:
		
		/*
		 * Test 7: We are not able to parse invalid css, so this test does not work for now.
		 */

		/*
		 * Test 8: Corresponds to test 15 in studio 1.5 test. Result is different than 1.5 test since the newlines
		 * within an @media rule does not have its own setting
		 */

		/*
		 * Test 11: Studio 1.5 tests seems to move all the selectors to a new line... The current behavior matches
		 * Studio 2 behavior.
		 */

		/*
		 * Test 14: The asterisk before a declaration is not valid CSS, which results in incorrect formatting (the
		 * parser is reading * as a different declaration). We may want to make it so it does not modify declarations
		 * with incorrect syntax like that
		 */
	}

}
