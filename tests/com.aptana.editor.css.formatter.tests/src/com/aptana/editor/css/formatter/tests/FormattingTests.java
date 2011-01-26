package com.aptana.editor.css.formatter.tests;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.common.formatting.FormatterTestFile;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place css files under the 'formatting' folder and run these tests from the
	// com.aptana.editor.css.formatter.tests plugin
	// NOTE: Ensure that the contents section ends with a newline, or the generation may not work.
	private static boolean INITIALIZE_MODE = false;

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.css.formatterFactory"; //$NON-NLS-1$
	private static String FORMATTER_ID = "com.aptana.editor.css.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "css"; //$NON-NLS-1$

	@Override
	public void setUp() throws Exception
	{
		factory = (IScriptFormatterFactory) ScriptFormatterManager.getInstance().getContributionById(
				FORMATTER_FACTORY_ID);

		super.setUp();
	}

	protected boolean compareWithWhiteSpace(String original, String formattedText)
	{
		return original.equals(formattedText);
	}

	public void testFilesInFormattingFolder() throws Exception
	{
		String[] files = getFiles(FORMATTING_FOLDER, FILE_TYPE, FORMATTER_ID);

		for (String filename : files)
		{
			FormatterTestFile file = new FormatterTestFile(factory, FORMATTER_ID, filename, FORMATTING_FOLDER);
			if (INITIALIZE_MODE)
			{
				file.generateFormattedContent();
			}

			formatterTest(file, filename, FILE_TYPE);
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
