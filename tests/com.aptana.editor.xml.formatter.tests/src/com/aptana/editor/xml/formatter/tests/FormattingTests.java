package com.aptana.editor.xml.formatter.tests;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.common.formatting.FormatterTestFile;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place xml files under the 'formatting' folder and run these tests from the
	// com.aptana.editor.xml.formatter.tests plugin
	// NOTE: Ensure that the contents section ends with a newline, or the generation may not work.
	private static boolean INITIALIZE_MODE = false;

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.xml.formatterFactory"; //$NON-NLS-1$
	private static String FORMATTER_ID = "com.aptana.editor.xml.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "xml"; //$NON-NLS-1$

	@Override
	protected void setUp() throws Exception
	{
		factory = (IScriptFormatterFactory) ScriptFormatterManager.getInstance().getContributionById(
				FORMATTER_FACTORY_ID);

		super.setUp();
	}

	@Override
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

		// Tests that are not working no longer have the xml extension

		// Notes on Tests:

		/*
		 * We currently indent attributes of a tag with the same indentation as the whole tag. (Studio 1.5 does this
		 * differently) If we want to change this, we would need to modify how the writer writes the indentation. test2,
		 * test4, test5, test6, test7, test8, test15 , test19
		 */

		/*
		 * Test 12: Spaces inside tags need to work
		 */

		/*
		 * Test 16: This doesn't pass since we don't handle invalid xml.
		 */

		/*
		 * Test 17, 18: This does not work since comments aren't working yet in xml.
		 */

	}

}
