package com.aptana.editor.html.formatter.tests;

import com.aptana.editor.common.formatting.AbstractFormatterTestCase;
import com.aptana.editor.common.formatting.FormatterTestFile;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;

public class FormattingTests extends AbstractFormatterTestCase
{
	// Turn this flag on for development only (used to generate the formatted files)
	// To generate formatted files, place html files under the 'formatting' folder and run these tests from the
	// com.aptana.editor.html.formatter.tests plugin
	// NOTE: Ensure that the contents section ends with a newline, or the generation may not work.
	private static boolean INITIALIZE_MODE = false;

	private static String FORMATTER_FACTORY_ID = "com.aptana.editor.html.formatterFactory"; //$NON-NLS-1$
	private static String FORMATTER_ID = "com.aptana.editor.html.formatter.tests"; //$NON-NLS-1$
	private static String FILE_TYPE = "html"; //$NON-NLS-1$

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
			
			/*
			 * Test 13, 21: We currently don't support <pre><pre>x</pre></pre> being on the same line (test still passes with current output)
			 */
			
			/*
			 * Test 17: The contents of the file under the formatted section looks incorrect, but it works fine in the editor (and the test passes)
			 */
			
			/*
			 * Test 3, 24: Comments do not format the same way as it did in studio 1.5
			 */
			
			/*
			 * Test 26: We currently don't support tags like "<![if ltIE5]>"
			 */
			
		}

	}

}
